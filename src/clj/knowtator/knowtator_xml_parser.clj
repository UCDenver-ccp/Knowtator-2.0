(ns knowtator.knowtator-xml-parser
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [clojure.xml :as xml]
            [knowtator.util :as util]
            [meander.epsilon :as m :refer [defsyntax]]))

(declare
  !id
  !concepts
  !concept
  !colors
  !vs
  !es
  !ns
  !ts
  !fs
  !as
  !ann
  !span
  ?ann !spans
  !doc
  !file-name
  !file-name-id
  !profile
  !start
  !end
  !content
  !concept-label)

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
            :annotation ~node-ann}})

(defsyntax relation-annotation [{:keys [edge from to counter]
                                 :or   {edge    '_
                                        from    '_
                                        to      '_
                                        counter '(atom -1)}}]
  `{:tag   :triple
    :attrs {:id      (m/app (partial verify-id ~counter "edge-") ~edge)
            :subject ~from
            :object  ~to}})

(defsyntax graph-space [{:keys [graph counter] :as args
                         :or   {graph   '_
                                counter '(atom -1)}}]
  `{:tag     :graph-space
    :attrs   {:id (m/app (partial verify-id ~counter "graph-space-") ~graph)}
    :content [(m/or
                (annotation-node ~args)
                (relation-annotation ~args))
              ...]})
(defsyntax annotation [{:keys [ann counter profile] :as args
                        :or   {ann     '_
                               profile '_
                               counter '(atom -1)}}]
  `{:tag     :annotation
    :attrs   {:id        (m/app (partial verify-id ~counter "annotation-") ~ann)
              :annotator ~profile}
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


#_(defsyntax annotation [{:keys [ann profile counter] :as args
                          :or   {ann     '_
                                 profile '_
                                 counter '(atom -1)}}]
    `{:tag     :annotation
      :attrs   {:id        (m/app (partial verify-id ~counter "annotation-") ~ann)
                :annotator ~profile}
      :content (m/scan (m/or
                         (concept ~args)
                         (span ~args)))})

(defsyntax document [{:keys [doc file-name counter file-name-id] :as args
                      :or   {doc          '_
                             file-name    '_
                             file-name-id '_
                             counter      '(atom -1)}}]
  `{:tag     :document
    :attrs   {:id        (m/app (partial verify-id ~counter "document-") (m/and ~doc ~file-name-id))
              :text-file ~file-name}
    :content (m/or (m/pred empty?)
               (m/scan (m/or
                         (annotation ~args)
                         (graph-space ~args))))})

(defsyntax knowtator-project [args]
  `{:tag     :knowtator-project
    :content (m/scan (m/or
                       (document ~args)
                       (profile ~args)))})

(defn parse-profiles [counter xml]
  (-> xml
    (m/rewrites
      (knowtator-project {:profile  !id
                          :concepts !concepts
                          :colors   !colors
                          :counter  counter})
      [{:id     !id
        :colors (m/map-of !concepts !colors)}
       ...])
    (->> (apply concat))))

(defn parse-graph-spaces [counter xml]
  (-> xml
    (m/rewrites
      (knowtator-project {:doc      !doc
                          :graph    !id
                          :node     !vs
                          :node-ann !as
                          :edge     !es
                          :from     !fs
                          :to       !ts
                          :counter  counter})
      [{:id    !id
        :doc   (m/app keyword !doc)
        :nodes [{:id  !vs
                 :ann (m/app keyword !as)}
                ...]
        :edges [(m/app (fn [{:keys [from to] :as e}]
                         (let [vs (set (map keyword !vs))]
                           (when (and (vs from) (vs to))
                             e)))
                  {:id   !es
                   :from (m/app keyword !fs)
                   :to   (m/app keyword !ts)})
                ...]}
       ...])
    (->>
      (apply concat)
      (map #(update % :edges (partial remove nil?))))))

(defn parse-annotations [counter xml]
  (-> xml
    (m/rewrites
      (knowtator-project {:doc     !doc
                          :profile !profile
                          :ann     !id
                          :span    !span
                          :start   !start
                          :end     !end
                          :concept !concept
                          :counter counter})
      [{:id      !id
        :profile (m/app #(or (keyword %) :Default) !profile)
        :doc     (m/app keyword !doc)
        :concept !concept
        :spans   [{:id    !span
                   :start !start
                   :end   !end}
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
        (knowtator-project {:doc          !id
                            :file-name    !file-name
                            :file-name-id !file-name-id
                            :counter      counter})
        [{:id        !id
          :file-name (m/app (fn [[id file-name]]
                              (or file-name (str (str (name id)) ".txt")))
                       [!file-name-id !file-name])}
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
    {:anns     (map #(dissoc % :spans) anns)
     :docs     (parse-documents counter articles xml)
     :profiles (parse-profiles counter xml)
     :spans    (parse-spans anns)
     :graphs   (parse-graph-spaces counter xml)}))
