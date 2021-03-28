(ns knowtator.knowtator-xml-parser
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [clojure.xml :as xml]
            [knowtator.util :as util]
            [meander.epsilon :as m :refer [defsyntax]]))

(declare
  ?id
  ?concept
  ?concepts
  !concepts
  !colors
  !vs
  !es
  !ns
  !ts
  !fs
  !as
  ?ann
  ?doc
  ?file-name
  ?profile
  ?start
  ?end
  ?content
  ?concept-label)

(defn verify-id [counter prefix id]
  (keyword (or id
             (do (swap! counter inc)
                 (str prefix @counter)))))

(defn read-project-xmls [dir project-file-name]
  (letfn [(struct->map [x]
            (cond->> x
              (instance? clojure.lang.PersistentStructMap x)
              (into {})))]
    (-> project-file-name
      (io/file dir)
      file-seq
      rest
      (->>
        (map xml/parse)
        (map (partial walk/postwalk struct->map))))))

(defsyntax concept-color [concepts-pattern colors-pattern]
  `{:tag   :highlighter
    :attrs {:class ~concepts-pattern
            :color ~colors-pattern}})

(defsyntax profile [id-pattern concepts-pattern colors-pattern]
  `{:tag     :profile
    :attrs   {:id ~id-pattern}
    :content [(concept-color ~concepts-pattern ~colors-pattern) ...]})

(defn parse-profile [counter xml]
  (m/rewrites xml
    {:tag     :knowtator-project
     :content (m/scan (profile ?id !concepts !colors))}
    {:id     (m/app (partial verify-id counter "profile-") ?id)
     :colors (m/app (partial apply hash-map) [!concepts !colors ...])}))



(defsyntax concept [concept-pattern concept-label-pattern]
  `{:tag   :class
    :attrs {:id    ~concept-pattern
            :label ~concept-label-pattern}})



(defsyntax annotation-node [vertex-pattern annotation-pattern]
  `{:tag   :vertex
    :attrs {:id         ~vertex-pattern
            :annotation ~annotation-pattern}})

(defsyntax relation-annotation [edge-pattern from-pattern to-pattern]
  `{:tag   :triple
    :attrs {:id      ~edge-pattern
            :subject ~from-pattern
            :object  ~to-pattern}})

(defsyntax graph-space [graph-space-pattern
                        vertex-pattern annotation-pattern
                        edge-pattern from-pattern to-pattern]
  `{:tag     :graph-space
    :attrs   {:id ~graph-space-pattern}
    :content [(m/or
                (annotation-node ~vertex-pattern ~annotation-pattern)
                (relation-annotation ~edge-pattern ~from-pattern ~to-pattern))
              ...]})

(defn parse-graph-space [counter xml]
  (-> xml
    (m/rewrites
      {:tag     :knowtator-project
       :content (m/scan {:tag     :document
                         :attrs   {:id ?doc}
                         :content (m/scan (graph-space ?id !vs !as !es !fs !ts))})}
      {:id    (m/app (partial verify-id counter "graph-space-") ?id)
       :doc   (m/app keyword ?doc)
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
               ...]})
    (->> (map #(update % :edges (partial remove nil?))))))

(defsyntax span [span-pattern start-pattern end-pattern]
  `{:tag   :span
    :attrs {:id    ~span-pattern
            :start (m/app #(Integer/parseInt %) ~start-pattern)
            :end   (m/app #(Integer/parseInt %) ~end-pattern)}})

(defsyntax annotation [{id-pattern            :ann
                        profile-pattern       :profile
                        concept-pattern       :concept
                        concept-label-pattern :concept-label
                        span-pattern          :span
                        start-pattern         :start
                        end-pattern           :end
                        :or
                        {id-pattern            '_
                         profile-pattern       '_
                         concept-pattern       '_
                         concept-label-pattern '_
                         span-pattern          '_
                         start-pattern         '_
                         end-pattern           '_}}]
  `{:tag     :annotation
    :attrs   {:id        ~id-pattern
              :annotator ~profile-pattern}
    :content [(m/or
                (concept ~concept-pattern ~concept-label-pattern)
                (span ~span-pattern ~start-pattern ~end-pattern))
              ...]})

(defsyntax document
  [{id-pattern              :id
    file-name-pattern       :file-name
    graph-space-pattern     :graph
    vertex-pattern          :node
    node-annotation-pattern :node-ann
    edge-pattern            :edge
    from-pattern            :from
    to-pattern              :to
    :or
    {id-pattern              '_
     file-name-pattern       '_
     graph-space-pattern     '_
     vertex-pattern          '_
     node-annotation-pattern '_
     edge-pattern            '_
     from-pattern            '_
     to-pattern              '_}
    :as                     args}]
  `{:tag     :document
    :attrs   {:id        ~id-pattern
              :text-file ~file-name-pattern}
    :content [(m/or
                (annotation ~args)
                (graph-space ~graph-space-pattern
                  ~vertex-pattern ~node-annotation-pattern
                  ~edge-pattern ~from-pattern ~to-pattern))
              ...]})

(m/rewrite {:tag     :document
            :attrs   {:id "d1"}
            :content []}
  (document {:id ?id})
  ?id)

(defn parse-document [counter xml]
  (m/rewrites xml
    {:tag     :knowtator-project
     :content (m/scan (document {:id        ?id
                                 :file-name ?file-name}))}
    {:id        (m/app (partial verify-id counter "document-") ?id)
     :file-name (m/app #(or % (str ?id ".txt")) ?file-name)}))

(defn parse-annotation [counter xml]
  (m/rewrite xml
    {:tag     :knowtator-project
     :content (m/scan
                (document {:id            ?doc
                           :ann           !id
                           :profile       !profile
                           :concept       !concept
                           :concept-label !concept-label}))}
    [{:id      (m/app (partial verify-id counter "annotation-") !id)
      :doc     (m/app keyword ?doc)
      :profile (m/app #(or (keyword %) :Default) !profile)
      :concept !concept}
     ...]))

(defn parse-span [counter xml]
  (mapcat identity
    (m/rewrites xml
      {:tag     :knowtator-project
       :content (m/scan
                  {:tag     :document
                   :content (m/scan (annotation {:ann   ?ann
                                                 :span  !id
                                                 :start !start
                                                 :end   !end}))})}
      [(m/app (fn [[id ann start end]]
                {:id    (verify-id counter "span-" id)
                 :ann   (keyword ann)
                 :start (min start end)
                 :end   (max start end)})
         [!id ?ann !start !end])
       ...])))

(defn parse-documents [project-file xmls]
  (letfn [(file-name [f]
            (str (.getName f)))
          (file-name->id [f]
            (-> f
              file-name
              (str/replace-first #"\.txt$" "")
              keyword))]
    (let [articles (-> project-file
                     (io/file "Articles")
                     file-seq
                     rest
                     (->>
                       (map (juxt file-name file-name->id slurp))
                       (map (partial zipmap [:file-name :id :content]))
                       (util/map-with-key :id)))]
      (->> xmls
        (mapcat (partial parse-document (atom 0)))
        (util/map-with-key :id)
        (merge-with (partial merge-with (comp (partial some identity) vector)) articles)
        vals))))

(defn parse-annotations [xmls]
  (->> xmls
    (mapcat (partial parse-annotation (atom 0)))
    set))

(defn parse-profiles [xmls]
  (->> xmls
    (mapcat (partial parse-profile (atom 0)))))

(defn parse-spans [xmls]
  (let [counter              (atom 0)
        spans                (->> xmls
                               (mapcat (partial parse-span counter)))
        unique-content-spans (->> spans
                               (group-by (juxt :ann :start :end))
                               vals
                               (map first))]
    unique-content-spans))

(defn parse-graph-spaces [xmls]
  (->> xmls
    (mapcat (partial parse-graph-space (atom 0)))))

(defn parse-project [project-file]
  (let [annotation-xmls (read-project-xmls "Annotations" project-file)
        profile-xmls    (read-project-xmls "Profiles" project-file)
        annotation-xmls (apply merge-with (fn [x y]
                                            (if (and (coll? x) (coll? y))
                                              (into x y)
                                              y))
                          annotation-xmls)]
    {:anns     (parse-annotations annotation-xmls)
     :docs     (parse-documents project-file annotation-xmls)
     :profiles (parse-profiles profile-xmls)
     :spans    (parse-spans annotation-xmls)
     :graphs   (parse-graph-spaces annotation-xmls)}))
