(ns knowtator.util
  (:require [re-frame.core :as rf]))

(def <sub (comp deref rf/subscribe))
(def >evt rf/dispatch)

(defn filter-vals
  [f m]
  (->> m
    (filter #(f (second %)))
    (into {})))

(defn map-vals
  [f m]
  (zipmap (keys m)
    (map f (vals m))))

(defn toggle-contains-set
  [coll x]
  (if (contains? coll x)
    (disj coll x)
    (conj (or coll #{}) x)))

(defn toggle-contains-vector
  [coll x]
  (if (some #(= x %) coll)
    (->> coll
      (remove #(= x %))
      (vec))
    (conj (or coll []) x)))

(defn combine-as-set
  [x y]
  (let [x (if (coll? x) x (hash-set x))
        y (if (coll? y) y (hash-set y))]
    (into x y)))
