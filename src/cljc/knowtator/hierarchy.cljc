(ns knowtator.hierarchy
  (:require
   [clojure.zip :as zip]))

(defn roots [h]
  (->> h
    :descendants
    keys
    (remove #(contains? (:parents h) %))))

(defn direct-descendants [h tag]
  (->> tag
    (descendants h)
    (filter #(contains? (parents h %) tag))))

(defn hierarchy-zippers [h]
  (->> h
    roots
    (map (partial zip/zipper
           (partial descendants h)
           (partial direct-descendants h)
           second))))

(defn right-nodes [loc]
  (->> loc
    (iterate zip/right)
    (take-while seq)))

(defn child-nodes [loc]
  (->> loc
    zip/down
    right-nodes))
