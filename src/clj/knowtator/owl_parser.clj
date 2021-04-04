(ns knowtator.owl-parser
  (:require [tawny.owl :as to]
            [tawny.query :as tq]
            [clojure.java.io :as io]))

(defn load-ontology [f]
  (.loadOntologyFromOntologyDocument (to/owl-ontology-manager) (to/iri f)))

(defn into-map [c]
  (-> c
    tq/into-map
    (update :annotation
      (fn [annotations]
        (->> annotations
          (mapv (fn [[type & vs]]
                  (->> [:type type]
                    (conj vs)
                    (reduce
                      (fn [m [id & vs]]
                        (let [vs (cond (= id :literal)    (apply hash-map (conj vs :value))
                                       (empty? (rest vs)) (first vs)
                                       :else              vs)]
                          (assoc m id vs)))
                      {})))))))))

(defn parse-ontology [ontology]
  (let [owl-groups {:obj-props   tq/obj-props
                    :classes     tq/classes
                    :ann-props   tq/ann-props
                    :data-props  tq/data-props
                    :individuals tq/individuals}]
    (->> owl-groups
      (map (fn [[k f]] [k (->> ontology
                           f
                           (map into-map))]))
      (into {:ontology (into-map ontology)}))))
