(ns knowtator.text-annotation.events
  (:require [day8.re-frame.undo :as undo :refer [undoable]]
            [knowtator.model :as model]
            [re-frame.core :refer [reg-event-db]]))

(reg-event-db ::cycle
  (fn [db [_ coll-id direction]]
    (let [restriction (case coll-id
                        :spans (comp (partial filter #(model/in-restriction? % {:doc [(get-in db [:selection :docs])]}))
                                 (partial model/spans-with-spanned-text (:docs db) (:anns db)))
                        identity)]
      (model/cycle-selection db restriction coll-id direction))))

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
  (fn [db [_]]
    (let [doc-id (model/unique-id db :docs "d" (-> db :docs count))]
      (-> db
        (assoc-in [:docs doc-id]  {:id      doc-id
                                   :content (str "I'm called " doc-id)})
        (assoc-in [:selection :docs] doc-id)))))

(reg-event-db ::add-ann
  (undoable "Adding annotation")
  (fn [db [_]]
    (let [span-id                                    (model/unique-id db :spans "s" (-> db :spans count))
          ann-id                                     (model/unique-id db :anns "a" (-> db :anns count))
          {:keys [profiles concepts docs end start]} (:selection db)]
      (-> db
        (assoc-in [:anns ann-id]  {:id      ann-id
                                   :profile profiles
                                   :concept concepts
                                   :doc     docs})
        (assoc-in [:spans span-id] {:id    span-id
                                    :ann   ann-id
                                    :end   end
                                    :start start})
        (assoc-in [:selection :anns] ann-id)
        (assoc-in [:selection :spans] span-id)))))

(reg-event-db ::remove-selected-doc
  (undoable "Removing document")
  (fn [db [_]]
    (-> db
      (update :docs dissoc (get-in db [:selection :docs]))
      (assoc-in [:selection :docs] (-> db
                                     :docs
                                     keys
                                     first)))))
(reg-event-db ::remove-selected-ann
  (undoable "Removing annotation")
  (fn [db [_]]
    (-> db
      (update :anns dissoc (get-in db [:selection :anns]))
      (update :spans dissoc (get-in db [:selection :spans]))
      (assoc-in [:selection :anns] nil)
      (assoc-in [:selection :spans] nil))))
