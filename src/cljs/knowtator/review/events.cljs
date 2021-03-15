(ns knowtator.review.events
  (:require
   [re-frame.core :refer [reg-event-db]]))

(reg-event-db
  ::select-review-type
  (fn [db [_ id]]
    (assoc-in db [:selection :review-type] id)))
