(ns knowtator.relation-annotation.events
  (:require [knowtator.util :as util]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame.core :refer [reg-event-db]]))

(defn verify-id [db]
  (->> db
    :text-annotation
    :graphs
    (map (comp count :nodes))
    (reduce +)
    inc
    (str "n")
    keyword))

(reg-event-db ::add-node
  (fn-traced [db [_ graph-id node]]
    (when-let [ann-id (get-in db [:selection :anns])]
      (let [new-node (merge node
                       {:id      (verify-id db)
                        :physics false
                        :ann     ann-id})]
        (println graph-id node new-node)
        (update-in db [:text-annotation :graphs]
          (fn [graphs]
            (-> graphs
              (->> (util/map-with-key :id))
              (update-in [graph-id :nodes] conj new-node)
              vals)))))))

(reg-event-db ::select-ann-node
  (fn-traced [db [_ graph-id node-id]]
    (let [node-id (keyword node-id)
          ann-id  (-> db
                    (get-in [:text-annotation :graphs])
                    (->> (util/map-with-key :id))
                    (get-in [graph-id :nodes])
                    (->> (util/map-with-key :id))
                    (get-in [node-id :ann]))]
      (-> db
        (assoc-in [:selection :nodes] node-id)
        (assoc-in [:selection :anns] ann-id)))))

(reg-event-db ::toggle-node-physics
  (fn-traced [db [_ graph-id id x y]]
    (when id
      (update-in db [:text-annotation :graphs]
        (fn [graphs]
          (-> graphs
            (->> (util/map-with-key :id))
            (update-in [graph-id :nodes]
              (fn [nodes]
                (let [nodes (util/map-with-key :id nodes)
                      id    (keyword id)]
                  (-> nodes
                    (cond-> (contains? nodes id) (->
                                                   (update-in [id :physics] (fn [val] (if (false? val)
                                                                                       true
                                                                                       false)))
                                                   (assoc-in [id :x] x)
                                                   (assoc-in [id :y] y)))
                    vals))))
            vals))))))

(reg-event-db ::toggle-physics
  (fn [db _]
    (let [graph-id (get-in db [:selection :graphs])]
      (update-in db [:text-annotation :graphs]
        (fn [graphs]
          (-> graphs
            (->> (util/map-with-key :id))
            (update-in [graph-id :physics] not)
            vals))))))

(reg-event-db ::select-graph-space
  (fn [db [_ id]]
    (assoc-in db [:selection :graphs] id)))
