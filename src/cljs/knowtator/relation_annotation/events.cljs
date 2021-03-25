(ns knowtator.relation-annotation.events
  (:require [re-frame.core :refer [reg-event-db]]
            [knowtator.util :as util]))

(reg-event-db ::add-node
  (fn [db [_ node]]
    (update-in db [:graph :nodes] conj (merge node
                                         {:id      (-> db
                                                     (get-in [:graph :nodes])
                                                     count
                                                     inc
                                                     (->> (str "n"))
                                                     keyword)
                                          :physics false
                                          :ann     :a1}))))

(reg-event-db ::toggle-node-physics
  (fn [db [_ id x y]]
    (when id
      (update-in db [:graph :nodes]
        (fn [nodes]
          (let [nodes (util/map-with-key :id nodes)]
            (-> nodes
              (cond-> (contains? nodes id) (->
                                             (update-in [id :physics] not)
                                             (assoc-in [id :x] x)
                                             (assoc-in [id :y] y)))
              vals)))))))

(reg-event-db ::toggle-physics
  (fn [db _]
    (update-in db [:graph :physics] not)))
