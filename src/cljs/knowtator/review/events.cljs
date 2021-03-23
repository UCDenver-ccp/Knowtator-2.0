(ns knowtator.review.events
  (:require
   [re-frame.core :refer [reg-event-db]]
   [cljs.reader :as reader]
   [knowtator.util :as util]))

(reg-event-db
  ::select-review-type
  (fn [db [_ id]]
    (assoc-in db [:selection :review-type] id)))

(reg-event-db
  ::select-review-filter-type
  (fn [db [_ filt-type]]
    (assoc-in db [:selection :review-filter-type] filt-type)))

(reg-event-db
  ::add-review-filter
  (fn [db [_ filt-str]]
    (let [filt-type (get-in db [:selection :review-filter-type])
          filt      (reader/read-string filt-str)]
      (->> filt
        keyword
        (update-in db [:selection :review-filter filt-type] (fnil conj []))))))

(reg-event-db
  ::update-review-filter-item
  (fn [db [_ filt-str filt-type filt-item-i]]
    (let [filt (reader/read-string filt-str)]
      (if filt
        (->> filt
          keyword
          (assoc-in db [:selection :review-filter filt-type filt-item-i]))
        (update-in db [:selection :review-filter filt-type] util/vec-remove filt-item-i)))))
