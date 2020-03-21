(ns knowtator.model
  (:require [knowtator.util :as util]))

(defn ann-color
  [{:keys [profile concept]} profiles]
  (get-in profiles [profile concept]))

(defn resolve-span-content
  [content spans]
  (let [[container i] (reduce (fn [[container i] {:keys [start end] :as span}]
                                [(conj container
                                   (subs content i start)
                                   (assoc span :content (subs content start end)))
                                 end])
                        [[] 0] spans)]
    (conj container (subs content i))))

(defn in-doc?
  ([{:keys [doc]} doc-id]
   (= doc doc-id))
  ([{:keys [ann]} anns doc-id]
   (in-doc? (get anns ann) doc-id)))


(defn filter-in-doc
  ([doc-id anns]
   (util/filter-vals #(in-doc? % doc-id) anns))
  ([doc-id anns spans]
   (util/filter-vals #(in-doc? % anns doc-id) spans)))

(defn in-ann?
  [{:keys [ann]} ann-id]
  (= ann ann-id))

(defn filter-in-ann
  ([ann-id spans]
   (util/filter-vals #(in-ann? % ann-id) spans)))

(defn contain-loc?
  [{:keys [start end]} i]
  (<= start i end))

(defn spans-containing-loc
  [loc spans]
  (util/filter-vals #(contain-loc? % loc) spans))

(defn split-right
  [{s1e :end :as s1}
   {s2e :end :as s2}]
  (let [start (min s1e s2e)
        end (max s1e s2e)]
    (when (not= start end)
      (assoc (if (< s1e s2e) s2 s1)
             :start start
             :end end))))

(defn split-left
  [{s1s :start :as s1}
   {s2s :start :as s2}]
  (let [start (min s1s s2s)
        end (max s1s s2s)]
    (when (not= start end)
      (assoc (if (< s1s s2s) s1 s2)
             :start start
             :end end))))


(defn split-overlap
  [{s1s :start s1e :end :as s1}
   {s2s :start s2e :end :as s2}]
  (let [start (max s1s s2s)
        end (min s1e s2e)]
    (when (not= start end)
      (assoc (merge-with util/combine-as-set s1 s2)
             :start start
             :end end))))

(defn sort-spans-by-loc
  [spans]
  (sort-by (juxt :start :end) spans))

(defn overlap?
  [{s1s :start s1e :end}
   {s2s :start s2e :end}]
  (or (<= s1s s2s (dec s1e))
      (<= s2s s1s (dec s2e))))

(defn split-spans-into-overlaps
  [s1 s2]
  [(split-right s1 s2)
   (split-overlap s1 s2)
   (split-left s1 s2)])

(defn make-overlapping-spans
  ([spans]
   (make-overlapping-spans spans #{}))
  ([spans finished]
   (let [s1 (first spans)
         spans (set (rest spans))
         new-spans (->> spans
                        (reduce (fn [spans s2]
                                  (if (overlap? s1 s2)
                                    (->> s2
                                         (split-spans-into-overlaps s1)
                                         (remove nil?)
                                         (into spans))
                                    (conj spans s2)))
                                #{}))
         finished (if (= spans new-spans)
                    (conj finished s1)
                    finished)]
     (if (empty? spans)
       finished
       (recur new-spans finished)))))

#_(let [spans [{:id :C  :start 3 :end 7}
               {:id :B :start 2 :end 8}
               {:id :A :start 1 :end 3}
               {:id :D  :start 6 :end 10}
               {:id :E :start 100 :end 120}]
        true-overlaps #{{:id #{:A :B}, :start 2, :end 3 :ann #{nil}}
                        {:id #{:A}, :start 1, :end 2 :ann #{nil}}
                        {:id #{:D}, :start 8, :end 10 :ann #{nil}}
                        {:id #{:B :D}, :start 7, :end 8 :ann #{nil}}
                        {:id #{:B :C}, :start 3, :end 6 :ann #{nil}}
                        {:id #{:B :C :D}, :start 6, :end 7 :ann #{nil}}
                        {:id #{:E}, :start 100, :end 120 :ann #{nil}}}
        overlaps (make-overlapping-spans spans)]
    (assert (= overlaps true-overlaps) (clojure.data/diff overlaps true-overlaps)))
