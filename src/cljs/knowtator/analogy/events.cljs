(ns knowtator.analogy.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db trim-v]]))

(reg-event-db ::select-graph-space
  trim-v
  (fn [db [id]] (assoc-in db [:selection :analogy-graphs] id)))

(reg-event-db ::select-concept-graph
  trim-v
  (fn [db [id]] (assoc-in db [:selection :concept-graphs] id)))

(reg-event-db ::add-graph-panel
  trim-v
  (fn [db _]
    (update-in db
               [:selection :graph-panels]
               (comp (partial apply conj) (juxt identity count)))))
