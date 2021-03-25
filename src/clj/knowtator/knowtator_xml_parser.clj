(ns knowtator.knowtator-xml-parser
  (:require [clojure.java.io :as io]
            [clojure.walk :as walk]
            [clojure.xml :as xml]
            [meander.epsilon :as m]))

(defn read-annotation-files [project-file-name]
  (letfn [(struct->map [x]
            (cond->> x
              (instance? clojure.lang.PersistentStructMap x)
              (into {})))]
    (-> project-file-name
      (io/file "Annotations")
      str
      io/resource
      io/file
      file-seq
      rest
      (->>
        (map xml/parse)
        (map (partial walk/postwalk struct->map))))))

(defn parse-documents [xmls]
  (->> xmls
    (mapcat #(m/rewrites %
               {:tag     :knowtator-project
                :attrs   nil
                :content (m/scan {:tag   :document
                                  :attrs {:id        ?doc
                                          :text-file ?file-name}})}
               {:id        ?doc
                :file-name ?file-name}))))

(defn parse-annotations [xmls]
  (->> xmls
    (mapcat #(m/rewrites %
               {:tag     :knowtator-project
                :attrs   nil
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
                :concept ?concept-label}))))
