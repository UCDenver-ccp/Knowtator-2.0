(ns knowtator.relation-annotation.events
  (:require [re-frame.core :refer [reg-event-db]]
            [knowtator.util :as util]))

(reg-event-db ::add-node
  (fn [db [_ x y]]
    (update-in db [:graph :nodes] conj {:id      (count (lazy-cat
                                                          (get-in db [:graph :nodes])
                                                          (get-in db [:graph :edges])))
                                        :x       x
                                        :y       y
                                        :physics false})))

(reg-event-db ::toggle-node-physics
  (fn [db [_ id x y]]
    (when id
      (update-in db [:graph :nodes]
        (fn [nodes]
          (-> (util/map-with-key :id nodes)
            (update-in [id :physics] not)
            (assoc-in [id :x] x)
            (assoc-in [id :y] y)
            vals))))))

(reg-event-db ::toggle-physics
  (fn [db _]
    (update-in db [:graph :physics] not)))
