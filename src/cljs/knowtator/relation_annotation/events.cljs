(ns knowtator.relation-annotation.events
  (:require [re-frame.core :refer [reg-event-db]]))

(reg-event-db
  ::add-node
  (fn [db _]
    (update-in db [:graph :nodes] conj {:id (count (lazy-cat
                                                     (get-in db [:graph :nodes])
                                                     (get-in db [:graph :edges])))})))
