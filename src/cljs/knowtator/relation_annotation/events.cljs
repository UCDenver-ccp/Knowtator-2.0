(ns knowtator.relation-annotation.events
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [knowtator.owl.events :as owl]
            [knowtator.text-annotation.events :as txt-evts]
            [knowtator.util :as util]
            [re-frame.core :refer [reg-event-db reg-event-fx]]))

(defn verify-id [db k prefix]
  (->> db
    :text-annotation
    :graphs
    (map (comp count k))
    (reduce +)
    inc
    (str prefix)
    keyword))

(reg-event-db ::add-node
  (fn-traced [db [_ graph-id node]]
    (when-let [ann-id (get-in db [:selection :anns])]
      (let [new-node (merge node
                       {:id      (verify-id db :nodes "n")
                        :label "test"
                        :physics false
                        :ann     ann-id})]
        (println graph-id node new-node)
        (update-in db [:text-annotation :graphs]
          (fn [graphs]
            (-> graphs
              (->> (util/map-with-key :id))
              (update-in [graph-id :nodes] conj new-node)
              vals)))))))

(reg-event-db ::add-edge
  (fn-traced [db [_ graph-id edge]]
    (let [new-edge (merge edge
                     {:id (verify-id db :edges "e")})]
      (update-in db [:text-annotation :graphs]
        (fn [graphs]
          (-> graphs
            (->> (util/map-with-key :id))
            (update-in [graph-id :edges] conj new-edge)
            vals))))))

(reg-event-fx ::select-ann-node
  (fn-traced [{:keys [db]} [_ graph-id node-id]]
    (let [node-id (keyword node-id)
          ann-id  (-> db
                    (get-in [:text-annotation :graphs])
                    (->> (util/map-with-key :id))
                    (get-in [graph-id :nodes])
                    (->> (util/map-with-key :id))
                    (get-in [node-id :ann]))]
      {:db       (-> db
                   (assoc-in [:selection :nodes] node-id)
                   (assoc-in [:selection :anns] ann-id))
       :dispatch [::txt-evts/select-annotation ann-id]})))

(reg-event-fx ::select-relation-ann
  (fn-traced [{:keys [db]} [_ graph-id relation-ann-id]]
    (let [relation-ann-id (keyword relation-ann-id)
          obj-prop-id     (-> db
                            (get-in [:text-annotation :graphs])
                            (->> (util/map-with-key :id))
                            (get-in [graph-id :edges])
                            (->> (util/map-with-key :id))
                            (get-in [relation-ann-id :predicate :property]))]
      {:db       (-> db
                   (assoc-in [:selection :relation-ann] relation-ann-id))
       :dispatch [::owl/select-owl-obj-prop obj-prop-id]})))

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

(reg-event-db ::toggle-display-ann-node-owl-class
  (fn [db _]
    (let [graph-id (get-in db [:selection :graphs])]
      (update-in db [:text-annotation :graphs]
        (fn [graphs]
          (-> graphs
            (->> (util/map-with-key :id))
            (update-in [graph-id :display-owl-class?] not)
            vals))))))

(reg-event-db ::select-graph-space
  (fn [db [_ id]]
    (assoc-in db [:selection :graphs] id)))

(reg-event-db ::set-edge-length
  (fn [db [_ v]]
    (assoc-in db [:selection :edge-length] v)))
