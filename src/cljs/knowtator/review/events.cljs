(ns knowtator.review.events
  (:require
   [re-frame.core :refer [reg-event-db]]))

(reg-event-db
  ::select-review-type
  (fn [db [_ id]]
    (assoc-in db [:selection :review-type] id)))

(reg-event-db
  ::set-review-filter
  (fn [db [_ filt]]
    (assoc-in db [:selection :review-filter] filt)))
