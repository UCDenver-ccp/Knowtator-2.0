(ns knowtator.knowtator-xml-parser
  (:require [clojure.java.io :as io]
            [clojure.walk :as walk]
            [clojure.xml :as xml]
            [meander.epsilon :as m]
            [clojure.string :as str]
            [knowtator.util :as util]))


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

(defn parse-document [xml]
  (m/rewrites xml
    {:tag     :knowtator-project
     :content (m/scan {:tag   :document
                       :attrs {:id        ?doc
                               :text-file ?file-name}})}
    {:id        ?doc
     :file-name ?file-name}))

(defn parse-annotation [xml]
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
    {:id      ?ann
     :doc     ?doc
     :profile ?profile
     :concept ?concept-label}))

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
     :ann   ?ann
     :start (m/app #(Integer/parseInt %) ?start)
     :end   (m/app #(Integer/parseInt %) ?end)}))

(defn parse-documents [project-file xmls]
  (letfn [(file-name [f]
            (str (.getName f)))
          (file-name->id [f]
            (-> f
              file-name
              (str/replace-first #"\.txt$" "")))]
    (let [articles (-> project-file
                     (io/file "Articles")
                     file-seq
                     rest
                     (->>
                       (map (juxt file-name file-name->id slurp))
                       (map (partial zipmap [:file-name :id :content]))
                       (util/map-with-key :id)))]
      (->> xmls
        (mapcat parse-document)
        (util/map-with-key :id)
        (merge-with (partial merge-with (comp (partial some identity) vector)) articles)
        vals))))


(defn parse-annotations [xmls]
  (->> xmls
    (mapcat parse-annotation)
    set))

(defn parse-profiles [xmls]
  (->> xmls
    (mapcat (partial parse-profile (atom 0)))))

(defn parse-spans [xmls]
  (let [counter (atom 0)]
    (->> xmls
      (mapcat (partial parse-span counter)))))

(defn parse-project [project-file]
  (let [annotation-xmls (read-project-xmls "Annotations" project-file)
        profile-xmls    (read-project-xmls "Profiles" project-file)]
    {:anns     (parse-annotations annotation-xmls)
     :docs     (parse-documents project-file annotation-xmls)
     :profiles (parse-profiles profile-xmls)
     :spans    (parse-spans annotation-xmls)
     :graphs   []}))
