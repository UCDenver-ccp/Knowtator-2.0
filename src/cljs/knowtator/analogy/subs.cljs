(ns knowtator.analogy.subs
  (:require [re-frame.core :as rf :refer [reg-sub]]
            [knowtator.model :as model]
            [com.rpl.specter :as sp]
            [mops.core :as mops]))

(reg-sub ::analogy-graphs :analogy)

(reg-sub ::selected-mop-map
  :<- [::analogy-graphs]
  :<- [::selected-analogy-graph-id]
  (fn [[graphs id] _]
    (->> graphs
         (sp/select-one [(sp/filterer #(= id (:id %))) sp/FIRST]))))

(reg-sub ::selection :selection)

(reg-sub ::selected-analogy-graph-id
  :<- [::selection]
  (fn [selected _] (:ana-graphs selected)))

(reg-sub ::selected-node-label (fn [_ _] :id))
(reg-sub ::selected-edge-label (fn [_ _] :predicate))

(reg-sub ::slots
  (fn [db [_ graph-id]]
    (sp/select-one [:graph-panels (sp/filterer #(= (:id %) graph-id)) sp/FIRST
                    :fillers]
                   db)))

(reg-sub ::filtered-mop-map
  (fn [[_ graph-id] _] [(rf/subscribe [::selected-mop-map])
                        (rf/subscribe [::slots graph-id])])
  (fn [[mm slots] _]
    (-> mm
        (model/filter-mops slots))))

(reg-sub ::selected-graph
  (fn [[_ graph-id] _] (rf/subscribe [::filtered-mop-map graph-id]))
  (fn [mm _]
    (-> mm
        model/mop-map->graph)))

(reg-sub ::selected-analogy-graph
  (fn [[_ graph-id] _] [(rf/subscribe [::selected-graph graph-id])
                        (rf/subscribe [::selected-node-label])
                        (rf/subscribe [::selected-edge-label])])
  (fn [[graph node-label edge-label] _]
    (-> graph
        (update :nodes (partial map #(assoc % :label (get % node-label))))
        (update :edges (partial map #(assoc % :label (get % edge-label))))
        (#(or %
              {:nodes []
               :edges []})))))

(defn all-roles
  [mm]
  (->> mm
       :mops
       vals
       (reduce
        (fn [m mop]
          (->> mop
               (reduce (fn [m [role fillers]]
                         (update m
                                 role
                                 (fn [role-m]
                                   (-> role-m
                                       (update :fillers (fnil into #{}) fillers)
                                       (update :mops (fnil conj #{}) mop)
                                       (assoc :mop (mops/get-mop mm role))
                                       (assoc :id role)))))
                       m)))
        {})))

(reg-sub ::selected-mop-map-roles-map
  :<- [::selected-mop-map]
  (fn [mm _]
    (-> mm
        all-roles)))

(reg-sub ::selected-mop-map-roles
  :<- [::selected-mop-map-roles-map]
  (fn [m _]
    (-> m
        vals)))

(reg-sub ::fillers-for-role
  :<- [::selected-mop-map-roles-map]
  (fn [roles-map [_ role]]
    (->> (get-in roles-map
                 [role :fillers])
         (map (partial hash-map :id)))))

(defn table-name
  [base-name ext]
  (keyword (namespace base-name) (str (name base-name) "-" ext)))

(reg-sub ::graph-panels (fn [db _] (map :id (get db :graph-panels))))

(reg-sub ::selected-graphs
  :<- [::selection]
  (fn [selected _] (get selected :graphs)))

(reg-sub ::selected-roles
  (fn [db [_ graph-id]]
    (sp/select-one [:graph-panels (sp/filterer #(= (:id %) graph-id)) sp/FIRST
                    :roles]
                   db)))

(reg-sub ::selected-role?
  (fn [[_ _ graph-id] _] (rf/subscribe [::selected-roles graph-id]))
  (fn [roles [_ role _]] (contains? roles role)))

(reg-sub ::selected-fillers
  (fn [db [_ role graph-id]]
    (sp/select-one [:graph-panels (sp/filterer #(= (:id %) graph-id)) sp/FIRST
                    :fillers role]
                   db)))

(reg-sub ::selected-filler?
  (fn [[_ _ role graph-id] _] (rf/subscribe [::selected-fillers role graph-id]))
  (fn [roles [_ filler _ _]] (contains? roles filler)))
