(ns knowtator.relation-annotation.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub ::graph
  :graph)

(reg-sub ::graph-physics
  (fn [db _]
    (true? (get-in db [:graph :physics]))))
