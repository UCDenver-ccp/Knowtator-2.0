(ns knowtator.knowtator-xml-parser
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [clojure.xml :as xml]
            [knowtator.util :as util]
            [meander.epsilon :as m]))

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

(defn parse-profile [counter xml]
  (m/rewrites xml
    {:tag     :knowtator-project
     :content (m/scan
                {:tag     :profile
                 :attrs   {:id ?id}
                 :content [{:tag   :highlighter
                            :attrs {:class !concepts
                                    :color !colors}}
                           ...]})}
    {:id     (m/app (partial verify-id counter "profile-") ?id)
     :colors (m/app (partial apply hash-map) [!concepts !colors ...])}))

(defn parse-document [counter xml]
  (m/rewrites xml
    {:tag     :knowtator-project
     :content (m/scan {:tag   :document
                       :attrs {:id        ?doc
                               :text-file ?file-name}})}
    {:id        (m/app (partial verify-id counter "document-") ?doc)
     :file-name (m/app #(or % (str ?doc ".txt")) ?file-name)}))

(defn parse-annotation [counter xml]
  (m/rewrites xml
    {:tag     :knowtator-project
     :content (m/scan {:tag     :document
                       :attrs   {:id ?doc}
                       :content (m/scan
                                  {:tag     :annotation
                                   :attrs   {:id        ?ann
                                             :annotator ?profile}
                                   :content (m/scan
                                              {:tag   :class
                                               :attrs {:id    ?concept
                                                       :label ?concept-label}})})})}
    {:id      (m/app (partial verify-id counter "annotation-") ?ann)
     :doc     (m/app keyword ?doc)
     :profile (m/app #(or (keyword %) :Default) ?profile)
     :concept ?concept}))

(defn parse-graph-space [counter xml]
  (-> xml
    (m/rewrites
      {:tag     :knowtator-project
       :content (m/scan {:tag     :document
                         :attrs   {:id ?doc}
                         :content (m/scan
                                    {:tag     :graph-space
                                     :attrs   {:id ?id}
                                     :content [(m/or
                                                 {:tag   :vertex
                                                  :attrs {:id         !vs
                                                          :annotation !as}}
                                                 {:tag   :triple
                                                  :attrs {:id      !es
                                                          :subject !ts
                                                          :object  !fs }})
                                               ...]})})}
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


(defn parse-span [counter xml]
  (m/rewrites xml
    {:tag     :knowtator-project
     :content (m/scan
                {:tag     :document
                 :content (m/scan
                            {:tag     :annotation
                             :attrs   {:id ?ann}
                             :content (m/scan
                                        {:tag     :span
                                         :attrs   {:id    ?id
                                                   :start ?start
                                                   :end   ?end}
                                         :content ?content})})})}
    {:id    (m/app (partial verify-id counter "span-") ?id)
     :ann   (m/app keyword ?ann)
     :start (m/app #(let [start (Integer/parseInt %)
                          end   (Integer/parseInt ?end)]
                      (if (< start end)
                        start
                        end))
              ?start)
     :end   (m/app #(let [start (Integer/parseInt ?start)
                          end   (Integer/parseInt %)]
                      (if (< start end)
                        end
                        start))
              ?end)}))

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
        profile-xmls    (read-project-xmls "Profiles" project-file)]
    {:anns     (parse-annotations annotation-xmls)
     :docs     (parse-documents project-file annotation-xmls)
     :profiles (parse-profiles profile-xmls)
     :spans    (parse-spans annotation-xmls)
     :graphs   (parse-graph-spaces annotation-xmls)}))
