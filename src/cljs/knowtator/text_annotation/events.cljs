(ns knowtator.text-annotation.events
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [day8.re-frame.undo :as undo :refer [undoable]]
            [knowtator.model :as model]
            [knowtator.owl.events :as owl-evts]
            [knowtator.util :as util]
            [re-frame.core :refer [reg-event-db reg-event-fx]]))

(defn filter-in-doc
  [db coll-id]
  (->> db
    model/realize-spans
    :text-annotation
    coll-id
    (filter #(model/in-restriction? % {:filter-tye    :doc
                                       :filter-values #{(get-in db [:selection :docs])}}))))

(reg-event-fx ::cycle
  (fn-traced [{:keys [db]} [_ coll-id direction]]
    (let [coll        (->> (cond-> db
                             (#{:spans :anns} coll-id) (filter-in-doc coll-id))
                        (sort-by (juxt :start :end :id)))
          new-item-id (model/cycle-selection db coll coll-id direction)]
      (cond (#{:spans} coll-id) {:db       db
                                 :dispatch [::select-span new-item-id]}
            :else               {:db (assoc-in db [:selection coll-id] new-item-id)}))))

(reg-event-fx ::select-span
  (fn-traced [{:keys [db]} [_ id]]
    (let [{:keys [ann]} (->> db
                          :text-annotation
                          :spans
                          (util/map-with-key :id)
                          id)]
      {:db       (assoc-in db [:selection :spans] id)
       :dispatch [::select-annotation ann]})))

(reg-event-fx ::select-annotation
  (fn-traced [{:keys [db]} [_ id]]
    (let [{:keys [concept]} (->> db
                              :text-annotation
                              :anns
                              (util/map-with-key :id)
                              id)]
      {:db       (assoc-in db [:selection :anns] id)
       :dispatch [::owl-evts/select-owl-class concept]})))

(reg-event-db ::grow-selected-span-start
  (undoable "Growing span start")
  #(model/mod-span % :start dec))

(reg-event-db ::shrink-selected-span-start
  (undoable "Shrinking span start")
  #(model/mod-span % :start inc))

(reg-event-db ::shrink-selected-span-end
  (undoable "Shrinking span end")
  #(model/mod-span % :end dec))

(reg-event-db ::grow-selected-span-end
  (undoable "Growing span end")
  #(model/mod-span % :end inc))

(reg-event-db ::select-doc
  (fn [db [_ doc-id]]
    (assoc-in db [:selection :docs] doc-id)))

(reg-event-db ::select-profile
  (fn [db [_ profile-id]]
    (assoc-in db [:selection :profiles] profile-id)))

(reg-event-db ::toggle-profile-restriction
  (fn [db]
    (update-in db [:selection :profile-restriction] not)))

(reg-event-db ::add-doc
  (undoable "Adding document")
  (fn-traced [{{:keys [docs]} :text-annotation :as db} [_]]
    (let [doc-id (model/unique-id (util/map-with-key docs :id) "d" (count docs))]
      (-> db
        (assoc-in [:text-annotation :docs doc-id]  {:id      doc-id
                                                    :content (str "I'm called " doc-id)})
        (assoc-in [:selection :docs] doc-id)))))

(reg-event-db ::add-ann
  (undoable "Adding annotation")
  (fn-traced [{{:keys [spans anns]}                       :text-annotation
               {:keys [profiles concepts docs start end]} :selection
               :as                                        db} [_]]
    (let [span-id (model/unique-id (util/map-with-key :id spans) "s" (count spans))
          ann-id  (model/unique-id (util/map-with-key :id anns) "a" (count anns))]
      (-> db
        (update-in [:text-annotation :anns] conj  {:id      ann-id
                                                   :profile profiles
                                                   :concept concepts
                                                   :doc     docs})
        (update-in [:text-annotation :spans] conj {:id    span-id
                                                   :ann   ann-id
                                                   :end   end
                                                   :start start})
        (assoc-in [:selection :anns] ann-id)
        (assoc-in [:selection :spans] span-id)))))

(defn remove-selected-item
  [db k]
  (let [selected-id (get-in db [:selection k])]
    (-> db
      (update-in [:text-annotation k] (comp vals #(dissoc % selected-id) (partial util/map-with-key :id)))
      (assoc-in [:selection k] nil))))

(reg-event-db ::remove-selected-doc
  (undoable "Removing document")
  (fn [db [_]]
    (-> db
      (remove-selected-item :docs)
      (assoc-in [:selection :docs] (-> db
                                     :docs
                                     keys
                                     first)))))

(reg-event-db ::remove-selected-ann
  (undoable "Removing annotation")
  (fn [db [_]]
    (-> db
      (remove-selected-item :anns)
      (remove-selected-item :spans))))
