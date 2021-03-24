(ns knowtator.relation-annotation.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  ::graph
  (fn [db]
    (:graph db)))
