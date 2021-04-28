(ns knowtator.model
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as str]
            [com.rpl.specter :as sp]
            [knowtator.specs :as specs]
            [knowtator.util :as util]))

(defn TXT-OBJS
  [k-type k id]
  [(sp/keypath :text-annotation k-type)
   (sp/filterer #((cond->> id ((complement set?) id) (conj #{})) (k %)))])

(defn TXT-OBJ [k-type k id] [(TXT-OBJS k-type k id) sp/FIRST])

(defn ann-color
  [{:keys [profile concept]} profiles]
  (get-in (util/map-with-key :id profiles)
          [profile
            :colors concept]))

(s/fdef ann-color
  :args (s/cat :ann      (s/keys :req-un [:ann/profile :ann/concept])
               :profiles (s/map-of ::specs/id ::specs/profile))
  :ret  (s/nilable ::specs/color))

(defn sort-spans-by-loc [spans] (sort-by (juxt :start :end) spans))

(s/fdef sort-spans-by-loc
  :args (s/cat :spans (s/coll-of ::specs/span :kind vector?))
  :ret  (s/coll-of ::specs/span))

(defn split-right
  [{s1e :end
    :as s1}
   {s2e :end
    :as s2}]
  (let [start (min s1e s2e)
        end   (max s1e s2e)]
    (when (not= start end)
      (assoc (if (< s1e s2e) s2 s1) :start start :end end))))

(s/fdef split-right
  :args (s/cat :s1 ::specs/span
               :s2 ::specs/span)
  :ret  (s/nilable ::specs/span))

(defn split-left
  [{s1s :start
    :as s1}
   {s2s :start
    :as s2}]
  (let [start (min s1s s2s)
        end   (max s1s s2s)]
    (when (not= start end)
      (assoc (if (< s1s s2s) s1 s2) :start start :end end))))

(s/fdef split-left
  :args (s/cat :s1 ::specs/span
               :s2 ::specs/span)
  :ret  (s/nilable ::specs/span))

(defn split-overlap
  [{s1s :start
    s1e :end
    :as s1}
   {s2s :start
    s2e :end
    :as s2}]
  (let [start (max s1s s2s)
        end   (min s1e s2e)]
    (when (not= start end)
      (assoc (merge-with util/combine-as-set s1 s2) :start start :end end))))

(s/fdef split-overlap
  :args (s/cat :s1 ::specs/span
               :s2 ::specs/span)
  :ret  (s/nilable :span-overlap/span))

(defn overlap?
  [{s1s :start
    s1e :end}
   {s2s :start
    s2e :end}]
  (or (<= s1s s2s (dec s1e)) (<= s2s s1s (dec s2e))))

(s/fdef overlap?
  :args (s/cat :s1 ::specs/span
               :s2 ::specs/span)
  :ret  boolean?)

(defn split-spans-into-overlaps
  [s1 s2]
  [(split-right s1 s2) (split-overlap s1 s2) (split-left s1 s2)])

(s/fdef split-spans-into-overlaps
  :args (s/cat :s1 ::specs/span
               :s2 ::specs/span)
  :ret  (s/cat :right   (s/nilable ::specs/span)
               :overlap (s/nilable :span-overlap/span)
               :left    (s/nilable ::specs/span)))

(defn make-overlapping-spans
  ([spans] (make-overlapping-spans spans #{}))
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
                                         #{}
                                         spans) finished]
                                (if s1
                                  [spans (conj finished s1)]
                                  [spans finished]))]
     (if (empty? spans) finished (recur new-spans finished)))))

(s/def ::spans
  (s/with-gen
   (s/coll-of ::specs/span)
   #(s/gen
     (s/coll-of (s/with-gen
                 ::specs/span (fn []
                                (gen/fmap (fn [{:keys [start end]
                                                :as   span}]
                                            (cond-> span
                                              (< end start) (assoc :start end
                                                                   :end start)))
                                          (s/gen ::specs/span))))))))

(s/fdef make-overlapping-spans
  :args (s/alt :unary  (s/cat :spans ::spans)
               :binary (s/cat :spans    ::spans
                              :finished (s/coll-of (s/or :overlap
                                                         :span-overlap/span
                                                         :regular ::specs/span)
                                                   :kind set?)))
  :ret  (s/coll-of (s/or :overlap :span-overlap/span
                         :regular ::specs/span)))

(defn resolve-span-content
  [content spans]
  (let [[container i] (->>
                       spans
                       make-overlapping-spans
                       sort-spans-by-loc
                       (reduce
                        (fn [[container i]
                             {:keys [start end]
                              :as   span}]
                          [(conj container
                                 (subs content i start)
                                 (assoc span :content (subs content start end)))
                           end])
                        [[] 0]))]
    (conj container (subs content i))))

(s/fdef resolve-span-content
  :args (s/with-gen
         (s/cat :content ::specs/content
                :spans   ::spans)
         #(gen/tuple
           (gen/fmap (partial apply str) (gen/vector (gen/char-alpha) 100))
           (gen/fmap (partial map
                              (fn [span]
                                (let [start (rand-int 100)
                                      end   (+ (inc start)
                                               (rand-int (- 100 (inc start))))]
                                  (assoc span :start start :end end))))
                     (s/gen ::spans))))
  :ret  (s/coll-of (s/or :string string?
                         :span   (s/merge (s/keys :req-un [::specs/content])
                                          (s/or :regular ::specs/span
                                                :overlap :span-overlap/span)))
                   :kind vector?))

(defn in-restriction?
  [m restriction]
  (every? (fn [{:keys [filter-type filter-values]}]
            (if (seq filter-values) (filter-values (get m filter-type)) true))
          restriction))

(s/fdef in-restriction?
  :args (s/alt :ann-in-restriction  (s/cat :ann         ::specs/ann
                                           :restriction ::specs/restriction)
               :span-in-restriction (s/cat :span        ::specs/span
                                           :anns        (s/map-of ::specs/id
                                                                  ::specs/ann)
                                           :restriction ::specs/restriction))
  :ret  boolean?)

(defn contain-loc? [{:keys [start end]} i] (<= start i end))

(s/fdef contain-loc?
  :args (s/cat :span ::specs/span
               :loc  int?)
  :ret  boolean?)

(defn spans-containing-loc [loc spans] (filter #(contain-loc? % loc) spans))

(s/fdef filter-in-ann
  :args (s/cat :loc   int?
               :spans (s/map-of ::specs/id ::specs/span))
  :ret  (s/map-of ::specs/id ::specs/span))

(defn split-into-paragraphs
  [spans]
  (->>
   spans
   (reduce (fn [[paragraphs current] span]
             (if (string? span)
               (let [[first-p & rest-p] (str/split-lines span)
                     middle-p           (->> rest-p
                                             butlast
                                             (map vector))
                     rest-p             (map vector rest-p)
                     last-p             (last rest-p)
                     current            (conj current first-p)]
                 (cond (and (empty? rest-p) (not (str/ends-with? span "\n")))
                       [paragraphs current]
                       (str/ends-with? span "\n") [(-> paragraphs
                                                       (conj current)
                                                       (into rest-p)) []]
                       :else [(-> paragraphs
                                  (conj current)
                                  (into middle-p)) last-p]))
               [paragraphs (conj current span)]))
           [[] []])
   (apply conj)))

(s/fdef split-into-paragraphs
  :args (s/cat :spans (s/coll-of (s/or :string  string?
                                       :regular ::specs/span
                                       :overlap :span-overlap/span)))
  :ret  (s/coll-of (s/coll-of (s/or :p       string?
                                    :regular ::specs/span
                                    :overlap :span-overlap/span)
                              :kind vector?)
                   :kind vector?)
  :fn   (fn [{:keys [args ret]}] (<= (count ret) (count args))))

(defn unique-id
  [coll prefix suffix-num]
  (let [id (->> suffix-num
                (str prefix)
                keyword)]
    (if (contains? coll id) (recur coll prefix (inc suffix-num)) id)))

(defn cycle-coll
  [id coll dir]
  (let [coll (vec coll)
        f    (case dir
               :next #(let [val (inc %)] (if (<= (count coll) val) 0 val))
               :prev #(let [val (dec %)]
                        (if (neg? val) (dec (count coll)) val)))]
    (->> id
         (.indexOf coll)
         f
         (get coll))))

(defn cycle-selection
  [db coll coll-id dir]
  (-> db
      (get-in [:selection coll-id])
      (cycle-coll (map :id coll) dir)))

(defn fn-if [v test-fn when-fn] (cond-> v (test-fn v) when-fn))

(defn mod-span
  [db loc f]
  (let [s (get-in db [:selection :spans])]
    (update-in
     db
     [:text-annotation :spans]
     (fn [spans]
       (let [spans    (zipmap (map :id spans) spans)
             new-span (let [new-span (-> spans
                                         (get s)
                                         (update loc f))]
                        (fn-if new-span
                               (comp (partial apply <) (juxt :end :start))
                               #(set/rename-keys %
                                                 {:start :end
                                                  :end   :start})))]
         (-> spans
             (cond-> s (assoc s new-span))
             vals))))))


#_(let [spans         [{:id    :C
                        :start 3
                        :end   7}
                       {:id    :B
                        :start 2
                        :end   8}
                       {:id    :A
                        :start 1
                        :end   3}
                       {:id    :D
                        :start 6
                        :end   10}
                       {:id    :E
                        :start 100
                        :end   120}]
        true-overlaps #{{:id    #{:A :B}
                         :start 2
                         :end   3
                         :ann   #{nil}}
                        {:id    #{:A}
                         :start 1
                         :end   2
                         :ann   #{nil}}
                        {:id    #{:D}
                         :start 8
                         :end   10
                         :ann   #{nil}}
                        {:id    #{:B :D}
                         :start 7
                         :end   8
                         :ann   #{nil}}
                        {:id    #{:B :C}
                         :start 3
                         :end   6
                         :ann   #{nil}}
                        {:id    #{:B :C :D}
                         :start 6
                         :end   7
                         :ann   #{nil}}
                        {:id    #{:E}
                         :start 100
                         :end   120
                         :ann   #{nil}}}
        overlaps      (make-overlapping-spans spans)]
    (assert (= overlaps true-overlaps)
            (clojure.data/diff overlaps true-overlaps)))

(defn realize-span
  [db {:keys [start end ann]
       :as   span}]
  (let [{:keys [doc]
         :as   ann}
        (->> db
             (sp/select-one (TXT-OBJ :anns :id ann)))
        content (-> db
                    (->> (sp/select-one [(TXT-OBJ :docs :id doc) :content]))
                    (subs start end))]
    (-> span
        (->> (merge ann))
        (assoc :content content))))

(defn realize-spans
  [db]
  ;; TODO Only realize spans that are visible
  (sp/transform [:text-annotation :spans sp/ALL] (partial realize-span db) db))

(defn realize-ann
  [{{:keys [color]} :defaults
    :as             db}
   {:keys [profile id concept]
    :as   ann}]
  (-> ann
      (assoc :content (->> db
                           (sp/select [(TXT-OBJS :spans :ann id) sp/ALL])
                           (map (partial realize-span db))
                           (map :content)
                           (interpose " ")
                           (apply str))
             :color   (or (sp/select-one [(TXT-OBJ :profiles :id profile)
                                          :colors concept]
                                         db)
                          color))))

(defn realize-anns
  [db]
  (sp/transform [:text-annotation :anns sp/ALL] (partial realize-ann db) db))

(defn realize-ann-node
  [db class-map owl-class?
   {:keys [ann]
    :as   node}]
  (let [node (merge (->> db
                         realize-anns
                         (sp/select-one (TXT-OBJ :anns :id ann)))
                    node)]
    (assoc
     node
     :label
     (->> node
          ((apply juxt
                  :content
                  (when owl-class? [(comp (partial get class-map) :concept)])))
          (interpose "\n")
          (apply str)))))

(defn realize-ann-nodes
  [graph db class-map owl-class?]
  (sp/transform [:nodes sp/ALL]
                (partial realize-ann-node db class-map owl-class?)
                graph))

(defn realize-relation-ann
  [property-map {{:keys                [property polarity]
                  {:keys [type value]} :quantifier}
                 :predicate
                 :as relation-ann}]
  (-> relation-ann
      (assoc :label
             (-> (list (name type) (get property-map property))
                 (cond-> (#{:min} type) (conj value))
                 (->> (interpose " ")
                      (apply str))))
      (assoc :color
             (case polarity
               :positive :black
               :negative :red
               :yellow))
      (assoc-in [:font :align] :top)))

(defn realize-relation-anns
  [graph property-map]
  (sp/transform [:edges sp/ALL]
                (partial realize-relation-ann property-map)
                graph))

(def text-objs-hierarchy
  (-> (make-hierarchy)
      (derive :docs :project)
      (derive :profiles :project)
      (derive :colors :profiles)
      (derive #_:concept-anns :anns :docs)
      (derive :graphs :docs)
      (derive :spans :anns #_:concept-anns)
      (derive :ann-nodes :graphs)
      (derive :assertion-anns :graphs)))
(def objs->obj
  {:docs           :doc
   :anns           :ann
   :profiles       :profiles
   :graphs         :graph
   :colors         :color
   :spans          :span
   :assertion-anns :assertion-ann
   :ann-nodes      :ann-node})

(defn remove-matching-sub-items
  [db parent-obj-k child-objs-k parent-id]
  (let [child-objs-nav (sp/comp-paths (TXT-OBJS child-objs-k
                                                parent-obj-k
                                                parent-id)
                                      sp/ALL)]
    (as-> db db
      (->> (for [parent-id   (sp/select [child-objs-nav :id]
                                        db)
                 child-obj-k (descendants text-objs-hierarchy child-objs-k)]
             [(objs->obj child-objs-k) child-obj-k parent-id])
           (reduce (fn [db args] (apply remove-matching-sub-items db args)) db))
      (sp/setval [:selection child-objs-k] nil db)
      (sp/setval child-objs-nav sp/NONE db))))

(defn remove-selected-item
  [db objs-k]
  (let [selected-id (get-in db [:selection objs-k])]
    (remove-matching-sub-items db :id objs-k selected-id)))

(defn verify-id
  [db k prefix]
  (->> db
       :text-annotation
       :graphs
       (map (comp count k))
       (reduce +)
       inc
       (str prefix)
       keyword))

(defn GRAPH-OBJS
  [graph-space-id k]
  [(TXT-OBJS :graphs :id graph-space-id) sp/ALL k sp/NIL->VECTOR])

(defn add-node
  [db graph-space-id node]
  (when-let [ann-id (get-in db [:selection :anns])]
    (let [new-node (merge node
                          {:id      (verify-id db :nodes "n")
                           :label   "test"
                           :physics false
                           :ann     ann-id})]
      (sp/transform (GRAPH-OBJS graph-space-id :nodes) #(conj % new-node) db))))


(defn add-edge
  [db graph-space-id edge]
  (when-let [obj-prop (get-in db [:selection :obj-props])]
    (let [new-edge (merge edge
                          {:id        (verify-id db :edges "e")
                           :predicate {:polarity   :positive
                                       :property   obj-prop
                                       :quantifier {:type  :some
                                                    :value nil}}})]
      (sp/transform (GRAPH-OBJS graph-space-id :edges) #(conj % new-edge) db))))



(defn get-analogy-map
  []
  (->
    {:hierarchy
     {:ancestors
      {:Bar #{:sme-clj.typedef/Entity :thing}
       :Beaker #{:sme-clj.typedef/Entity :thing}
       :Coffee #{:sme-clj.typedef/Entity :thing}
       :Heat #{:sme-clj.typedef/Entity :thing}
       :Icecube #{:sme-clj.typedef/Entity :thing}
       :Pipe #{:sme-clj.typedef/Entity :thing}
       :Vial #{:sme-clj.typedef/Entity :thing}
       :Water #{:sme-clj.typedef/Entity :thing}
       :cause #{:sme-clj.typedef/Expression :sme-clj.typedef/Functor
                :sme-clj.typedef/Relation :thing}
       :cause-greater-pressure-Beaker-pressure-Vial-flow-Beaker-Vial-Water-Pipe
       #{:sme-clj.typedef/Expression :thing}
       :clear #{:sme-clj.typedef/Attribute :sme-clj.typedef/Expression
                :sme-clj.typedef/Functor :thing}
       :clear-Beaker #{:sme-clj.typedef/Expression :thing}
       :diameter #{:sme-clj.typedef/Expression :sme-clj.typedef/Function
                   :sme-clj.typedef/Functor :thing}
       :diameter-Beaker #{:sme-clj.typedef/Expression :thing}
       :diameter-Vial #{:sme-clj.typedef/Expression :thing}
       :flat-top #{:sme-clj.typedef/Expression :sme-clj.typedef/Function
                   :sme-clj.typedef/Functor :thing}
       :flat-top-Coffee #{:sme-clj.typedef/Expression :thing}
       :flat-top-Water #{:sme-clj.typedef/Expression :thing}
       :flow #{:sme-clj.typedef/Expression :sme-clj.typedef/Functor
               :sme-clj.typedef/Relation :thing}
       :flow-Beaker-Vial-Water-Pipe #{:sme-clj.typedef/Expression :thing}
       :flow-Coffee-Icecube-Heat-Bar #{:sme-clj.typedef/Expression :thing}
       :greater #{:sme-clj.typedef/Expression :sme-clj.typedef/Functor
                  :sme-clj.typedef/Relation :thing}
       :greater-diameter-Beaker-diameter-Vial #{:sme-clj.typedef/Expression
                                                :thing}
       :greater-pressure-Beaker-pressure-Vial #{:sme-clj.typedef/Expression
                                                :thing}
       :greater-temperature-Coffee-temperature-Icecube
       #{:sme-clj.typedef/Expression :thing}
       :liquid #{:sme-clj.typedef/Attribute :sme-clj.typedef/Expression
                 :sme-clj.typedef/Functor :thing}
       :liquid-Coffee #{:sme-clj.typedef/Expression :thing}
       :liquid-Water #{:sme-clj.typedef/Expression :thing}
       :pressure #{:sme-clj.typedef/Expression :sme-clj.typedef/Function
                   :sme-clj.typedef/Functor :thing}
       :pressure-Beaker #{:sme-clj.typedef/Expression :thing}
       :pressure-Vial #{:sme-clj.typedef/Expression :thing}
       :simple-heat-flow #{:sme-clj.typedef/ConceptGraph}
       :simple-water-flow #{:sme-clj.typedef/ConceptGraph}
       :sme-clj.typedef/Attribute #{:sme-clj.typedef/Expression
                                    :sme-clj.typedef/Functor :thing}
       :sme-clj.typedef/Entity #{:thing}
       :sme-clj.typedef/Expression #{:thing}
       :sme-clj.typedef/Function #{:sme-clj.typedef/Expression
                                   :sme-clj.typedef/Functor :thing}
       :sme-clj.typedef/Functor #{:sme-clj.typedef/Expression :thing}
       :sme-clj.typedef/Relation #{:sme-clj.typedef/Expression
                                   :sme-clj.typedef/Functor :thing}
       :temperature #{:sme-clj.typedef/Expression :sme-clj.typedef/Function
                      :sme-clj.typedef/Functor :thing}
       :temperature-Coffee #{:sme-clj.typedef/Expression :thing}
       :temperature-Icecube #{:sme-clj.typedef/Expression :thing}}
      :descendants
      {:sme-clj.typedef/Attribute #{:clear :liquid}
       :sme-clj.typedef/ConceptGraph #{:simple-heat-flow :simple-water-flow}
       :sme-clj.typedef/Entity #{:Bar :Beaker :Coffee :Heat :Icecube :Pipe :Vial
                                 :Water}
       :sme-clj.typedef/Expression
       #{:cause
         :cause-greater-pressure-Beaker-pressure-Vial-flow-Beaker-Vial-Water-Pipe
         :clear :clear-Beaker :diameter :diameter-Beaker :diameter-Vial
         :flat-top :flat-top-Coffee :flat-top-Water :flow
         :flow-Beaker-Vial-Water-Pipe :flow-Coffee-Icecube-Heat-Bar :greater
         :greater-diameter-Beaker-diameter-Vial
         :greater-pressure-Beaker-pressure-Vial
         :greater-temperature-Coffee-temperature-Icecube :liquid :liquid-Coffee
         :liquid-Water :pressure :pressure-Beaker :pressure-Vial
         :sme-clj.typedef/Attribute :sme-clj.typedef/Function
         :sme-clj.typedef/Functor :sme-clj.typedef/Relation :temperature
         :temperature-Coffee :temperature-Icecube}
       :sme-clj.typedef/Function #{:diameter :flat-top :pressure :temperature}
       :sme-clj.typedef/Functor
       #{:cause :clear :diameter :flat-top :flow :greater :liquid :pressure
         :sme-clj.typedef/Attribute :sme-clj.typedef/Function
         :sme-clj.typedef/Relation :temperature}
       :sme-clj.typedef/Relation #{:cause :flow :greater}
       :thing
       #{:Bar :Beaker :Coffee :Heat :Icecube :Pipe :Vial :Water :cause
         :cause-greater-pressure-Beaker-pressure-Vial-flow-Beaker-Vial-Water-Pipe
         :clear :clear-Beaker :diameter :diameter-Beaker :diameter-Vial
         :flat-top :flat-top-Coffee :flat-top-Water :flow
         :flow-Beaker-Vial-Water-Pipe :flow-Coffee-Icecube-Heat-Bar :greater
         :greater-diameter-Beaker-diameter-Vial
         :greater-pressure-Beaker-pressure-Vial
         :greater-temperature-Coffee-temperature-Icecube :liquid :liquid-Coffee
         :liquid-Water :pressure :pressure-Beaker :pressure-Vial
         :sme-clj.typedef/Attribute :sme-clj.typedef/Entity
         :sme-clj.typedef/Expression :sme-clj.typedef/Function
         :sme-clj.typedef/Functor :sme-clj.typedef/Relation :temperature
         :temperature-Coffee :temperature-Icecube}}
      :parents
      {:Bar #{:sme-clj.typedef/Entity}
       :Beaker #{:sme-clj.typedef/Entity}
       :Coffee #{:sme-clj.typedef/Entity}
       :Heat #{:sme-clj.typedef/Entity}
       :Icecube #{:sme-clj.typedef/Entity}
       :Pipe #{:sme-clj.typedef/Entity}
       :Vial #{:sme-clj.typedef/Entity}
       :Water #{:sme-clj.typedef/Entity}
       :cause #{:sme-clj.typedef/Relation}
       :cause-greater-pressure-Beaker-pressure-Vial-flow-Beaker-Vial-Water-Pipe
       #{:sme-clj.typedef/Expression}
       :clear #{:sme-clj.typedef/Attribute}
       :clear-Beaker #{:sme-clj.typedef/Expression}
       :diameter #{:sme-clj.typedef/Function}
       :diameter-Beaker #{:sme-clj.typedef/Expression}
       :diameter-Vial #{:sme-clj.typedef/Expression}
       :flat-top #{:sme-clj.typedef/Function}
       :flat-top-Coffee #{:sme-clj.typedef/Expression}
       :flat-top-Water #{:sme-clj.typedef/Expression}
       :flow #{:sme-clj.typedef/Relation}
       :flow-Beaker-Vial-Water-Pipe #{:sme-clj.typedef/Expression}
       :flow-Coffee-Icecube-Heat-Bar #{:sme-clj.typedef/Expression}
       :greater #{:sme-clj.typedef/Relation}
       :greater-diameter-Beaker-diameter-Vial #{:sme-clj.typedef/Expression}
       :greater-pressure-Beaker-pressure-Vial #{:sme-clj.typedef/Expression}
       :greater-temperature-Coffee-temperature-Icecube
       #{:sme-clj.typedef/Expression}
       :liquid #{:sme-clj.typedef/Attribute}
       :liquid-Coffee #{:sme-clj.typedef/Expression}
       :liquid-Water #{:sme-clj.typedef/Expression}
       :pressure #{:sme-clj.typedef/Function}
       :pressure-Beaker #{:sme-clj.typedef/Expression}
       :pressure-Vial #{:sme-clj.typedef/Expression}
       :simple-heat-flow #{:sme-clj.typedef/ConceptGraph}
       :simple-water-flow #{:sme-clj.typedef/ConceptGraph}
       :sme-clj.typedef/Attribute #{:sme-clj.typedef/Functor}
       :sme-clj.typedef/Entity #{:thing}
       :sme-clj.typedef/Expression #{:thing}
       :sme-clj.typedef/Function #{:sme-clj.typedef/Functor}
       :sme-clj.typedef/Functor #{:sme-clj.typedef/Expression}
       :sme-clj.typedef/Relation #{:sme-clj.typedef/Functor}
       :temperature #{:sme-clj.typedef/Function}
       :temperature-Coffee #{:sme-clj.typedef/Expression}
       :temperature-Icecube #{:sme-clj.typedef/Expression}}}
     :ids {}
     :mops
     {:Bar {:id      :Bar
            :inst?   false
            :names   #{}
            :parents #{:sme-clj.typedef/Entity}}
      :Beaker {:id      :Beaker
               :inst?   false
               :names   #{}
               :parents #{:sme-clj.typedef/Entity}}
      :Coffee {:id      :Coffee
               :inst?   false
               :names   #{}
               :parents #{:sme-clj.typedef/Entity}}
      :Heat {:id      :Heat
             :inst?   false
             :names   #{}
             :parents #{:sme-clj.typedef/Entity}}
      :Icecube {:id      :Icecube
                :inst?   false
                :names   #{}
                :parents #{:sme-clj.typedef/Entity}}
      :Pipe {:id      :Pipe
             :inst?   false
             :names   #{}
             :parents #{:sme-clj.typedef/Entity}}
      :Vial {:id      :Vial
             :inst?   false
             :names   #{}
             :parents #{:sme-clj.typedef/Entity}}
      :Water {:id      :Water
              :inst?   false
              :names   #{}
              :parents #{:sme-clj.typedef/Entity}}
      :cause {:e1      #{:sme-clj.typedef/Expression}
              :e2      #{:sme-clj.typedef/Expression}
              :id      :cause
              :inst?   false
              :names   #{}
              :parents #{:sme-clj.typedef/Relation}}
      :cause-greater-pressure-Beaker-pressure-Vial-flow-Beaker-Vial-Water-Pipe
      {:concept-graph #{:simple-water-flow}
       :e1 #{:greater-pressure-Beaker-pressure-Vial}
       :e2 #{:flow-Beaker-Vial-Water-Pipe}
       :functor #{:cause}
       :id
       :cause-greater-pressure-Beaker-pressure-Vial-flow-Beaker-Vial-Water-Pipe
       :inst? false
       :names #{}
       :parents #{:sme-clj.typedef/Expression}}
      :clear {:e1      #{:sme-clj.typedef/Entity}
              :id      :clear
              :inst?   false
              :names   #{}
              :parents #{:sme-clj.typedef/Attribute}}
      :clear-Beaker {:concept-graph #{:simple-water-flow}
                     :e1            #{:Beaker}
                     :functor       #{:clear}
                     :id            :clear-Beaker
                     :inst?         false
                     :names         #{}
                     :parents       #{:sme-clj.typedef/Expression}}
      :diameter {:e1      #{:sme-clj.typedef/Entity}
                 :id      :diameter
                 :inst?   false
                 :names   #{}
                 :parents #{:sme-clj.typedef/Function}}
      :diameter-Beaker {:concept-graph #{:simple-water-flow}
                        :e1            #{:Beaker}
                        :functor       #{:diameter}
                        :id            :diameter-Beaker
                        :inst?         false
                        :names         #{}
                        :parents       #{:sme-clj.typedef/Expression}}
      :diameter-Vial {:concept-graph #{:simple-water-flow}
                      :e1            #{:Vial}
                      :functor       #{:diameter}
                      :id            :diameter-Vial
                      :inst?         false
                      :names         #{}
                      :parents       #{:sme-clj.typedef/Expression}}
      :flat-top {:e1      #{:sme-clj.typedef/Entity}
                 :id      :flat-top
                 :inst?   false
                 :names   #{}
                 :parents #{:sme-clj.typedef/Function}}
      :flat-top-Coffee {:concept-graph #{:simple-heat-flow}
                        :e1            #{:Coffee}
                        :functor       #{:flat-top}
                        :id            :flat-top-Coffee
                        :inst?         false
                        :names         #{}
                        :parents       #{:sme-clj.typedef/Expression}}
      :flat-top-Water {:concept-graph #{:simple-water-flow}
                       :e1            #{:Water}
                       :functor       #{:flat-top}
                       :id            :flat-top-Water
                       :inst?         false
                       :names         #{}
                       :parents       #{:sme-clj.typedef/Expression}}
      :flow {:e1      #{:sme-clj.typedef/Entity}
             :e2      #{:sme-clj.typedef/Entity}
             :e3      #{:sme-clj.typedef/Entity}
             :e4      #{:sme-clj.typedef/Entity}
             :id      :flow
             :inst?   false
             :names   #{}
             :parents #{:sme-clj.typedef/Relation}}
      :flow-Beaker-Vial-Water-Pipe {:concept-graph #{:simple-water-flow}
                                    :e1 #{:Beaker}
                                    :e2 #{:Vial}
                                    :e3 #{:Water}
                                    :e4 #{:Pipe}
                                    :functor #{:flow}
                                    :id :flow-Beaker-Vial-Water-Pipe
                                    :inst? false
                                    :names #{}
                                    :parents #{:sme-clj.typedef/Expression}}
      :flow-Coffee-Icecube-Heat-Bar {:concept-graph #{:simple-heat-flow}
                                     :e1 #{:Coffee}
                                     :e2 #{:Icecube}
                                     :e3 #{:Heat}
                                     :e4 #{:Bar}
                                     :functor #{:flow}
                                     :id :flow-Coffee-Icecube-Heat-Bar
                                     :inst? false
                                     :names #{}
                                     :parents #{:sme-clj.typedef/Expression}}
      :greater {:e1      #{:sme-clj.typedef/Expression}
                :e2      #{:sme-clj.typedef/Expression}
                :id      :greater
                :inst?   false
                :names   #{}
                :parents #{:sme-clj.typedef/Relation}}
      :greater-diameter-Beaker-diameter-Vial
      {:concept-graph #{:simple-water-flow}
       :e1            #{:diameter-Beaker}
       :e2            #{:diameter-Vial}
       :functor       #{:greater}
       :id            :greater-diameter-Beaker-diameter-Vial
       :inst?         false
       :names         #{}
       :parents       #{:sme-clj.typedef/Expression}}
      :greater-pressure-Beaker-pressure-Vial
      {:concept-graph #{:simple-water-flow}
       :e1            #{:pressure-Beaker}
       :e2            #{:pressure-Vial}
       :functor       #{:greater}
       :id            :greater-pressure-Beaker-pressure-Vial
       :inst?         false
       :names         #{}
       :parents       #{:sme-clj.typedef/Expression}}
      :greater-temperature-Coffee-temperature-Icecube
      {:concept-graph #{:simple-heat-flow}
       :e1            #{:temperature-Coffee}
       :e2            #{:temperature-Icecube}
       :functor       #{:greater}
       :id            :greater-temperature-Coffee-temperature-Icecube
       :inst?         false
       :names         #{}
       :parents       #{:sme-clj.typedef/Expression}}
      :liquid {:e1      #{:sme-clj.typedef/Entity}
               :id      :liquid
               :inst?   false
               :names   #{}
               :parents #{:sme-clj.typedef/Attribute}}
      :liquid-Coffee {:concept-graph #{:simple-heat-flow}
                      :e1            #{:Coffee}
                      :functor       #{:liquid}
                      :id            :liquid-Coffee
                      :inst?         false
                      :names         #{}
                      :parents       #{:sme-clj.typedef/Expression}}
      :liquid-Water {:concept-graph #{:simple-water-flow}
                     :e1            #{:Water}
                     :functor       #{:liquid}
                     :id            :liquid-Water
                     :inst?         false
                     :names         #{}
                     :parents       #{:sme-clj.typedef/Expression}}
      :pressure {:e1      #{:sme-clj.typedef/Entity}
                 :id      :pressure
                 :inst?   false
                 :names   #{}
                 :parents #{:sme-clj.typedef/Function}}
      :pressure-Beaker {:concept-graph #{:simple-water-flow}
                        :e1            #{:Beaker}
                        :functor       #{:pressure}
                        :id            :pressure-Beaker
                        :inst?         false
                        :names         #{}
                        :parents       #{:sme-clj.typedef/Expression}}
      :pressure-Vial {:concept-graph #{:simple-water-flow}
                      :e1            #{:Vial}
                      :functor       #{:pressure}
                      :id            :pressure-Vial
                      :inst?         false
                      :names         #{}
                      :parents       #{:sme-clj.typedef/Expression}}
      :simple-heat-flow {:id      :simple-heat-flow
                         :inst?   false
                         :names   #{}
                         :parents #{:sme-clj.typedef/ConceptGraph}}
      :simple-water-flow {:id      :simple-water-flow
                          :inst?   false
                          :names   #{}
                          :parents #{:sme-clj.typedef/ConceptGraph}}
      :sme-clj.typedef/Attribute {:id      :sme-clj.typedef/Attribute
                                  :inst?   false
                                  :names   #{}
                                  :parents #{:sme-clj.typedef/Functor}}
      :sme-clj.typedef/Entity {:id      :sme-clj.typedef/Entity
                               :inst?   false
                               :names   #{}
                               :parents #{:thing}}
      :sme-clj.typedef/Expression {:id      :sme-clj.typedef/Expression
                                   :inst?   false
                                   :names   #{}
                                   :parents #{:thing}}
      :sme-clj.typedef/Function {:id      :sme-clj.typedef/Function
                                 :inst?   false
                                 :names   #{}
                                 :parents #{:sme-clj.typedef/Functor}}
      :sme-clj.typedef/Functor {:id      :sme-clj.typedef/Functor
                                :inst?   false
                                :names   #{}
                                :parents #{:sme-clj.typedef/Expression}}
      :sme-clj.typedef/Relation {:id       :sme-clj.typedef/Relation
                                 :inst?    false
                                 :names    #{}
                                 :ordered? #{true}
                                 :parents  #{:sme-clj.typedef/Functor}}
      :temperature {:e1      #{:sme-clj.typedef/Entity}
                    :id      :temperature
                    :inst?   false
                    :names   #{}
                    :parents #{:sme-clj.typedef/Function}}
      :temperature-Coffee {:concept-graph #{:simple-heat-flow}
                           :e1            #{:Coffee}
                           :functor       #{:temperature}
                           :id            :temperature-Coffee
                           :inst?         false
                           :names         #{}
                           :parents       #{:sme-clj.typedef/Expression}}
      :temperature-Icecube {:concept-graph #{:simple-heat-flow}
                            :e1            #{:Icecube}
                            :functor       #{:temperature}
                            :id            :temperature-Icecube
                            :inst?         false
                            :names         #{}
                            :parents       #{:sme-clj.typedef/Expression}}}}
    :mops
    vals
    (->> (map #(dissoc % :inst? :names)))
    (->> (reduce (fn [m mop]
                   (-> m
                       (update :nodes conj mop)
                       (update :edges
                               into
                               (-> mop
                                   (dissoc :id)
                                   (->> (mapcat (fn [[k vs]]
                                                  (map (fn [v]
                                                         {:from k
                                                          :to   v})
                                                       vs))))))))
                 {:nodes []
                  :edges []}))))
