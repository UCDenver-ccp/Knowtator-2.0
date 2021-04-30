(ns knowtator.analogy.subs
  (:require [re-frame.core :as rf :refer [reg-sub]]
            [knowtator.model :as model]
            [knowtator.util :as util]
            [com.rpl.specter :as sp]
            [mops.core :as mops]
            [mops.records :as mr]
            [sme-clj.typedef :as types]
            [re-frame-datatable.core :as dt]))

(def default-mop-map
  (as-> (mr/make-mop-map) m
    (types/initialize-kg m)
    (reduce
     (partial apply types/add-entity)
     m
     [[:mass ::types/Function nil ::types/Entity]
      [:charge ::types/Function nil ::types/Entity]
      [:attracts ::types/Function nil ::types/Entity ::types/Entity]
      [:revolve-around ::types/Function nil ::types/Entity ::types/Entity]
      [:temperature ::types/Function nil ::types/Entity]
      [:gravity ::types/Function nil ::types/Expression ::types/Expression]
      [:opposite-sign ::types/Function nil ::types/Expression
       ::types/Expression]
      [:greater ::types/Relation nil ::types/Entity ::types/Entity]
      [:cause ::types/Relation nil ::types/Expression ::types/Expression]
      [:and ::types/Relation {:ordered? false} ::types/Expression
       ::types/Expression] [:Sun ::types/Entity nil]
      [:Planet ::types/Entity nil] [:Nucleus ::types/Entity nil]
      [:Electron ::types/Entity nil]])
    (apply types/add-concept-graph
           m
           :solar-system
           (let [attracts    [:attracts :Sun :Planet]
                 mass-sun    [:mass :Sun]
                 mass-planet [:mass :Planet]]
             [[:cause [:and [:greater mass-sun mass-planet] attracts]
               [:revolve-around :Planet :Sun]]
              [:greater [:temperature :Sun] [:temperature :Planet]]
              [:cause [:gravity mass-sun mass-planet] attracts]]))
    (apply types/add-concept-graph
           m
           :rutherford-atom
           [[:greater [:mass :Nucleus] [:mass :Electron]]
            [:revolve-around :Electron :Nucleus]
            [:cause [:opposite-sign [:charge :Nucleus] [:charge :Electron]]
             [:attracts :Nucleus :Electron]]])
    (mops/infer-hierarchy m)))

(reg-sub ::analogy-graphs
  (fn [_ _]
    (->> [(-> default-mop-map
              (assoc :id :default))]
         (sort-by (comp name :id) util/compare-alpha-num)
         (#(or % [])))))

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

(reg-sub ::selected-graph
  :<- [::selected-mop-map]
  (fn [mm [_ slots]]
    (-> mm
        (model/mop-map->graph slots))))

(reg-sub ::selected-analogy-graph
  (fn [[_ slots] _] [(rf/subscribe [::selected-graph slots])
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

(reg-sub ::selected-slots
  (fn [[_ roles-dt _] _] [(rf/subscribe [::dt/selected-items roles-dt
                                         [::selected-mop-map-roles]])])
  (fn [[roles] [_ _ fillers-dt]]
    (->>
     roles
     (map :id)
     (reduce (fn [m id]
               (let [selected-fillers (set (map :id
                                                @(rf/subscribe
                                                  [::dt/selected-items
                                                   (table-name fillers-dt id)
                                                   [::fillers-for-role id]])))]
                 (assoc m id selected-fillers)))
             {}))))

(reg-sub ::selected-graph-panels
  :<- [::selection]
  (fn [selected _] (get selected :graph-panels)))

(reg-sub ::selected-graphs
  :<- [::selection]
  (fn [selected _] (get selected :graphs)))
