(ns knowtator.text-annotation.events
  (:require [day8.re-frame.undo :as undo :refer [undoable]]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [knowtator.model :as model]
            [re-frame.core :refer [reg-event-db]]
            [knowtator.util :as util]))

(reg-event-db ::cycle
  (fn [db [_ coll-id direction]]
    (let [restriction (case coll-id
                        :spans (->> db
                                 model/realize-spans
                                 :spans
                                 (filter #(model/in-restriction? % {:doc [(get-in db [:selection :docs])]})))
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
      (update-in [:text-annotation :anns] (fn [spans]
                                            (-> spans
                                              (->> (util/map-with-key :id))
                                              (dissoc (get-in db [:selection :anns]))
                                              vals)))
      (update-in [:text-annotation :spans] (fn [spans]
                                             (-> spans
                                               (->> (util/map-with-key :id))
                                               (dissoc (get-in db [:selection :spans]))
                                               vals)))
      (assoc-in [:selection :anns] nil)
      (assoc-in [:selection :spans] nil))))
