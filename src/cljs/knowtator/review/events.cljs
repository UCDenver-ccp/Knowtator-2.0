(ns knowtator.review.events
  (:require
   [re-frame.core :refer [reg-event-db]]
   [cljs.reader :as reader]))

(reg-event-db
  ::select-review-type
  (fn [db [_ id]]
    (assoc-in db [:selection :review-type] id)))

(reg-event-db
  ::select-review-filter-type
  (fn [db [_ filt-type]]
    (assoc-in db [:selection :review-filter-type] filt-type)))

(reg-event-db
  ::set-review-filter
  (fn [db [_ filt-str]]
    (let [filt-type (get-in db [:selection :review-filter-type])
          filt      (reader/read-string filt-str)]
      (if filt
        (->> filt
          keyword
          (assoc-in db [:selection :review-filter filt-type]))
        (update-in db [:selection :review-filter] dissoc filt-type)))))
