(ns knowtator.relation-annotation.controls
  (:require
   [knowtator.relation-annotation.subs :as subs]
   [knowtator.text-annotation.events :as text-evts]
   [knowtator.relation-annotation.events :as evts]
   [knowtator.util :refer [<sub >evt]]
   [re-com.core :as    re-com
                :refer [at]]))

(defn graph-space-controls
  [graphs-sub graph-model-sub graph-event]
  [re-com/v-box
   :children [[re-com/h-box
               :src      (at)
               :children [[re-com/md-circle-icon-button
                           :md-icon-name "zmdi-arrow-left"
                           :on-click     #(>evt [::text-evts/cycle
                                                 :graphs :prev])]
                          [re-com/md-circle-icon-button
                           :md-icon-name "zmdi-arrow-right"
                           :on-click     #(>evt [::text-evts/cycle
                                                 :graphs :next])]
                          [re-com/md-circle-icon-button
                           :md-icon-name "zmdi-plus"
                           :on-click     #(>evt [::evts/add-graph-space])]
                          [re-com/md-circle-icon-button
                           :md-icon-name "zmdi-minus"
                           :on-click     #(>evt [::evts/remove-graph-space])]
                          [re-com/h-box
                           :children [[re-com/label
                                       :label "Graph spaces"]
                                      [re-com/single-dropdown
                                       :src         (at)
                                       :choices     (<sub graphs-sub)
                                       :label-fn    :id
                                       :filter-box? true
                                       :model       (<sub graph-model-sub)
                                       :on-change   graph-event]]]]]]])

(defn node-controls
  []
  [re-com/h-box
   :src      (at)
   :children [[re-com/checkbox
               :model     (<sub [::subs/display-ann-node-owl-class?])
               :label     "Display OWL class"
               :on-change #(>evt [::evts/toggle-display-ann-node-owl-class])]
              [re-com/checkbox
               :model     (<sub [::subs/graph-physics])
               :label     "Physics"
               :on-change #(>evt [::evts/toggle-physics])]]])

(defn edge-controls
  []
  [re-com/slider
   :model     (<sub [::subs/edge-length])
   :on-change #(>evt [::evts/set-edge-length %])
   :min       1
   :max       500])
