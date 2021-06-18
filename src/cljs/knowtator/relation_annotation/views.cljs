(ns knowtator.relation-annotation.views
  (:require [knowtator.relation-annotation.events :as evts]
            [knowtator.relation-annotation.subs :as subs]
            [knowtator.util :as util :refer [<sub >evt]]
            [reagent.core :as r]
            ["react-graph-vis" :as rgv]))

(def visjs-graph (aget rgv "default"))

(defn handler-fn [f] (fn [e] (f (js->clj e :keywordize-keys true))))

(defn manipulation-handler-fn
  [f]
  (fn [data callback] (callback (f (js->clj data :keywordize-keys true)))))

(defn vis
  [id
   graph
   {:keys [options events style]
    {:keys [width height]
     :or   {height "100%"
            width  "100%"}}
    :style}]
  (when-let [graph (<sub graph)]
    [(r/adapt-react-class visjs-graph)
     {:id      id
      :graph   graph
      :options (-> options
                   (update :manipulation
                           (partial util/map-vals manipulation-handler-fn))
                   (assoc-in [:manipulation :enabled] true)
                   (assoc :auto-resize true))
      :events  (util/map-vals handler-fn events)
      :style   (-> style
                   (assoc :height height :width width :position :absolute))}]))

(defn graph
  [graph-id graph-sub graph-id-sub & [options]]
  [:div
   {:style {:position :relative
            :height   "100%"
            :width    "100%"}}
   [vis
    graph-id
    graph-sub
    (merge-with
     merge
     {:options {:layout       {:hierarchical false}
                :edges        {:color "#000000"}
                :physics      {:enabled    (<sub [::subs/graph-physics])
                               :barnes-hut {:spring-length
                                            (<sub [::subs/edge-length])}}
                :interaction  {:hover true}
                :manipulation {:add-node    (fn [node-data]
                                              (>evt [::evts/add-node
                                                     (<sub graph-id-sub)
                                                     node-data]))
                               :add-edge    (fn [edge-data]
                                              (>evt [::evts/add-edge
                                                     (<sub graph-id-sub)
                                                     edge-data]))
                               :edit-node   (fn [node-data] (println node-data))
                               :edit-edge   (fn [edge-data] (println edge-data))
                               :delete-node (fn [node-ids] (println node-ids))
                               :delete-edge (fn [edge-ids] (println edge-ids))}}
      :events  {:click       (fn [{:keys                   [nodes]
                                   {{:keys [x y]} :canvas} :pointer}]
                               (>evt [::evts/toggle-node-physics
                                      (<sub graph-id-sub)
                                      (first nodes)
                                      x
                                      y]))
                :select-node (fn [{:keys [nodes]}]
                               (>evt [::evts/select-ann-node
                                      (<sub graph-id-sub)
                                      (first nodes)]))
                :select-edge (fn [{:keys [edges]}]
                               (>evt [::evts/select-relation-ann
                                      (<sub graph-id-sub)
                                      (first edges)]))}
      :style   {:background-color "white"}}
     options)]])
