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
  !doc
  !file-name
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

(defn read-project-xmls [dir project-file-name]
  (letfn [(struct->map [x]
            (cond->> x
              (instance? clojure.lang.PersistentStructMap x)
              (into {})))]
    (->> dir
      (io/file project-file-name)
      file-seq
      rest
      (map xml/parse)
      (map (partial walk/postwalk struct->map))
      (apply merge-with (fn [x y]
                          (if (and (coll? x) (coll? y))
                            (into x y)
                            y))))))

(defsyntax concept-color [{:keys [concepts colors]
                           :or   {concepts '_
                                  colors   '_}}]
  `{:tag   :highlighter
    :attrs {:class ~concepts
            :color ~colors}})

(defsyntax profile [{:keys [profile] :as args
                     :or   {profile '_}}]
  `{:tag     :profile
    :attrs   {:id ~profile}
    :content [(concept-color ~args) ...]})


(defsyntax annotation-node [{:keys [node node-ann]
                             :or   {node     '_
                                    node-ann '_}}]
  `{:tag   :vertex
    :attrs {:id         ~node
            :annotation ~node-ann}})

(defsyntax relation-annotation [{:keys [edge from to]
                                 :or   {edge '_
                                        from '_
                                        to   '_}}]
  `{:tag   :triple
    :attrs {:id      ~edge
            :subject ~from
            :object  ~to}})

(defsyntax graph-space [{:keys [graph] :as args
                         :or   {graph '_}}]
  `{:tag     :graph-space
    :attrs   {:id ~graph}
    :content [(m/or
                (annotation-node ~args)
                (relation-annotation ~args))
              ...]})

(defsyntax concept [{:keys [concept concept-label]
                     :or   {concept       '_
                            concept-label '_}}]
  `{:tag   :class
    :attrs {:id    ~concept
            :label ~concept-label}})

(defsyntax span [{:keys [span start end]
                  :or   {span  '_
                         start '_
                         end   '_}}]
  `{:tag   :span
    :attrs {:id    ~span
            :start (m/app #(Integer/parseInt %) ~start)
            :end   (m/app #(Integer/parseInt %) ~end)}})


(defsyntax annotation [{:keys [ann profile] :as args
                        :or   {ann     '_
                               profile '_}}]
  `{:tag     :annotation
    :attrs   {:id        ~ann
              :annotator ~profile}
    :content (m/scan (m/or
                       (concept ~args)
                       (span ~args)))})

(defsyntax document
  [{:keys [doc file-name] :as args
    :or   {doc       '_
           file-name '_}}]
  `{:tag     :document
    :attrs   {:id        ~doc
              :text-file ~file-name}
    :content (m/or
               (m/scan (m/or
                         (annotation ~args)
                         (graph-space ~args)))
               [])})

(defsyntax knowtator-project [args]
  `{:tag     :knowtator-project
    :content (m/scan (m/or
                       (document ~args)
                       (profile ~args)))})

(defn parse-profile [counter xml]
  (-> xml
    (m/rewrites
      (knowtator-project {:profile  !id
                          :concepts !concepts
                          :colors   !colors})
      [{:id     (m/app (partial verify-id counter "profile-") !id)
        :colors (m/app (partial apply hash-map) [!concepts !colors ...])}
       ...])
    (->> (apply concat))))

(defn parse-graph-space [counter xml]
  (-> xml
    (m/rewrites
      (knowtator-project {:doc      !doc
                          :graph    !id
                          :node     !vs
                          :node-ann !as
                          :edge     !es
                          :from     !fs
                          :to       !ts})
      [{:id    (m/app (partial verify-id counter "graph-space-") !id)
        :doc   (m/app keyword !doc)
        :nodes [{:id  (m/app keyword !vs)
                 :ann (m/app keyword !as)}
                ...]
        :edges [(m/app (fn [{:keys [from to] :as e}]
                         (let [vs (set (map keyword !vs))]
                           (when (and (vs from) (vs to))
                             e)))
                  {:id   (m/app keyword !es)
                   :from (m/app keyword !fs)
                   :to   (m/app keyword !ts)})
                ...]}
       ...])
    (->>
      (apply concat)
      (map #(update % :edges (partial remove nil?))))))

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

(defn parse-document [counter articles xml]
  (let [articles (util/map-with-key :id articles)]
    (-> xml
      (m/rewrites
        (knowtator-project {:doc       !id
                            :file-name !file-name})
        [(m/app (fn [[id file-name]]
                  {:id        (verify-id counter "document-" id)
                   :file-name (or file-name (str id ".txt"))})
           [!id !file-name])
         ...])
      (->> (apply concat)
        (util/map-with-key :id)
        (merge-with (partial merge-with (comp (partial some identity) vector)) articles)
        vals))))


(defn parse-annotation [counter xml]
  (-> xml
    (m/rewrites
      (knowtator-project {:doc           !doc
                          :ann           !id
                          :profile       !profile
                          :concept       !concept
                          :concept-label !concept-label})
      [{:id      (m/app (partial verify-id counter "annotation-") !id)
        :doc     (m/app keyword !doc)
        :profile (m/app #(or (keyword %) :Default) !profile)
        :concept !concept}
       ...])
    (->> (apply concat)
      (reduce
        (fn [annotations {:keys [id] :as ann}]
          (let [id (verify-id counter "annotation-"
                     (when-not (->> annotations
                                 id
                                 ((every-pred identity
                                    (comp (partial vector ann) (partial map #(dissoc % :id)) (partial apply not=)))))
                       id))]
            (assoc annotations id (assoc ann :id id))))
        {})
      vals)))

(defn parse-span [counter xml]
  (-> xml
    (m/rewrites
      (knowtator-project {:ann   !ann
                          :span  !id
                          :start !start
                          :end   !end})
      [(m/app (fn [[id ann start end]]
                {:id    (verify-id counter "span-" id)
                 :ann   (keyword ann)
                 :start (min start end)
                 :end   (max start end)})
         [!id !ann !start !end])
       ...])
    (->> (apply concat)
      (group-by (juxt :ann :start :end))
      vals
      (map first))))

(defn parse-project [project-file]
  (let [merged-annotation-xml (read-project-xmls "Annotations" project-file)
        profile-xmls          (read-project-xmls "Profiles" project-file)
        articles              (read-articles project-file)]
    {:anns     (parse-annotation (atom 0) merged-annotation-xml)
     :docs     (parse-document (atom 0) articles merged-annotation-xml)
     :profiles (parse-profile (atom 0) profile-xmls)
     :spans    (parse-span (atom 0) merged-annotation-xml)
     :graphs   (parse-graph-space (atom 0) merged-annotation-xml)}))
