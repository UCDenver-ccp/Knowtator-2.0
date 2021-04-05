(ns knowtator.owl-parser
  (:require [tawny.owl :as to]
            [tawny.query :as tq]
            [tawny.protocol :as tp]))

(defn load-ontology [f]
  (.loadOntologyFromOntologyDocument (to/owl-ontology-manager) (to/iri f)))

(defn collapse-data [[type & vs]]
  (cond
    (#{:annotation :label :comment} type)      (->> [:type type]
                                                  (conj vs)
                                                  (reduce
                                                    (fn [m [id & vs]]
                                                      (let [vs (cond (= :literal id)    (apply hash-map (conj vs :value))
                                                                     (empty? (rest vs)) (first vs)
                                                                     :else              vs)]
                                                        (assoc m id vs)))
                                                    {}))
    (#{:iri} type)                             (first vs)
    (#{:some :and :not :or :only :oneof} type) {:type type
                                                :data (->> vs
                                                        (map collapse-data)
                                                        set)}
    (#{:has-value} type)                       {:type type
                                                :data (->> vs
                                                        (map collapse-data)
                                                        vec)}
    (#{:at-least} type)                        {:type  type
                                                :value (first vs)
                                                :data  (->> vs
                                                         rest
                                                         (map collapse-data)
                                                         set)}
    :else                                      [type vs]))

(defn into-map [c ks]
  (reduce (fn [m k]
            (cond-> m (contains? m k)
                    (update k (partial mapv collapse-data))))
    (-> c
      tq/into-map
      (assoc :iri (-> c
                    tp/as-iri
                    str))
      (update :type first))
    ks))

(defn parse-ontology [ontology]
  (let [owl-groups {:obj-props   tq/obj-props
                    :classes     tq/classes
                    :ann-props   tq/ann-props
                    :data-props  tq/data-props
                    :individuals tq/individuals}]
    (->> owl-groups
      (map (fn [[k f]] [k (->> ontology
                           f
                           (map #(into-map % [:annotation :disjoint :super :equivalent])))]))
      (into {:ontology (into-map ontology [:annotation])}))))
