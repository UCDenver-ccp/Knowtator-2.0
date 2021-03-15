(ns knowtator.text-annotation.events
  (:require [day8.re-frame.undo :as undo :refer [undoable]]
            [knowtator.model :as model]
            [re-frame.core :refer [reg-event-db]]))

(reg-event-db
  ::select-prev-doc
  #(model/cycle-selection % :doc :docs :prev))

(reg-event-db
  ::select-next-doc
  #(model/cycle-selection % :doc :docs :next))

(reg-event-db
  ::select-prev-span
  #(model/cycle-selection % :span :spans :prev))

(reg-event-db
  ::select-next-span
  #(model/cycle-selection % :span :spans :next))

(reg-event-db
  ::grow-selected-span-start
  (undoable "Growing span start")
  #(model/mod-span % :start dec))

(reg-event-db
  ::shrink-selected-span-start
  (undoable "Shrinking span start")
  #(model/mod-span % :start inc))

(reg-event-db
  ::shrink-selected-span-end
  (undoable "Shrinking span end")
  #(model/mod-span % :end dec))

(reg-event-db
  ::grow-selected-span-end
  (undoable "Growing span end")
  #(model/mod-span % :end inc))

(reg-event-db
  ::select-doc
  (fn [db [_ doc-id]]
    (assoc-in db [:selection :doc] doc-id)))

(reg-event-db
  ::select-profile
  (fn [db [_ profile-id]]
    (assoc-in db [:selection :profile] profile-id)))

(reg-event-db
  ::toggle-profile-restriction
  (fn [db]
    (update-in db [:selection :profile-restriction] not)))

(reg-event-db
  ::add-doc
  (undoable "Adding document")
  (fn [db [_]]
    (let [doc-id (model/unique-id db :docs "d" (-> db :docs count))]
      (-> db
        (assoc-in [:docs doc-id]  {:id      doc-id
                                   :content (str "I'm called " doc-id)})
        (assoc-in [:selection :doc] doc-id)))))

(reg-event-db
  ::add-ann
  (undoable "Adding annotation")
  (fn [db [_]]
    (let [span-id                                 (model/unique-id db :spans "s" (-> db :spans count))
          ann-id                                  (model/unique-id db :anns "a" (-> db :anns count))
          {:keys [profile concept doc end start]} (:selection db)]
      (-> db
        (assoc-in [:anns ann-id]  {:id      ann-id
                                   :profile profile
                                   :concept concept
                                   :doc     doc})
        (assoc-in [:spans span-id] {:id    span-id
                                    :ann   ann-id
                                    :end   end
                                    :start start})
        (assoc-in [:selection :ann] ann-id)
        (assoc-in [:selection :span] span-id)))))

(reg-event-db
  ::remove-selected-doc
  (undoable "Removing document")
  (fn [db [_]]
    (-> db
      (update :docs dissoc (get-in db [:selection :doc]))
      (assoc-in [:selection :doc] (-> db
                                    :docs
                                    keys
                                    first)))))
(reg-event-db
  ::remove-selected-ann
  (undoable "Removing annotation")
  (fn [db [_]]
    (-> db
      (update :anns dissoc (get-in db [:selection :ann]))
      (update :spans dissoc (get-in db [:selection :span]))
      (assoc-in [:selection :ann] nil)
      (assoc-in [:selection :span] nil))))
