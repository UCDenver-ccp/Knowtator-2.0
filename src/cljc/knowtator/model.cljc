(ns knowtator.model
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as str]
            [knowtator.specs :as specs]
            [knowtator.util :as util]))

(defn ann-color
  [{:keys [profile concept]} profiles]
  (get-in (util/map-with-key :id profiles) [profile :colors concept]))

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
  [m restriction]
  (every? (fn [{:keys [filter-type filter-values]}]
            (if (seq filter-values)
              (filter-values (get m filter-type))
              true))
    restriction))

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

(defn contain-loc?
  [{:keys [start end]} i]
  (<= start i end))

(s/fdef contain-loc?
  :args (s/cat :span ::specs/span :loc int?)
  :ret boolean?)

(defn spans-containing-loc
  [loc spans]
  (filter #(contain-loc? % loc) spans))

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
  [coll prefix suffix-num]
  (let [id (->> suffix-num (str prefix) keyword)]
    (if (contains? coll id)
      (recur coll prefix (inc suffix-num))
      id)))

(defn cycle-coll
  [id coll dir]
  (let [coll (vec coll)
        f    (case dir
               :next #(let [val (inc %)]
                        (if (<= (count coll) val)
                          0
                          val))
               :prev #(let [val (dec %)]
                        (if (neg? val)
                          (dec (count coll))
                          val)))]
    (->> id
      (.indexOf coll)
      f
      (get coll))))

(defn cycle-selection
  [db coll coll-id dir]
  (-> db
    (get-in [:selection coll-id])
    (cycle-coll (map :id coll) dir)))

(defn fn-if
  [v test-fn when-fn]
  (cond-> v
    (test-fn v) when-fn))

(defn mod-span
  [db loc f]
  (let [s (get-in db [:selection :spans])]
    (update-in db [:text-annotation :spans] (fn [spans]
                                              (let [spans    (zipmap (map :id spans) spans)
                                                    new-span (let [new-span (-> spans
                                                                              (get s)
                                                                              (update loc f))]
                                                               (fn-if new-span
                                                                 (comp (partial apply <)
                                                                   (juxt :end :start))
                                                                 #(set/rename-keys % {:start :end
                                                                                      :end   :start})))]
                                                (-> spans
                                                  (cond-> s (assoc s new-span))
                                                  vals))))))


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

(defn realize-span
  [doc-map ann-map {:keys [start end ann] :as span}]
  (let [{:keys [doc] :as ann} (get ann-map ann)
        content               (-> doc-map
                                (get-in [doc :content])
                                (subs start end))]
    (-> span
      (->> (merge ann))
      (assoc :content content))))

(defn realize-spans
  [{{:keys [docs anns]} :text-annotation :as db}]
  (let [ann-map (util/map-with-key :id anns)
        doc-map (util/map-with-key :id docs)]
    (-> db
      (update-in [:text-annotation :spans] (partial map (partial realize-span doc-map ann-map))))))

(defn realize-ann
  [{{:keys [spans]} :text-annotation
    {:keys [color]} :defaults}
   profile-map
   doc-map
   ann-map
   {:keys [profile id concept] :as ann}]
  (-> ann
    (assoc
      :content (->> spans
                 (group-by :ann)
                 id
                 (map (partial realize-span doc-map ann-map))
                 (map :content)
                 (interpose " ")
                 (apply str))
      :color (get-in profile-map [profile :colors concept] color))))

(defn realize-anns
  [{{:keys [profiles docs anns]} :text-annotation :as db}]
  (let [profile-map (util/map-with-key :id profiles)
        doc-map     (util/map-with-key :id docs)
        ann-map     (util/map-with-key :id anns)]
    (-> db
      (update-in [:text-annotation :anns] (partial map (partial realize-ann db profile-map doc-map ann-map))))))

(defn realize-ann-node
  [db profile-map doc-map ann-map class-map owl-class? {:keys [ann] :as node}]
  (let [realized-ann (->> ann-map
                       ann
                       (realize-ann db profile-map doc-map ann-map))
        node         (merge realized-ann node)]
    (assoc node :label (if owl-class?
                         (->> [(:content node)
                               (->> node
                                 :concept
                                 (get class-map))]
                           (interpose "\n")
                           (apply str))
                         (:content node)))))

(defn realize-ann-nodes
  [graph db profile-map doc-map ann-map class-map owl-class?]
  (update graph :nodes (partial map (partial realize-ann-node db profile-map doc-map ann-map class-map owl-class?))))

(defn realize-relation-ann
  [property-map
   {{:keys                [property polarity]
     {:keys [type value]} :quantifier} :predicate
    :as                                relation-ann}]
  (let [property (get property-map property)
        type     (name type)]
    (-> relation-ann
      (assoc :label (->> (or
                           [type property]
                           [value type property])
                      (interpose " ")
                      (apply str)))
      (assoc :color (case polarity
                      :positive :black
                      :negative :red
                      :yellow)))))

(defn realize-relation-anns
  [graph property-map]
  (update graph :edges (partial map (partial realize-relation-ann property-map))))
