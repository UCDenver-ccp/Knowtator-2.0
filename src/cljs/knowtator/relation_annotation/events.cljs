(ns knowtator.relation-annotation.events
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [knowtator.owl.events :as owl]
            [knowtator.text-annotation.events :as txt-evts]
            [knowtator.util :as util]
            [re-frame.core :refer [reg-event-db reg-event-fx]]
            [com.rpl.specter :as sp]
            [knowtator.model :as model]))

(def GRAPHS (sp/comp-paths :text-annotation :graphs sp/NIL->VECTOR))

(reg-event-db ::add-graph-space
  (fn-traced [db [_ graph-space-id]]
    (let [graph-space-id (model/unique-id (->> db
                                            (sp/select [:text-annotation
                                                        :graphs sp/ALL])
                                            (map :id)
                                            set)
                           "g"
                           0)]
      (-> db
        (assoc-in [:selection :graphs] graph-space-id)
        (assoc-in [:selection :anns]
          (sp/select-one [:text-annotation :anns sp/FIRST :id] db))
        (->> (sp/transform GRAPHS
               #(conj %
                  {:id    graph-space-id
                   :nodes []
                   :edges []})))))))

(reg-event-db ::add-node
  (fn-traced [db [_ graph-space-id node]]
    (model/add-node db graph-space-id node)))

(reg-event-db ::add-edge
  (fn-traced [db [_ graph-space-id edge]]
    (model/add-edge db graph-space-id edge)))

(reg-event-fx ::select-ann-node
  (fn-traced [{:keys [db]} [_ graph-id node-id]]
    (let [node-id (keyword node-id)
          ann-id (-> db
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
          obj-prop-id (-> db
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
      (update-in
        db
        [:text-annotation :graphs]
        (fn [graphs]
          (-> graphs
            (->> (util/map-with-key :id))
            (update-in
              [graph-id :nodes]
              (fn [nodes]
                (let [nodes (util/map-with-key :id nodes)
                      id (keyword id)]
                  (-> nodes
                    (cond-> (contains? nodes id)
                      (-> (update-in [id :physics]
                            (fn [val]
                              (if (false? val) true false)))
                        (assoc-in [id :x] x)
                        (assoc-in [id :y] y)))
                    vals))))
            vals))))))

(reg-event-db ::toggle-physics
  (fn [db _]
    (let [graph-id (get-in db [:selection :graphs])]
      (update-in db
        [:text-annotation :graphs]
        (fn [graphs]
          (-> graphs
            (->> (util/map-with-key :id))
            (update-in [graph-id :physics] not)
            vals))))))

(reg-event-db ::toggle-display-ann-node-owl-class
  (fn [db _]
    (let [graph-id (get-in db [:selection :graphs])]
      (update-in db
        [:text-annotation :graphs]
        (fn [graphs]
          (-> graphs
            (->> (util/map-with-key :id))
            (update-in [graph-id :display-owl-class?] not)
            vals))))))

(reg-event-db ::select-graph-space
  (fn [db [_ id]] (assoc-in db [:selection :graphs] id)))

(reg-event-db ::set-edge-length
  (fn [db [_ v]] (assoc-in db [:selection :edge-length] v)))
