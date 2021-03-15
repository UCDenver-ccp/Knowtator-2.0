(ns knowtator.text-annotation.controls
  (:require [knowtator.subs :as subs]
            [knowtator.text-annotation.events :as evts]
            [knowtator.util :refer [<sub >evt]]
            [re-com.core :as re-com]))

(defn profile-controls
  []
  [re-com/h-box
   :children [[re-com/md-circle-icon-button
               :md-icon-name "zmdi-plus"
               :on-click #(>evt [::evts/add-profile])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-minus"
               :on-click #(>evt [::evts/remove-selected-profile])]
              [re-com/single-dropdown
               :choices (<sub [::subs/profiles])
               :label-fn :id
               :filter-box? true
               :model (<sub [::subs/selected-profile])
               :on-change #(>evt [::evts/select-profile %])]
              [re-com/checkbox
               :model (<sub [::subs/profile-restriction?])
               :label "Restrict to profile"
               :on-change #(>evt [::evts/toggle-profile-restriction])]]])

(defn doc-controls
  []
  [re-com/h-box
   :children [[re-com/md-circle-icon-button
               :md-icon-name "zmdi-arrow-left"
               :on-click #(>evt [::evts/cycle :docs :prev])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-arrow-right"
               :on-click #(>evt [::evts/cycle :docs :next])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-plus"
               :on-click #(>evt [::evts/add-doc])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-minus"
               :on-click #(>evt [::evts/remove-selected-doc])]
              [re-com/single-dropdown
               :choices (<sub [::subs/docs])
               :label-fn :id
               :filter-box? true
               :model (<sub [::subs/selected-doc])
               :on-change #(>evt [::evts/select-doc %])]]])

(defn ann-controls
  []
  [re-com/h-box
   :children [[re-com/md-circle-icon-button
               :md-icon-name "zmdi-plus"
               :on-click #(>evt [::evts/add-ann])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-minus"
               :on-click #(>evt [::evts/remove-selected-ann])]]])

(defn span-controls
  []
  [re-com/h-box
   :children [[re-com/md-circle-icon-button
               :md-icon-name "zmdi-arrow-left"
               :on-click #(>evt [::evts/cycle :spans :prev])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-arrow-right"
               :on-click #(>evt [::evts/cycle :spans :next])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-plus"
               :on-click #(>evt [::evts/grow-selected-span-start])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-minus"
               :on-click #(>evt [::evts/shrink-selected-span-start])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-minus"
               :on-click #(>evt [::evts/shrink-selected-span-end])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-plus"
               :on-click #(>evt [::evts/grow-selected-span-end])]]])
