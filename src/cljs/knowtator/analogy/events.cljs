(ns knowtator.analogy.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db trim-v]]))

(reg-event-db ::select-graph-space
  (fn [db [_ id]] (assoc-in db [:selection :analogy-graphs] id)))
