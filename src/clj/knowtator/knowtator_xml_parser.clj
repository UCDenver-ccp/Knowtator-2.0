(ns knowtator.knowtator-xml-parser
  (:require [clojure.java.io :as io]
            [clojure.walk :as walk]
            [clojure.xml :as xml]
            [meander.epsilon :as m]
            [clojure.string :as str]
            [knowtator.util :as util]))

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
        (mapcat #(m/rewrites %
                   {:tag     :knowtator-project
                    :content (m/scan {:tag   :document
                                      :attrs {:id        ?doc
                                              :text-file ?file-name}})}
                   {:id        ?doc
                    :file-name ?file-name}))
        (util/map-with-key :id)
        (merge-with (partial merge-with (comp (partial some identity) vector)) articles)
        vals))))

(defn parse-annotations [xmls]
  (->> xmls
    (mapcat #(m/rewrites %
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
                                                                  :label ?concept-label}
                                                          :as    ?other})})})}
               {:id      ?ann
                :doc     ?doc
                :profile ?profile
                :concept ?concept-label}))
    set))

(defn parse-profiles [xmls]
  (->> xmls
    (mapcat #(m/rewrites %
               {:tag     :knowtator-project
                :content (m/scan
                           {:tag     :profile
                            :attrs   {:id ?id}
                            :content [{:tag   :highlighter
                                       :attrs {:class !concept
                                               :color !color}}
                                      ...]})}
               {:id     (m/app keyword ?id)
                :colors (m/app (partial apply hash-map) [!concept !color ...])}))))

(defn parse-span [verify-id xml]
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
    {:id    (m/app verify-id ?id)
     :ann   ?ann
     :start (m/app #(Integer/parseInt %) ?start)
     :end   (m/app #(Integer/parseInt %) ?end)}))

(defn parse-spans [xmls]
  (let [id-idx (atom 0)]
    (letfn [(verify-id [id]
              (or id
                (do (swap! id-idx inc)
                    (str "span-" @id-idx))))]
      (->> xmls
        (mapcat (partial parse-span verify-id))))))

(defn parse-project [project-file]
  (let [annotation-xmls (read-project-xmls "Annotations" project-file)
        profile-xmls    (read-project-xmls "Profiles" project-file)]
    {:anns     (parse-annotations annotation-xmls)
     :docs     (parse-documents project-file annotation-xmls)
     :profiles (parse-profiles profile-xmls)
     :spans    (parse-spans annotation-xmls)
     :graphs   []}))
