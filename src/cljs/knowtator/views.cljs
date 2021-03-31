(ns knowtator.views
  (:require [breaking-point.core :as bp]
            [knowtator.events :as evts]
            [knowtator.relation-annotation.controls :as ra-controls]
            [knowtator.relation-annotation.events :as ra-events]
            [knowtator.relation-annotation.subs :as ra-subs]
            [knowtator.relation-annotation.views :as ra]
            [knowtator.review.views :as review]
            [knowtator.routes :as routes]
            [knowtator.subs :as subs]
            [knowtator.text-annotation.controls :as tac]
            [knowtator.text-annotation.views :as tav]
            [knowtator.util :refer [<sub >evt]]
            [re-com.core :as re-com :refer [at]]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn display-re-pressed-example []
  (let [re-pressed-example (<sub [::subs/re-pressed-example])]
    [:div

     [:p
      [:span "Re-pressed is listening for keydown events. A message will be displayed when you type "]
      [:strong [:code "hello"]]
      [:span ". So go ahead, try it out!"]]

     (when-let [rpe re-pressed-example]
       [re-com/alert-box
        :src        (at)
        :alert-type :info
        :body       rpe])]))

(defn home-title []
  (let [name (rf/subscribe [::subs/name])]
    [re-com/title
     :src   (at)
     :label (str "Hello from " @name ". This is the Home Page.")
     :level :level1]))

(defn link-to-about-page []
  [re-com/hyperlink
   :src      (at)
   :label    "go to About Page"
   :on-click #(rf/dispatch [::evts/navigate :about])])

;; about

(defn about-title []
  [re-com/title
   :src   (at)
   :label "This is the About Page."
   :level :level1])

(defn link-to-home-page []
  [re-com/hyperlink
   :src      (at)
   :label    "go to Home Page"
   :on-click #(rf/dispatch [::evts/navigate :home])])

(defn about-panel []
  [re-com/v-box
   :src      (at)
   :gap      "1em"
   :children [[about-title]
              [link-to-home-page]]])


(defn graph-panel []
  [:div
   [re-com/v-box
    :children [
               [re-com/checkbox
                :model (<sub [::ra-subs/graph-physics])
                :label "Physics"
                :on-change #(>evt [::ra-events/toggle-physics])]
               [ra-controls/graph-space-controls]
               [ra/graph]]]])


(defn page-title []
  [re-com/h-box
   :children [[:img (let [size "60px"]
                      {:src   "https://avatars.githubusercontent.com/u/1854424?s=200&v=4"
                       :style {:width  size
                               :height size}})]
              [re-com/title
               :label "Knowtator"
               :level :level1]]])

(defn annotation-title []
  [re-com/title
   :label "Knowtator"
   :level :level1])

(defn undo-controls
  []
  [re-com/h-box
   :children [[re-com/md-circle-icon-button
               :md-icon-name "zmdi-undo"
               :on-click #(>evt [:undo])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-redo"
               :on-click #(>evt [:redo])]]])


(defn target-value
  "Returns value found in text field"
  [d]
  (-> d (.-target) (.-value)))

(defn color-controls
  []
  [re-com/h-box
   :children [[:input#my {:type      :color
                          :class     "rc-button"
                          :value     (<sub [::subs/selected-color])
                          :on-change #(>evt [::evts/set-concept-color (target-value %)])}]]])


(defn search-controls
  []
  [re-com/h-box
   :children [[re-com/input-text
               :model (<sub [::subs/search-query])
               :on-change #(>evt [::evts/update-search-text %])
               ;; :attr {:on-key-press #(when (html/key? % :enter)
               ;;                         (>evt [::events/find-in-selected-doc]))}
               :change-on-blur? false
               :placeholder "search document"]
              [re-com/label
               :label (<sub [::subs/search-matches])]]])

(defn doc-display []
  [re-com/v-box
   :height "300px"
   :children [[tav/doc-header (<sub [::subs/selected-doc])]
              [re-com/h-split
               :src (at)
               :initial-split 85
               :panel-1[tav/editor (<sub [::subs/selected-doc])]
               :panel-2 [tav/annotation-info]]]])

(defn annotation-panel []
  [re-com/v-box
   :src (at)
   :children [[annotation-title]
              [undo-controls]
              [re-com/h-box
               :children [[tac/profile-controls]
                          [tac/doc-controls]]]
              [re-com/h-box
               :children [[color-controls]
                          [tac/ann-controls]]]
              [doc-display]
              [re-com/h-box
               :children [[tac/span-controls]
                          [search-controls]]]]])

(defn filter-controls []
  (let [showing? (r/atom false)]
    [:div {:style {:position :fixed
                   :top      0
                   :right    0
                   :z-index  1}}
     [re-com/popover-anchor-wrapper
      :showing? showing?
      :position :below-left
      :anchor [re-com/button
               :on-click (re-com/handler-fn (swap! showing? not))
               :label "Filters"]
      :popover [re-com/popover-content-wrapper
                :title "Filters"
                :body [review/filterer]]]]))


(defn review-panel []
  [:div
   [re-com/title
    :src   (at)
    :label "Review"
    :level :level1]
   [review/chooser]
   [review/table]
   [filter-controls]])

(defmethod routes/panels :review-panel [] [review-panel])
(defmethod routes/panels :annotation-panel [] [annotation-panel])
(defmethod routes/panels :graph-panel [] [graph-panel])
(defmethod routes/panels :about-panel [] [about-panel])

;; main

(defn main-panel []
  (let [active-panel (rf/subscribe [::subs/active-panel])]
    [re-com/v-box
     :src      (at)
     :height   "100%"
     :children [[page-title]
                (routes/panels @active-panel)]]))
