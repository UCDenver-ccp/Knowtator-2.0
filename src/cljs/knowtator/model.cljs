(ns knowtator.model
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as str]
            [knowtator.specs :as specs]
            [knowtator.util :as util]))

(defn ann-color
  [{:keys [profile concept]} profiles]
  (get-in profiles [profile :colors concept]))

(s/fdef ann-color
  :args (s/cat
          :ann (s/keys :req-un [:ann/profile :ann/concept])
          :profiles (s/map-of ::specs/id ::specs/profile))
  :ret (s/nilable ::specs/color))

(defn sort-spans-by-loc
  [spans]
  (sort-by (juxt :start :end) spans))

(s/fdef sort-spans-by-loc
  :args (s/cat :spans (s/coll-of ::specs/span :kind vector?))
  :ret (s/coll-of ::specs/span))

(defn split-right
  [{s1e :end :as s1}
   {s2e :end :as s2}]
  (let [start (min s1e s2e)
        end   (max s1e s2e)]
    (when (not= start end)
      (assoc (if (< s1e s2e) s2 s1)
        :start start
        :end end))))

(s/fdef split-right
  :args (s/cat
          :s1 ::specs/span
          :s2 ::specs/span)
  :ret (s/nilable ::specs/span))

(defn split-left
  [{s1s :start :as s1}
   {s2s :start :as s2}]
  (let [start (min s1s s2s)
        end   (max s1s s2s)]
    (when (not= start end)
      (assoc (if (< s1s s2s) s1 s2)
        :start start
        :end end))))

(s/fdef split-left
  :args (s/cat
          :s1 ::specs/span
          :s2 ::specs/span)
  :ret (s/nilable ::specs/span))

(defn split-overlap
  [{s1s :start s1e :end :as s1}
   {s2s :start s2e :end :as s2}]
  (let [start (max s1s s2s)
        end   (min s1e s2e)]
    (when (not= start end)
      (assoc (merge-with util/combine-as-set s1 s2)
        :start start
        :end end))))

(s/fdef split-overlap
  :args (s/cat
          :s1 ::specs/span
          :s2 ::specs/span)
  :ret (s/nilable :span-overlap/span))

(defn overlap?
  [{s1s :start s1e :end}
   {s2s :start s2e :end}]
  (or (<= s1s s2s (dec s1e))
    (<= s2s s1s (dec s2e))))

(s/fdef overlap?
  :args (s/cat
          :s1 ::specs/span
          :s2 ::specs/span)
  :ret boolean?)

(defn split-spans-into-overlaps
  [s1 s2]
  [(split-right s1 s2)
   (split-overlap s1 s2)
   (split-left s1 s2)])

(s/fdef split-spans-into-overlaps
  :args (s/cat
          :s1 ::specs/span
          :s2 ::specs/span)
  :ret (s/cat
         :right (s/nilable ::specs/span)
         :overlap (s/nilable :span-overlap/span)
         :left (s/nilable ::specs/span)))

(defn make-overlapping-spans
  ([spans]
   (make-overlapping-spans spans #{}))
  ([spans finished]
   (let [s1                   (first spans)
         spans                (set (rest spans))
         [new-spans finished] (if (some (partial overlap? s1) spans)
                                [(reduce (fn [spans s2]
                                           (if (overlap? s1 s2)
                                             (->> s2
                                               (split-spans-into-overlaps s1)
                                               (remove nil?)
                                               (into spans))
                                             (conj spans s2)))
                                   #{} spans)
                                 finished]
                                (if s1 [spans (conj finished s1)] [spans finished]))]
     (if (empty? spans)
       finished
       (recur new-spans finished)))))

(s/def ::spans (s/with-gen (s/coll-of ::specs/span)
                 #(s/gen (s/coll-of (s/with-gen ::specs/span
                                      (fn []
                                        (gen/fmap (fn [{:keys [start end] :as span}]
                                                    (cond-> span
                                                      (< end start) (assoc :start end :end start)))
                                          (s/gen ::specs/span))))))))

(s/fdef make-overlapping-spans
  :args (s/alt :unary (s/cat :spans ::spans)
          :binary (s/cat
                    :spans ::spans
                    :finished (s/coll-of (s/or :overlap :span-overlap/span :regular ::specs/span) :kind set?)))
  :ret (s/coll-of (s/or :overlap :span-overlap/span :regular ::specs/span)))

(defn resolve-span-content
  [content spans]
  (let [[container i] (->> spans
                        make-overlapping-spans
                        sort-spans-by-loc
                        (reduce (fn [[container i] {:keys [start end] :as span}]
                                  [(conj container
                                     (subs content i start)
                                     (assoc span :content (subs content start end)))
                                   end])
                          [[] 0]))]
    (conj container (subs content i))))

(s/fdef resolve-span-content
  :args (s/with-gen (s/cat
                      :content ::specs/content
                      :spans ::spans)
          #(gen/tuple
             (gen/fmap (partial apply str) (gen/vector (gen/char-alpha) 100))
             (gen/fmap (partial map (fn [span]
                                      (let [start (rand-int 100)
                                            end   (+ (inc start) (rand-int (- 100 (inc start))))]
                                        (assoc span
                                          :start start
                                          :end end))))
               (s/gen ::spans))))
  :ret (s/coll-of (s/or :string string?
                    :span (s/merge
                            (s/keys :req-un [::specs/content])
                            (s/or :regular ::specs/span :overlap :span-overlap/span)))
         :kind vector?))

(defn in-restriction?
  ([ann restriction]
   (every? (fn [[k v]]
             (= (get ann k) v))
     restriction))
  ([{:keys [ann]} anns restriction]
   (in-restriction? (get anns ann) restriction)))

(s/fdef in-restriction?
  :args (s/alt
          :ann-in-restriction (s/cat
                                :ann ::specs/ann
                                :restriction ::specs/restriction)
          :span-in-restriction (s/cat
                                 :span ::specs/span
                                 :anns (s/map-of ::specs/id ::specs/ann)
                                 :restriction ::specs/restriction))
  :ret boolean?)

(defn in-ann?
  [{:keys [ann]} ann-id]
  (= ann ann-id))

(s/fdef in-ann?
  :args (s/cat :span ::specs/span :ann-id ::specs/id)
  :ret boolean?)

(defn filter-in-restriction
  ([restriction anns]
   (util/filter-vals #(in-restriction? % restriction) anns))
  ([restriction anns spans]
   (util/filter-vals #(in-restriction? % anns restriction) spans)))

#_(defn filter-in-profile
    ([profile-id anns]
     (util/filter-vals #(in-profile? % profile-id) anns))
    ([profile-id anns spans]
     (util/filter-vals #(in-profile? % anns profile-id) spans)))

(s/fdef filter-in-doc
  :args (s/alt
          :anns-in-doc (s/cat
                         :doc-id ::specs/id
                         :ann (s/map-of ::specs/id ::specs/ann))
          :spans-in-doc (s/cat
                          :doc-id ::specs/id
                          :anns (s/map-of ::specs/id ::specs/ann)
                          :spans (s/map-of ::specs/id ::specs/span)))
  :ret (s/or :anns (s/map-of ::specs/id ::specs/ann)
         :spans (s/map-of ::specs/id ::specs/span)))

(defn filter-in-ann
  ([ann-id spans]
   (util/filter-vals #(in-ann? % ann-id) spans)))

(s/fdef filter-in-ann
  :args (s/cat :ann-id ::specs/id :spans (s/map-of ::specs/id ::specs/span))
  :ret (s/map-of ::specs/id ::specs/span))

(defn contain-loc?
  [{:keys [start end]} i]
  (<= start i end))

(s/fdef contain-loc?
  :args (s/cat :span ::specs/span :loc int?)
  :ret boolean?)

(defn spans-containing-loc
  [loc spans]
  (util/filter-vals #(contain-loc? % loc) spans))

(s/fdef filter-in-ann
  :args (s/cat :loc int? :spans (s/map-of ::specs/id ::specs/span))
  :ret (s/map-of ::specs/id ::specs/span))

(defn split-into-paragraphs
  [spans]
  (->> spans
    (reduce (fn [[paragraphs current] span]
              (if (string? span)
                (let [[first-p & rest-p] (str/split-lines span)
                      middle-p           (->> rest-p
                                           butlast
                                           (map vector))
                      rest-p             (map vector rest-p)
                      last-p             (last rest-p)
                      current            (conj current first-p)]
                  (cond
                    (and (empty? rest-p) (not (str/ends-with? span "\n"))) [paragraphs current]
                    (str/ends-with? span "\n")                             [(-> paragraphs
                                                                              (conj current)
                                                                              (into rest-p))
                                                                            []]
                    :else                                                  [(-> paragraphs
                                                                              (conj current)
                                                                              (into middle-p))
                                                                            last-p]))
                [paragraphs (conj current span)]))
      [[] []])
    (apply conj)))

(s/fdef split-into-paragraphs
  :args (s/cat :spans (s/coll-of (s/or
                                   :string string?
                                   :regular ::specs/span
                                   :overlap :span-overlap/span)))
  :ret (s/coll-of
         (s/coll-of (s/or
                      :p string?
                      :regular ::specs/span
                      :overlap :span-overlap/span)
           :kind vector?)
         :kind vector?)
  :fn (fn [{:keys [args ret]}]
        (<= (count ret) (count args))))

(defn unique-id
  [db k prefix suffix-num]
  (let [id (->> suffix-num (str prefix) keyword)]
    (if (contains? (get db k) id)
      (recur db k prefix (inc suffix-num))
      id)))

(defn cycle-coll
  [id db k dir]
  ;; TODO sorting of spans needs to be handled by start and end locs
  (let [docs (-> db k keys sort vec)
        f    (case dir
               :next #(let [val (inc %)]
                        (if (<= (count docs) val)
                          0
                          val))
               :prev #(let [val (dec %)]
                        (if (neg? val)
                          (dec (count docs))
                          val)))]
    (->> id
      (.indexOf docs)
      f
      (get docs))))

(defn cycle-selection
  [db sel col dir]
  (update-in db [:selection sel] cycle-coll db col dir))

(defn mod-span
  [db loc f]
  (let [s (get-in db [:selection :span])]
    (update-in db [:spans s] #(let [{:keys [start end] :as new-s} (update % loc f)]
                                (cond-> new-s
                                  (< end start) (assoc
                                                  :start (:end new-s)
                                                  :end (:start new-s)))))))

#_(let [spans         [{:id :C :start 3 :end 7}
                       {:id :B :start 2 :end 8}
                       {:id :A :start 1 :end 3}
                       {:id :D :start 6 :end 10}
                       {:id :E :start 100 :end 120}]
        true-overlaps #{{:id #{:A :B}, :start 2, :end 3 :ann #{nil}}
                        {:id #{:A}, :start 1, :end 2 :ann #{nil}}
                        {:id #{:D}, :start 8, :end 10 :ann #{nil}}
                        {:id #{:B :D}, :start 7, :end 8 :ann #{nil}}
                        {:id #{:B :C}, :start 3, :end 6 :ann #{nil}}
                        {:id #{:B :C :D}, :start 6, :end 7 :ann #{nil}}
                        {:id #{:E}, :start 100, :end 120 :ann #{nil}}}
        overlaps      (make-overlapping-spans spans)]
    (assert (= overlaps true-overlaps) (clojure.data/diff overlaps true-overlaps)))
