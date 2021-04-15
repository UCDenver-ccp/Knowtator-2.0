(ns knowtator.relation-annotation.views
  (:require [knowtator.relation-annotation.events :as evts]
            [knowtator.relation-annotation.subs :as subs]
            [knowtator.util :as util :refer [<sub >evt]]
            [reagent.core :as r]
            ["react-graph-vis" :as rgv]))

(def visjs-graph (aget rgv "default"))

(defn handler-fn
  [f]
  (fn [e]
    (f (js->clj e :keywordize-keys true))))

(defn manipulation-handler-fn
  [f]
  (fn [data callback]
    (callback (f (js->clj data :keywordize-keys true)))))

(defn vis [id graph & {:keys [options events style]
                       :or   {style {:height "640px"}}}]
  (when-let [graph (<sub graph)]
    [(r/adapt-react-class visjs-graph)
     {:graph   graph
      :options (-> options
                 (update :manipulation (partial util/map-vals manipulation-handler-fn))
                 (assoc-in [:manipulation :enabled] true))
      :events  (util/map-vals handler-fn events)
      :style   style}]))

(defn graph []
  [vis :relation-annotation-graph [::subs/selected-realized-graph]
   :options {:layout       {:hierarchical false}
             :edges        {:color "#000000"}
             :physics      {:enabled    (<sub [::subs/graph-physics])
                            :barnes-hut {:spring-length (<sub [::subs/edge-length])}}
             :interaction  {:hover true}
             :manipulation {:add-node    (fn [node-data]
                                           (println node-data)
                                           (>evt [::evts/add-node (<sub [::subs/selected-graph-space-id]) node-data]))
                            :add-edge    (fn [edge-data]
                                           (println edge-data)
                                           (>evt [::evts/add-edge (<sub [::subs/selected-graph-space-id]) edge-data]))
                            :edit-node   (fn [node-data]
                                           (println node-data))
                            :edit-edge   (fn [edge-data]
                                           (println edge-data))
                            :delete-node (fn [node-ids]
                                           (println node-ids))
                            :delete-edge (fn [edge-ids]
                                           (println edge-ids))}}
   :events  {:click       (fn [{:keys                   [nodes]
                               {{:keys [x y]} :canvas} :pointer}]
                            (>evt [::evts/toggle-node-physics (<sub [::subs/selected-graph-space-id]) (first nodes) x y]))
             :select-node (fn [{:keys [nodes]}]
                            (>evt [::evts/select-ann-node (<sub [::subs/selected-graph-space-id]) (first nodes)]))
             :select-edge (fn [{:keys [edges]}]
                            (>evt [::evts/select-relation-ann (<sub [::subs/selected-graph-space-id]) (first edges)]))}])