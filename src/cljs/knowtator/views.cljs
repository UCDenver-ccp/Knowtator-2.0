(ns knowtator.views
  (:require [knowtator.events :as evts]
            [knowtator.owl.views :as owl]
            [knowtator.relation-annotation.controls :as ra-controls]
            [knowtator.relation-annotation.views :as ra]
            [knowtator.relation-annotation.subs :as ra-subs]
            [knowtator.review.views :as review]
            [knowtator.routes :as routes]
            [knowtator.subs :as subs]
            [knowtator.text-annotation.controls :as tac]
            [knowtator.text-annotation.views :as tav]
            [knowtator.util :refer [<sub >evt]]
            [re-com.core :as re-com :refer [at]]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [knowtator.analogy.subs :as ana-subs]
            [knowtator.relation-annotation.events :as ra-evts]
            [knowtator.analogy.events :as ana-evts]
            [knowtator.analogy.views :as ana-views]))


(defn page-title
  []
  [re-com/v-box
    :children
    [[re-com/h-box
       :children
       [[:img
         (let [size "60px"]
           {:src   "https://avatars.githubusercontent.com/u/1854424?s=200&v=4"
            :style {:width  size
                    :height size}})]
        [re-com/title
          :label "Knowtator"
          :level :level1]]]
     [re-com/h-box
       :children
       [[re-com/single-dropdown
          :src       (at)
          :choices   (<sub [::subs/available-projects])
          :label-fn  identity
          :id-fn     identity
          :model     (<sub [::subs/selected-project])
          :on-change #(>evt [::evts/select-project %])]
        [re-com/button
          :src      (at)
          :label    "Open project"
          :on-click #(>evt [::evts/load-project
                            (<sub [::subs/selected-project])])]
        [re-com/alert-list
          :on-close #(println %)
          :alerts
          (->>
           [{:id         :project-loading
             :alert-type :info
             :heading    "Project Status"
             :body       (if (<sub [::subs/loading? :project]) "spinny" "done")}
            (when (<sub [::subs/error? :project])
              {:id         :project-error
               :alert-type :danger
               :heading    "Project load error"})
            {:id         :ontology-loading
             :alert-type :info
             :heading    "Ontology status"
             :body       (if (<sub [::subs/loading? :ontology])
                           "spinny"
                           "done")}
            (when (<sub [::subs/error? :ontology])
              {:id         :ontology-error
               :alert-type :danger
               :heading    "Ontology load error"})]
           (keep identity)
           vec)]]]]])

(defn undo-controls
  []
  [re-com/h-box
    :children [[re-com/md-circle-icon-button
                 :md-icon-name "zmdi-undo"
                 :on-click     #(>evt [:undo])]
               [re-com/md-circle-icon-button
                 :md-icon-name "zmdi-redo"
                 :on-click     #(>evt [:redo])]]])


(defn target-value
  "Returns value found in text field"
  [d]
  (-> d
      (.-target)
      (.-value)))

(defn color-controls
  []
  [re-com/h-box
    :children [[:input#my {:type      :color
                           :class     "rc-button"
                           :value     (<sub [::subs/selected-color])
                           :on-change #(>evt [::evts/set-concept-color
                                              (target-value %)])}]]])


(defn search-controls
  []
  [re-com/h-box
    :children [[re-com/input-text
                 :model           (<sub [::subs/search-query])
                 :on-change       #(>evt [::evts/update-search-text %])
                 ;; :attr {:on-key-press #(when (html/key? % :enter)
                 ;;                         (>evt
                 ;;                         [::events/find-in-selected-doc]))}
                 :change-on-blur? false
                 :placeholder     "search document"]
               [re-com/label
                 :label (<sub [::subs/search-matches])]]])

(defn doc-display
  []
  [re-com/v-box
    :height   "300px"
    :children [[tav/doc-header (<sub [::subs/selected-doc])]
               [re-com/h-split
                 :src           (at)
                 :initial-split 85
                 :panel-1       [tav/editor (<sub [::subs/selected-doc])]
                 :panel-2       [tav/annotation-info]]]])

(defn annotation-panel
  []
  [re-com/v-box
    :src      (at)
    :children [[undo-controls]
               [re-com/h-box
                 :children [[tac/profile-controls] [tac/doc-controls]]]
               [re-com/h-box
                 :children [[color-controls] [tac/ann-controls]]]
               [re-com/h-split
                 :src           (at)
                 :initial-split 25
                 :panel-1       [owl/owl-hierarchies]
                 :panel-2       [doc-display]]
               [re-com/h-box
                 :children [[tac/span-controls] [search-controls]]]]])

(defn filter-controls
  []
  (let [showing? (r/atom false)]
    [:div {:style {:position :fixed
                   :top      0
                   :right    0
                   :z-index  1}}
      [re-com/popover-anchor-wrapper
        :showing? showing?
        :position :below-left
        :anchor   [re-com/button
                    :on-click (re-com/handler-fn (swap! showing? not))
                    :label    "Filters"]
        :popover  [re-com/popover-content-wrapper
                    :title "Filters"
                    :body  [review/filterer]]]]))


(defn review-panel
  []
  [:div
   [re-com/title
     :src   (at)
     :label "Review"
     :level :level1] [review/chooser] [review/table] [filter-controls]])

(defn graph-panel
  []
  [:div
   [re-com/v-box
     :children [[owl/owl-controls]
                [ra-controls/graph-space-controls [::ra-subs/graph-spaces]
                 [::ra-subs/selected-graph-space-id]
                 #(>evt [::ra-evts/select-graph-space %])]
                [ra-controls/node-controls] [ra-controls/edge-controls]
                [re-com/h-box
                  :children [[owl/owl-obj-prop-hierarchy]
                             [ra/graph [::ra-subs/selected-realized-graph]
                              [::ra-subs/selected-graph-space-id]]]]]]])

(defn analogy-panel
  []
  [:div
   [re-com/v-box
     :children [[ra-controls/graph-space-controls [::ana-subs/analogy-graphs]
                 [::ana-subs/selected-analogy-graph-id]
                 #(>evt [::ana-evts/select-graph-id %])]
                [ra-controls/node-controls] [ra-controls/edge-controls]
                [ana-views/concept-graph-chooser] [ana-views/roles-table]
                [ana-views/analogy-graph]]]])

(defmethod routes/panels :review-panel [] [review-panel])
(defmethod routes/panels :annotation-panel [] [annotation-panel])
(defmethod routes/panels :graph-panel [] [graph-panel])
(defmethod routes/panels :analogy-panel [] [analogy-panel])

;; main

(defn main-panel
  []
  (let [active-panel (rf/subscribe [::subs/active-panel])]
    [re-com/v-box
      :src      (at)
      :height   "100%"
      :children [[page-title] (routes/panels @active-panel)]]))
