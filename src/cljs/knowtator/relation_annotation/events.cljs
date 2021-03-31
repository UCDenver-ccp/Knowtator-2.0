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
    (println "here")
    (let [new-node (merge node
                     {:id      (verify-id db)
                      :physics false
                      :ann     :a1})]
      (println graph-id node new-node)
      (update-in db [:text-annotation :graphs]
        (fn [graphs]
          (-> graphs
            (->> (util/map-with-key :id))
            (update-in [graph-id :nodes] conj new-node)
            vals))))))

(reg-event-db ::toggle-node-physics
  (fn [db [_ graph-id id x y]]
    (when id
      (update-in db [:text-annotation :graphs]
        (fn [graphs]
          (-> graphs
            (->> (util/map-with-key :id))
            (update-in [graph-id :nodes]
              (fn [nodes]
                (let [nodes (util/map-with-key :id nodes)]
                  (-> nodes
                    (cond-> (contains? nodes id) (->
                                                   (update-in [id :physics] not)
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
