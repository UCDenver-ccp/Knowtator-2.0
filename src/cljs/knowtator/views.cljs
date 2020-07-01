(ns knowtator.views
  (:require
   [reagent.core :as reagent]
   [re-com.core :as re-com]
   [breaking-point.core :as bp]
   [knowtator.subs :as subs]
   [knowtator.events :as evts]
   [knowtator.html-util :as html :refer [<sub >evt]]))

(defn home-title []
  [re-com/title
   :label "Knowtator"
   :level :level1])

(defn doc-header []
  [re-com/title
   :label (<sub [::subs/visible-doc-id])
   :level :level2])

;; Editor

(defn popup-text-annotation
  [{:keys [ann content id searched]}]
  (let [e-id (random-uuid)]
    (reagent/create-class
      {:reagent-render      (fn []
                              [re-com/p-span
                               {:id    e-id
                                :style (cond-> {:background-color (<sub [::subs/ann-color ann])
                                                :border           (when (<sub [::subs/selected-span? id]) :solid)}
                                         searched (assoc :color :red))}
                               content])
       :component-did-mount (fn [comp]
                              (let [e (.getElementById js/document e-id)]
                                (when (and (<sub [::subs/un-searched?]) searched)
                                  (>evt [::evts/done-searching])
                                  (.scrollIntoView e))))})))

(defn text-annotation
  [{:keys [content ann]}]
  [re-com/p-span {:style {:background-color (<sub [::subs/ann-color ann])}}
   content])

(defn editor
  [doc-id]
  [:div
   [doc-header]
   [re-com/scroller
    :height "300px"
    :child [:div.text-annotation-editor {:on-click  #(>evt [::evts/record-selection (html/text-selection (.-target %) "text-annotation-editor") doc-id])
                                         :style {:padding "10px"}}
            (doall
              (for [paragraph (<sub [::subs/highlighted-text])]
                ^{:key (str (random-uuid))}
                [re-com/p
                 {:style {:text-align   :justify
                          :text-justify :inter-word}}
                 (doall
                   (for [text paragraph]
                     (if (string? text)
                       text
                       ^{:key (str (random-uuid))}
                       [popup-text-annotation text])))]))]]])


(defn document-controls
  []
  [re-com/h-box
   :children [[re-com/md-circle-icon-button
               :md-icon-name "zmdi-arrow-left"
               :on-click #(>evt [::evts/select-prev-doc])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-arrow-right"
               :on-click #(>evt [::evts/select-next-doc])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-plus"
               :on-click #(>evt [::evts/add-doc])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-minus"
               :on-click #(>evt [::evts/remove-selected-doc])]
              [re-com/single-dropdown
               :choices (<sub [::subs/doc-maps])
               :label-fn :id
               :filter-box? true
               :model (<sub [::subs/visible-doc-id])
               :on-change #(>evt [::evts/select-doc %])]]])

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
               :choices (<sub [::subs/profile-maps])
               :label-fn :id
               :filter-box? true
               :model (<sub [::subs/selected-profile])
               :on-change #(>evt [::evts/select-profile %])]]])

(defn annotation-controls
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
               :on-click #(>evt [::evts/select-prev-span])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-arrow-right"
               :on-click #(>evt [::evts/select-next-span])]
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

(defn annotation-info
  []
  [re-com/v-box
   :children [[re-com/title
               :label "Annotation"
               :level :level3]
              (let [ann (<sub [::subs/selected-ann-map])]
                (for [[k v] ann]
                  ^{:key (str (random-uuid))}
                  [re-com/h-box
                   :gap "10px"
                   :children [[re-com/label
                               :label (name k)]
                              [re-com/label
                               :label (str v)]]]))]])

(defn search-area
  []
  [re-com/h-box
   :children [[re-com/input-text
               :model (<sub [::subs/search-text])
               :on-change #(>evt [::evts/update-search-text %])
               ;; :attr {:on-key-press #(when (html/key? % :enter)
               ;;                         (>evt [::evts/find-in-selected-doc]))}
               :change-on-blur? false
               :placeholder "search document"]
              [re-com/label
               :label (<sub [::subs/search-matches])]]])

(defn undo-controls
  []
  [re-com/h-box
   :children [[re-com/md-circle-icon-button
               :md-icon-name "zmdi-undo"
               :disabled? (not (<sub [:undos?]))
               :on-click #(>evt [:undo])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-redo"
               :disabled? (not (<sub [:redos?]))
               :on-click #(>evt [:redo])]]])

(defn home-panel
  []
  [re-com/v-box
   :children [[home-title]
              [undo-controls]
              [profile-controls]
              [document-controls]
              [annotation-controls]
              [span-controls]
              [search-area]
              [re-com/h-box
               :width (str (- (<sub [::bp/screen-width]) 50) "px")
               :children [[editor (<sub [::subs/visible-doc-id])]
                          [annotation-info]]]]])

;; home

(defn display-re-pressed-example []
  [:div
   [:p
    [:span "Re-pressed is listening for keydown events. A message will be displayed when you type "]
    [:strong [:code "hello"]]
    [:span ". So go ahead, try it out!"]]

   (when-let [rpe (<sub [::subs/re-pressed-example])]
     [re-com/alert-box
      :alert-type :info
      :body rpe])])


(defn link-to-about-page []
  [re-com/hyperlink-href
   :label "go to About Page"
   :href "#/about"])

#_(defn home-panel []
    [re-com/v-box
     :gap "1em"
     :children [[home-title]
                [text-annotation]
                [link-to-about-page]
                [display-re-pressed-example]
                [:div
                 [:h3 (str "screen-width: " @(re-frame/subscribe [::bp/screen-width]))]
                 [:h3 (str "screen: " @(re-frame/subscribe [::bp/screen]))]]]])

;; about

(defn about-title []
  [re-com/title
   :label "This is the About Page."
   :level :level1])

(defn link-to-home-page []
  [re-com/hyperlink-href
   :label "go to Home Page"
   :href "#/"])

(defn about-panel []
  [re-com/v-box
   :gap "1em"
   :children [[about-title]
              [link-to-home-page]]])

;; main

(defn- panels [panel-name]
  (case panel-name
    :home-panel  [home-panel]
    :about-panel [about-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  [re-com/v-box
   :height "100%"
   :width (str (<sub [::bp/screen-width]) "px")
   :children [[panels (<sub [::subs/active-panel])]]])
