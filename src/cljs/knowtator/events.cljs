(ns knowtator.events
  (:require
   [re-frame.core :as re-frame]
   [knowtator.db :as db]
   [knowtator.subs :as subs]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [knowtator.model :as model]
   [clojure.string :as str]))

(re-frame/reg-event-db
  ::initialize-db
  (fn-traced [_ _]
    db/default-db))

(re-frame/reg-event-db
  ::set-active-panel
  (fn-traced [db [_ active-panel]]
    (assoc db :active-panel active-panel)))

(re-frame/reg-event-db
  ::set-re-pressed-example
  (fn [db [_ value]]
    (assoc db :re-pressed-example value)))

(re-frame/reg-event-fx
  ::record-selection
  (fn [{:keys [db]} [_ {:keys [start end] :as text-range} doc-id]]
    (cond-> {:db (update db :selection merge text-range)}
      (= start end) (assoc :dispatch [::select-span start doc-id]))))

(re-frame/reg-event-db
  ::select-span
  (fn [db [_ loc doc-id]]
    (let [anns    (:anns db)
          span-id (->> db
                    :spans
                    (model/filter-in-doc doc-id anns)
                    (model/spans-containing-loc loc)
                    vals
                    first
                    :id)]
      (assoc-in db [:selection :span] span-id))))

(re-frame/reg-event-db
  ::select-doc
  (fn [db [_ doc-id]]
    (assoc db :selection {:doc  doc-id
                          :ann  nil
                          :span nil})))

(re-frame/reg-event-db
  ::add-doc
  (fn [db [_]]
    (assoc-in db [:docs :d3]  {:id      :d3
                               :content "I'm the third"})))
(re-frame/reg-event-db
  ::add-ann
  (fn [db [_]]
    (-> db
      (assoc-in [:anns :a3]  {:id :a3
                              :profile (get-in db [:selection :profile])
                              :concept (get-in db [:selection :concept])
                              :doc     (get-in db [:selection :doc])})
      (assoc-in [:spans :s6] {:id    :s6
                              :ann   :a3
                              :end   (get-in db [:selection :end])
                              :start (get-in db [:selection :start])})
      (assoc-in [:selection :ann] :a3)
      (assoc-in [:selection :spn] :s6))))

(re-frame/reg-event-db
  ::remove-selected-doc
  (fn [db [_]]
    (-> db
      (update :docs dissoc (get-in db [:selection :doc]))
      (assoc-in [:selection :doc] (-> db
                                    :docs
                                    keys
                                    first)))))
(re-frame/reg-event-db
  ::remove-selected-ann
  (fn [db [_]]
    (-> db
      (update :anns dissoc (get-in db [:selection :ann]))
      (update :spans dissoc (get-in db [:selection :span]))
      (assoc-in [:selection :ann] nil)
      (assoc-in [:selection :span] nil))))


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

(re-frame/reg-event-db
  ::select-prev-doc
  #(cycle-selection % :doc :docs :prev))

(re-frame/reg-event-db
  ::select-next-doc
  #(cycle-selection % :doc :docs :next))

(re-frame/reg-event-db
  ::select-prev-span
  #(cycle-selection % :span :spans :prev))

(re-frame/reg-event-db
  ::select-next-span
  #(cycle-selection % :span :spans :next))

(defn mod-span
  [db loc f]
  (let [s (subs/selected-span db)]
    (update-in db [:spans s] #(let [{:keys [start end] :as new-s} (update % loc f)]
                                (cond-> new-s
                                  (< end start) (assoc
                                                  :start (:end new-s)
                                                  :end (:start new-s)))))))

(re-frame/reg-event-db
  ::grow-selected-span-start
  #(mod-span % :start dec))

(re-frame/reg-event-db
  ::shrink-selected-span-start
  #(mod-span % :start inc))

(re-frame/reg-event-db
  ::shrink-selected-span-end
  #(mod-span % :end dec))

(re-frame/reg-event-db
  ::grow-selected-span-end
  #(mod-span % :end inc))

(re-frame/reg-event-db
  ::find-in-selected-doc
  (fn [db [_ text]]
    (let [doc-id            (get-in db [:selection :doc])
          doc               (get-in db [:docs doc-id :content])
          last-search-start (get-in db [:spans :last-search-span :start])
          result            (or
                              (str/index-of doc text last-search-start)
                              (str/index-of doc text))]
      (-> db
        (assoc-in [:spans :last-search-span] {:id       :last-search-span
                                              :searched result
                                              :ann      :last-search-ann
                                              :start    result
                                              :end      (when result (+ result (count text)))})
        (assoc-in [:anns :last-search-ann] {:id  :last-search-ann
                                            :doc doc-id})
        (assoc-in [:search :un-searched?] true)))))

(re-frame/reg-event-fx
  ::update-search-text
  (fn [{:keys [db]} [_ text]]
    {:db       (assoc-in db [:search :search-text] text)
     :dispatch [::find-in-selected-doc text]}))

(re-frame/reg-event-db
  ::done-searching
  (fn [db]
    (assoc-in db [:search :un-searched?] false)))
