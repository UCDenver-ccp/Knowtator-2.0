(ns knowtator.review.events
  (:require
   [re-frame.core :refer [reg-event-db]]
   [cljs.reader :as reader]
   [knowtator.util :as util]
   [clojure.set :as set]))

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
        (update-in db [:selection :review-filters filt-type] (fnil update {:filter-type filt-type}) :filter-values (fnil conj #{}))))))

(reg-event-db
  ::update-review-filter-item
  (fn [db [_ old-val new-val-str filt-type]]
    (let [new-val (reader/read-string new-val-str)]
      (if new-val
        (update-in db [:selection :review-filters filt-type :filter-values] set/rename-keys {old-val (keyword new-val)})
        (update-in db [:selection :review-filters filt-type :filter-values] disj old-val)))))
