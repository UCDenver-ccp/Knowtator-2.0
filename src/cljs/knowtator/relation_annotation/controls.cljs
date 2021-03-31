(ns knowtator.relation-annotation.controls
  (:require [knowtator.relation-annotation.subs :as subs]
            [knowtator.text-annotation.events :as text-evts]
            [knowtator.relation-annotation.events :as evts]
            [knowtator.util :refer [<sub >evt]]
            [re-com.core :as re-com :refer [at]]))

(defn graph-space-controls
  []
  [re-com/v-box
   :children [[re-com/h-box
               :src (at)
               :children [[re-com/md-circle-icon-button
                           :md-icon-name "zmdi-arrow-left"
                           :on-click #(>evt [::text-evts/cycle :graphs :prev])]
                          [re-com/md-circle-icon-button
                           :md-icon-name "zmdi-arrow-right"
                           :on-click #(>evt [::text-evts/cycle :graphs :next])]
                          [re-com/md-circle-icon-button
                           :md-icon-name "zmdi-plus"
                           :on-click #(>evt [::evts/add-graph-space])]
                          [re-com/md-circle-icon-button
                           :md-icon-name "zmdi-minus"
                           :on-click #(>evt [::evts/remove-graph-space])]
                          [re-com/single-dropdown
                           :src (at)
                           :choices (<sub [::subs/graph-spaces])
                           :label-fn :id
                           :filter-box? true
                           :model (<sub [::subs/selected-graph-space-id])
                           :on-change #(>evt [::evts/select-graph-space %])]]]
              [re-com/checkbox
               :model (<sub [::subs/graph-physics])
               :label "Physics"
               :on-change #(>evt [::evts/toggle-physics])]]])
