(ns knowtator-clj.subs-evts
  (:require [re-frame.core :refer [reg-event-db
                                   reg-sub
                                   reg-event-fx]]
            [ajax.core :as ajax]
            [knowtator-clj.db :as db]
            [knowtator-clj.util :as util]
            [knowtator-clj.span :as span]))

(reg-event-fx
 ::initialize-db
 (fn [_ _]
   {:db         db/default-db
    :http-xhrio {:method          :get
                 :uri             "/document-summary/"
                 :params          {:id :all}
                 :timeout         3000
                 :response-format (ajax/transit-response-format)
                 :on-success      [::receive-doc-list]}}))

(reg-event-db
 ::receive-doc-list
 (fn [db [_ docs]]
   (assoc db :docs docs
          :sending true)))

(reg-sub
 ::doc-ids
 (fn [db _]
   (->> db
        :docs
        vals
        (map :id))))

(reg-event-fx
 ::load-doc
 (fn [{:keys [db]} [_ id]]
   {:db         (assoc db :sending true)
    :http-xhrio {:method          :get
                 :uri             "/document/"
                 :params          {:id id}
                 :timeout         3000
                 :response-format (ajax/transit-response-format)
                 :on-success      [::receive-doc]
                 :on-failure      [::handle-failure]}}))

(reg-event-db
 ::handle-failure
 (fn [db [_ response]]
   (println "Failure:" response)
   (assoc db :response :failure
          :sending false)))

(reg-event-db
 ::receive-doc
 (fn [db [_ doc]]
   (assoc db :doc doc
          :sending true)))

(reg-sub
 ::visible-doc
 (fn [db]
   (get-in db [:selection :doc])))

(reg-sub
 ::visible-doc-id
 (fn [db]
   (get-in db [:selection :doc])))

(reg-sub
 ::visible-doc-content
 :<- [::visible-doc-id]
 :<- [::docs]
 (fn [[doc-id docs] _]
   (get-in docs [doc-id :content])))

(reg-sub
 ::sending?
 (fn [db _]
   (:sending db)))

(defn ->id
  []
  (str (random-uuid)))

(defn ->span [{:keys [content]} ann start end]
  {:start start
   :end   end
   :ann   ann
   :text  (subs content start end)})

(defn ->ann
  [profile owl-class]
  {:id        (->id)
   :owl-class owl-class
   :profile   profile})

(reg-event-fx
 ::add-ann
 (fn [{:keys [db]} [_]]
   (when (and (get-in db [:selection :start])
              (get-in db [:selection :end]))
     (let [profile   (get-in db [:profile :id])
           owl-class (get-in db [:owl-class :id])]
       {:db       (update-in db [:doc :anns] conj (->ann profile owl-class))
        :dispatch [::add-span]}))))

(reg-event-db
 ::add-span
 (fn [db [_]]
   (let [doc   (get-in db [:doc])
         start (get-in db [:selection :start])
         end   (get-in db [:selection :end])
         ann   (get-in db [:ann :id])]
     (if (and start end)
       (update-in db [:doc :spans] conj (->span doc ann start end))
       db))))

(defn vec-remove
  "remove elem in coll"
  [coll pos]
  (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))

(reg-event-db
 ::remove-span
 (fn [db [_ span]]
   (update-in db [:doc :spans] vec-remove span)))

(reg-event-db
 ::remove-ann
 (fn [db [_ ann]]
   (update-in db [:doc :anns ann] vec-remove ann)))

(reg-event-fx
 ::record-selection
 (fn [{:keys [db]} [_ loc end doc-id]]
   (let [[start end] (when-not (= loc end) [loc end])]
     (cond-> {:db (-> db
                      (assoc-in [:selection :start] start)
                      (assoc-in [:selection :end] end))}
       (= start end) (assoc :dispatch [::select-span loc doc-id])))))

(reg-sub
 ::graph-space
 (fn [db]
   (get db ::graph-space)))

(reg-sub
 ::graph-nodes
 (fn [db]
   (get-in db [:graph-space :nodes])))

(def space-len 10)
(def text-node-offset 10)

(reg-event-db
 ::perform-layout
 (fn [db _]
   (update-in db [:graph-space :nodes]
              (fn [nodes]
                (->> nodes
                     (reduce
                      (fn [[layout-nodes i] node]
                        (let [layout-nodes (conj layout-nodes (assoc node :x i))]
                          i (+ i space-len (util/len-text (:text node))))
                        [layout-nodes i])
                      [nil text-node-offset])
                     (first))))))

(reg-sub
 ::docs
 :docs)

(reg-sub
 ::anns
 :anns)

(reg-sub
 ::spans
 :spans)

(reg-sub
 ::profiles
 :profiles)

(defn ann-color
  [{:keys [profile concept]} profiles]
  (get-in profiles [profile concept]))

(defn span-color
  [{:keys [ann]} ann-colors]
  (if (coll? ann)
    (map ann-colors ann)
    (get ann-colors ann)))

#_(span-color (get-in db/default-db [:spans :s1])
              (get-in db/default-db [:anns])
              (:profiles db/default-db))

(reg-sub
 ::visible-anns
 :<- [::visible-doc-id]
 :<- [::anns]
 #(apply db/filter-in-doc %))

(reg-sub
 ::visible-ann-colors
 :<- [::visible-anns]
 :<- [::profiles]
 (fn [[visible-anns profiles] _]
   (util/map-vals #(ann-color % profiles) visible-anns)))

(reg-sub
 ::visible-spans
 :<- [::visible-doc-id]
 :<- [::anns]
 :<- [::spans]
 #(apply db/filter-in-doc %))

(reg-sub
 ::ann-color
 :<- [::anns]
 :<- [::profiles]
 (fn [[anns profiles] [_ ann]]
   (cond (not (coll? ann)) (ann-color (get anns ann) profiles)
         (empty? (rest ann)) (ann-color (get anns (first ann)) profiles)
         :else "grey")))

(defn resolve-span-content
  [content spans]
  (let [[container i] (reduce (fn [[container i] {:keys [start end] :as span}]
                                [(conj container
                                       (subs content i start)
                                       (assoc span :content (subs content start end)))
                                 end])
                              [[] 0] spans)]
    (conj container (subs content i))))

#_(highlight-text (get-in db/default-db [:docs :d1 :content])
                  (vals (get-in db/default-db [:spans]))
                  (get-in db/default-db [:anns])
                  :s1)

(reg-sub
 ::highlighted-text
 :<- [::visible-doc-content]
 :<- [::visible-spans]
 (fn [[content spans] _]
   (->> spans
        vals
        span/make-overlapping-spans
        span/sort-spans-by-loc
        (resolve-span-content content))))

(defn spans-containing-loc
  [loc spans]
  (util/filter-vals #(span/contain-loc? % loc) spans))

(reg-event-db
 ::select-span
 (fn [db [_ start doc-id]]
   (let [anns (:anns db)
         span-id         (->> db
                              :spans
                              (db/filter-in-doc doc-id anns)
                              (spans-containing-loc start)
                              vals
                              first
                              :id)]
     (assoc-in db [:selection :span] span-id))))

(reg-sub
 ::selected-span-id
 (fn [db _]
   (get-in db [:selection :span])))

(reg-sub
 ::selected-ann-id
 (fn [db _]
   (get-in db [:spans (get-in db [:selection :span]) :ann])))

(reg-sub
 ::selected-ann-spans
 :<- [::selected-ann-id]
 :<- [::spans]
 (fn [[ann-id spans]]
   (db/filter-in-ann ann-id spans)))

(reg-sub
 ::selected-span-content
 :<- [::visible-doc-content]
 :<- [::selected-span-id]
 :<- [::selected-ann-spans]
 (fn [[content span-id spans] _]
   (->> (if span-id
          (assoc-in spans [span-id :selected] true)
          spans)
        vals
        (resolve-span-content content)
        (remove nil?))))
