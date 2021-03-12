(ns knowtator.views
  (:require [breaking-point.core :as bp]
            [knowtator.events :as events]
            [knowtator.routes :as routes]
            [knowtator.subs :as subs]
            [re-com.core :as re-com :refer [at]]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [knowtator.util :refer [>evt <sub]]
            [knowtator.text-annotation.views :as text-ann]
            ["react-graph-vis" :as rgv]))

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
   :on-click #(rf/dispatch [::events/navigate :about])])

(defn home-panel []
  [re-com/v-box
   :src      (at)
   :gap      "1em"
   :children [[home-title]
              [link-to-about-page]
              [display-re-pressed-example]
              [:div
               [:h3 (str "screen-width: " @(rf/subscribe [::bp/screen-width]))]
               [:h3 (str "screen: " @(rf/subscribe [::bp/screen]))]]]])

(defmethod routes/panels :home-panel [] [home-panel])

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
   :on-click #(rf/dispatch [::events/navigate :home])])

(defn about-panel []
  [re-com/v-box
   :src      (at)
   :gap      "1em"
   :children [[about-title]
              [link-to-home-page]]])

(defmethod routes/panels :about-panel [] [about-panel])

(defn graph-panel []
  (let [options {:layout {:hierarchical false}
                 :edges  {:color "#000000"}}
        events  {:select       (fn [e]
                                 (let [e                     (js->clj e :keywordize-keys true)
                                       {:keys [nodes edges]} e]
                                   (println "Nodes:" nodes)
                                   (println "Edges:" edges)))
                 :double-click (fn [e]
                                 (let [{:keys [x y]} (-> e
                                                       (js->clj :keywordize-keys true)
                                                       (get-in [:pointer :canvas]))]
                                   (println x y)
                                   (rf/dispatch [::events/add-node])))}]

    [:div
     [(r/adapt-react-class (aget rgv "default"))
      {:graph   (<sub [::subs/graph])
       :options options
       :events  events
       :style   {:height "640px"}}]]))

(defmethod routes/panels :graph-panel [] [graph-panel])

(defn annotation-title []
  [re-com/title
   :label "Knowtator"
   :level :level1])

(defn undo-controls
  []
  [re-com/h-box
   :children [[re-com/md-circle-icon-button
               :md-icon-name "zmdi-undo"
               :on-click #(>evt [::events/undo])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-redo"
               :on-click #(>evt [::events/redo])]]])

(defn profile-controls
  []
  [re-com/h-box
   :children [[re-com/md-circle-icon-button
               :md-icon-name "zmdi-plus"
               :on-click #(>evt [::events/add-profile])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-minus"
               :on-click #(>evt [::events/remove-selected-profile])]
              [re-com/single-dropdown
               :choices (<sub [::subs/profile-maps])
               :label-fn :id
               :filter-box? true
               :model (<sub [::subs/selected-profile])
               :on-change #(>evt [::events/select-profile %])]
              [re-com/checkbox
               :model (<sub [::subs/profile-restriction?])
               :label "Restrict to profile"
               :on-change #(>evt [::events/toggle-profile-restriction])]]])

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
                          :on-change #(>evt [::events/set-concept-color (target-value %)])}]]])

(defn document-controls
  []
  [re-com/h-box
   :children [[re-com/md-circle-icon-button
               :md-icon-name "zmdi-arrow-left"
               :on-click #(>evt [::events/select-prev-doc])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-arrow-right"
               :on-click #(>evt [::events/select-next-doc])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-plus"
               :on-click #(>evt [::events/add-doc])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-minus"
               :on-click #(>evt [::events/remove-selected-doc])]
              [re-com/single-dropdown
               :choices (<sub [::subs/doc-maps])
               :label-fn :id
               :filter-box? true
               :model (<sub [::subs/selected-doc])
               :on-change #(>evt [::events/select-doc %])]]])

(defn annotation-controls
  []
  [re-com/h-box
   :children [[re-com/md-circle-icon-button
               :md-icon-name "zmdi-plus"
               :on-click #(>evt [::events/add-ann])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-minus"
               :on-click #(>evt [::events/remove-selected-ann])]]])

(defn span-controls
  []
  [re-com/h-box
   :children [[re-com/md-circle-icon-button
               :md-icon-name "zmdi-arrow-left"
               :on-click #(>evt [::events/select-prev-span])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-arrow-right"
               :on-click #(>evt [::events/select-next-span])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-plus"
               :on-click #(>evt [::events/grow-selected-span-start])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-minus"
               :on-click #(>evt [::events/shrink-selected-span-start])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-minus"
               :on-click #(>evt [::events/shrink-selected-span-end])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-plus"
               :on-click #(>evt [::events/grow-selected-span-end])]]])

(defn search-controls
  []
  [re-com/h-box
   :children [[re-com/input-text
               :model (<sub [::subs/search-query])
               :on-change #(>evt [::events/update-search-text %])
               ;; :attr {:on-key-press #(when (html/key? % :enter)
               ;;                         (>evt [::events/find-in-selected-doc]))}
               :change-on-blur? false
               :placeholder "search document"]
              [re-com/label
               :label (<sub [::subs/search-matches])]]])

(defn doc-display []
  [:div
   [text-ann/doc-header (<sub [::subs/selected-doc])]
   [re-com/h-split
    :src (at)
    :width (str (- (<sub [::bp/screen-width]) 50) "px")
    :panel-1 [text-ann/editor (<sub [::subs/selected-doc])]
    :panel-2 [text-ann/annotation-info (<sub [::subs/selected-ann])]]])

(defn annotation-panel []
  [re-com/v-box
   :src (at)
   :children [[annotation-title]
              [undo-controls]
              [profile-controls]
              [color-controls]
              [document-controls]
              [annotation-controls]
              [span-controls]
              [search-controls]
              [doc-display]]])

(defmethod routes/panels :annotation-panel [] [annotation-panel])

;; main

(defn main-panel []
  (let [active-panel (rf/subscribe [::subs/active-panel])]
    [re-com/v-box
     :src      (at)
     :height   "100%"
     :children [(routes/panels @active-panel)]]))
