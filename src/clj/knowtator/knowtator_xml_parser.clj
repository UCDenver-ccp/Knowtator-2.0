(ns knowtator.knowtator-xml-parser
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [clojure.xml :as xml]
            [knowtator.util :as util]
            [meander.epsilon :as m :refer [defsyntax]]))

(declare
  !quants
  !quant-vals
  !properties
  !polarities
  !ids
  !graphs
  !concepts
  !concepts
  !colors
  !concept-ann-nodes
  !assertion-anns
  !tos
  !froms
  !node-concept-anns
  !concept-anns
  !spans
  ?concept-ann
  !docs
  !file-names
  !file-name-ids
  !profiles
  !starts
  !ends
  !contents
  !concept-labels)

(defn verify-id [counter prefix id]
  (if (keyword? id)
    id
    (keyword (or id
               (do (swap! counter inc)
                   (str prefix @counter))))))

(defn read-project-xml-dir [dir project-file-name]
  (letfn [(struct->map [x]
            (cond->> x
              (instance? clojure.lang.PersistentStructMap x)
              (into {})))]
    (->> dir
      (io/file project-file-name)
      file-seq
      rest
      (filter (comp #(str/ends-with? % ".xml") str))
      (map xml/parse)
      (map (partial walk/postwalk struct->map))
      (apply merge-with (fn [x y]
                          (if (and (coll? x) (coll? y))
                            (into x y)
                            y))))))


(defn read-project-xmls [project-file-name]
  (let [profile-xml    (read-project-xml-dir "Profiles" project-file-name)
        annotation-xml (read-project-xml-dir "Annotations" project-file-name)]
    (merge-with (fn [x y]
                  (if (and (coll? x) (coll? y))
                    (into x y)
                    y))
      profile-xml annotation-xml)))

(defsyntax concept-color [{:keys [concepts colors]
                           :or   {concepts '_
                                  colors   '_}}]
  `{:tag   :highlighter
    :attrs {:class ~concepts
            :color ~colors}})

(defsyntax profile [{:keys [profile counter] :as args
                     :or   {profile '_
                            counter '(atom -1)}}]
  `{:tag     :profile
    :attrs   {:id (m/app (partial verify-id ~counter "profile-") ~profile)}
    :content [(concept-color ~args) ...]})

(defsyntax annotation-node [{:keys [node node-ann counter]
                             :or   {node     '_
                                    node-ann '_
                                    counter  '(atom -1)}}]
  `{:tag   :vertex
    :attrs {:id         (m/app (partial verify-id ~counter "node-") ~node)
            :annotation (m/app keyword ~node-ann)}})

(defsyntax relation-annotation [{:keys [edge from to property counter ra-motivation ra-annotator polarity quantifier quantifier-value]
                                 :or   {edge             '_
                                        from             '_
                                        to               '_
                                        ra-motivation    '_
                                        ra-annotator     '_
                                        property         '_
                                        quantifier       '_
                                        quantifier-value '_
                                        counter          '(atom -1)
                                        polarity         '_}}]
  `{:tag   :triple
    :attrs {:id         (m/app (partial verify-id ~counter "edge-") ~edge)
            :subject    ~from
            :object     ~to
            :property   ~property
            :motivation ~ra-motivation
            :annotator  ~ra-annotator
            :polarity   (m/app keyword ~polarity)
            :quantifier (m/app #(if (empty? %) :some (keyword %)) ~quantifier)
            :value      (m/app #(when ((complement empty?) %) (Integer/parseInt %)) ~quantifier-value)}})

(defsyntax graph-space [{:keys [graph counter] :as args
                         :or   {graph   '_
                                counter '(atom -1)}}]
  `{:tag     :graph-space
    :attrs   {:id (m/app (partial verify-id ~counter "graph-space-") ~graph)}
    :content [(m/or
                (annotation-node ~args)
                (relation-annotation ~args))
              ...]})

(defsyntax annotation [{:keys [ann counter profile ca-motivation ann-type] :as args
                        :or   {ann           '_
                               profile       '_
                               ann-type      '_
                               ca-motivation '_
                               counter       '(atom -1)}}]
  `{:tag     :annotation
    :attrs   {:id         (m/app (partial verify-id ~counter "annotation-") ~ann)
              :motivation ~ca-motivation
              :type       ~ann-type
              :annotator  ~profile}
    :content [(m/or
                (span ~args)
                (concept ~args))
              ...]})

(defsyntax concept [{:keys [concept concept-label]
                     :or   {concept       '_
                            concept-label '_}}]
  `{:tag   :class
    :attrs {:id    ~concept
            :label ~concept-label}})

(defsyntax span [{:keys [span start end counter]
                  :or   {span    '_
                         start   '_
                         end     '_
                         counter '(atom -1)}}]
  `{:tag   :span
    :attrs {:id    (m/app (partial verify-id ~counter "span-") ~span)
            :start (m/app #(Integer/parseInt %) ~start)
            :end   (m/app #(Integer/parseInt %) ~end)}})

(defsyntax document [{:keys [doc file-name counter file-name-id graph] :as args
                      :or   {doc          '_
                             file-name    '_
                             file-name-id '_
                             graph        '_
                             counter      '(atom -1)}}]
  `{:tag     :document
    :attrs   {:id        (m/app (partial verify-id ~counter "document-") (m/and ~doc ~file-name-id))
              :text-file ~file-name}
    :content (m/or (m/pred empty?)
               (m/scan (m/or
                         (annotation ~args)
                         {:tag :graph-space :as ~graph})))})

(defsyntax knowtator-project [args]
  `{:tag     :knowtator-project
    :content (m/scan (m/or
                       (document ~args)
                       (profile ~args)))})

(defn parse-profiles [counter xml]
  (-> xml
    (m/rewrites
      (knowtator-project {:profile  !ids
                          :concepts !concepts
                          :colors   !colors
                          :counter  counter})
      [{:id     !ids
        :colors (m/map-of !concepts !colors)}
       ...])
    (->> (apply concat))))




(defn parse-graph-spaces [counter xml]
  (-> xml
    (m/rewrites
      (knowtator-project {:doc      !docs
                          :graph    !graphs
                          :node     !concept-ann-nodes
                          :node-ann !node-concept-anns
                          :counter  counter})
      [{:doc   !docs
        :graph !graphs}
       ...])
    (->> (apply concat))
    (m/rewrites
      (m/scan {:doc   !docs
               :graph (graph-space {:graph            !ids
                                    :node             !concept-ann-nodes
                                    :node-ann         !node-concept-anns
                                    :edge             !assertion-anns
                                    :from             !froms
                                    :to               !tos
                                    :property         !properties
                                    :quantifier       !quants
                                    :quantifier-value !quant-vals
                                    :polarity         !polarities
                                    :counter          counter})})
      [{:id    !ids
        :doc   !docs
        :nodes [{:id  !concept-ann-nodes
                 :ann !node-concept-anns}
                ...]
        :edges [(m/app (fn [{:keys [from to] :as e}]
                         (let [vs (set (map keyword !concept-ann-nodes))]
                           (when (and (vs from) (vs to))
                             e)))
                  {:id    !assertion-anns
                   :from  (m/app keyword !froms)
                   :to    (m/app keyword !tos)
                   :value {:property   !properties
                           :polarity   !polarities
                           :quantifier {:type  !quants
                                        :value !quant-vals}}})
                ...]}
       ...])
    (->>
      (apply concat)
      (map #(update % :edges (partial remove nil?))))))

(defn parse-annotations [counter xml]
  (-> xml
    (m/rewrites
      (knowtator-project {:doc     !docs
                          :profile !profiles
                          :ann     !ids
                          :span    !spans
                          :start   !starts
                          :end     !ends
                          :concept !concepts
                          :counter counter})
      [{:id      !ids
        :profile (m/app #(or (keyword %) :Default) !profiles)
        :doc     (m/app keyword !docs)
        :concept !concepts
        :spans   [{:id    !spans
                   :start !starts
                   :end   !ends}
                  ...]}
       ...])
    (->>
      (apply concat)
      (reduce
        (fn [annotations {:keys [id] :as ann}]
          (let [new-id (verify-id counter "annotation-" (when-not (id annotations) id))]
            (cond-> annotations
              (not (some #(= (dissoc % :id) (dissoc ann :id))
                     (id annotations)))
              (update id (fnil conj #{}) (assoc ann :id new-id)))))
        {})
      vals
      (apply concat))))

(defn read-articles [project-file]
  (letfn [(file-name [f]
            (str (.getName f)))
          (file-name->id [f]
            (-> f
              file-name
              (str/replace-first #"\.txt$" "")
              keyword))]
    (->> "Articles"
      (io/file project-file)
      file-seq
      rest
      (map (juxt file-name file-name->id slurp))
      (map (partial zipmap [:file-name :id :content])))))

(defn parse-documents [counter articles xml]
  (let [articles (util/map-with-key :id articles)]
    (-> xml
      (m/rewrites
        (knowtator-project {:doc          !ids
                            :file-name    !file-names
                            :file-name-id !file-name-ids
                            :counter      counter})
        [{:id        !ids
          :file-name (m/app (fn [[id file-name]]
                              (or file-name (str (str (name id)) ".txt")))
                       [!file-name-ids !file-names])}
         ...])
      (->> (apply concat)
        (util/map-with-key :id)
        (merge-with (partial merge-with (comp (partial some identity) vector)) articles)
        vals))))

(defn parse-spans [anns]
  (letfn [(fix-range [{:keys [start end] :as span}]
            (assoc span
              :start (min start end)
              :end (max start end)))]

    (->> anns
      (mapcat (fn [{:keys [id spans]}]
                (map #(assoc % :ann id) spans)))
      (map fix-range)
      (group-by (juxt :ann :start :end))
      vals
      (map first))))

(defn parse-project [articles xml]
  (let [counter (atom 0)
        anns    (parse-annotations counter xml)]
    {:anns     (mapv #(dissoc % :spans) anns)
     :docs     (vec (parse-documents counter articles xml))
     :profiles (vec (parse-profiles counter xml))
     :spans    (vec (parse-spans anns))
     :graphs   (vec (parse-graph-spaces counter xml))}))
