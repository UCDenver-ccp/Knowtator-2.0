(ns knowtator.views
  (:require
   [re-frame.core :as re-frame]
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
  [{:keys [ann content id]}]
  [re-com/p-span
   {:style {:background-color (<sub [::subs/ann-color ann])
            :border           (when (<sub [::subs/selected-span? id]) :solid)}}
   content])

(defn text-annotation
  [{:keys [content ann]}]
  [re-com/p-span {:style {:background-color (<sub [::subs/ann-color ann])}}
   content])

(defn editor
  [doc-id]
  [:div.text-annotation-editor {:on-click #(>evt [::evts/record-selection (html/text-selection (.-target %) "text-annotation-editor") doc-id])}
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
              [popup-text-annotation text])))]))])


(defn document-controls
  []
  [re-com/h-box
   :children [[re-com/md-circle-icon-button
               :md-icon-name "zmdi-plus"
               :on-click #(>evt [::evts/add-doc])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-minus"
               :on-click #(>evt [::evts/remove-selected-doc])]
              [re-com/single-dropdown
               :choices (<sub [::subs/doc-ids])
               :model (<sub [::subs/visible-doc-id])
               :on-change #(>evt [::evts/select-doc %])]]])

(defn annotation-controls
  []
  [re-com/h-box
   :children [[re-com/md-circle-icon-button
               :md-icon-name "zmdi-plus"
               :on-click #(>evt [::evts/add-ann])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-minus"
               :on-click #(>evt [::evts/remove-selected-ann])]]])

(defn home-panel
  []
  [:div
   {:width (<sub [::bp/screen-width])}
   [home-title]
   [document-controls]
   [annotation-controls]
   [doc-header]
   [editor (<sub [::subs/visible-doc-id])]])

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
   :children [[panels (<sub [::subs/active-panel])]]])
