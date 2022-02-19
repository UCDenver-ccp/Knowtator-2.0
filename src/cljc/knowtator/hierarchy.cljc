(ns knowtator.hierarchy
  (:require
   [clojure.zip :as zip]))

(defn roots
  [h]
  (->> h
       :descendants
       keys
       (remove #(contains? (:parents h) %))))

(defn direct-descendants
  [h tag]
  (->> tag
       (descendants h)
       (filter #(contains? (parents h %) tag))))

(defn hierarchy-zippers
  [id-fn node-fn h]
  (->> h
       roots
       (map node-fn)
       (map (partial
             zip/zipper
             (comp (partial descendants h) id-fn)
             (comp (partial map node-fn) (partial direct-descendants h) id-fn)
             second))))

(defn right-nodes
  [loc]
  (->> loc
       (iterate zip/right)
       (take-while seq)))

(defn child-nodes
  [loc]
  (->> loc
       zip/down
       right-nodes))
