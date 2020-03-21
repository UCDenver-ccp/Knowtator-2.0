(ns knowtator.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core :as re-com]
   [breaking-point.core :as bp]
   [knowtator.subs :as subs]
   [knowtator.events :as evts]
   [knowtator.html-util :as html]
   ["rangy/lib/rangy-textrange" :as rangy-txt]))

;; Editor

(defn editor-foredrop
  []
  (let [text (re-frame/subscribe [::subs/selected-span-content])]
   [:div#foredrop.foredrop {:style {:z-index 3
                                    :background-color :transparent}}
    [:div.highlights
     (doall
       (for [text @text]
         (if (string? text)
           text
           (let [{:keys [id content selected]} text]
             ^{:key (str (random-uuid))}
             [:div.popup {:style {#_:border #_:solid
                                  :color :black
                                  :border-color :black}}
              (when selected
                [:span.popuptext.show {:style {:z-index 3}
                                       :id (str id "popup")}
                 id])
              content]))))]]))

(defn class?
  [e c]
  (-> e .-classList (.contains c)))

(defn get-parent-element-of-class
  [e c]
  (if (class? e c)
    e
    (recur (.-parentElement e) c)))

(defn text-selection
  [e]
  (let [e (get-parent-element-of-class e "text-annotation-editor")]
    (-> rangy-txt .getSelection (.saveCharacterRanges e) (js->clj :keywordize-keys true) first :characterRange)))

(defn editor
  []
  (let [doc-id(re-frame/subscribe [::subs/visible-doc-id])
        text (re-frame/subscribe [::subs/highlighted-text])]
   [:div
    ;; [document-controls]
    #_[annotation-controls]
    [:h2 @doc-id]
    [:div.text-annotation-editor {:on-click #(re-frame.core/dispatch [::evts/record-selection (text-selection (.-target %)) @doc-id])
                                      :read-only true
                                      :on-scroll #(html/unify-scroll "textarea" "backdrop" "foredrop")}
     (doall
       (for [text @text]
         (if (string? text)
           text
           (let [{:keys [content ann]} text]
             ^{:key (str (random-uuid))}
             [:span {:style {:background-color @(re-frame/subscribe [::subs/ann-color ann])}}
              content]))))]]))

;; home

(defn display-re-pressed-example []
  (let [re-pressed-example (re-frame/subscribe [::subs/re-pressed-example])]
    [:div
     [:p
      [:span "Re-pressed is listening for keydown events. A message will be displayed when you type "]
      [:strong [:code "hello"]]
      [:span ". So go ahead, try it out!"]]

     (when-let [rpe @re-pressed-example]
       [re-com/alert-box
        :alert-type :info
        :body rpe])]))

(defn home-title []
  (let [name (re-frame/subscribe [::subs/name])]
    [re-com/title
     :label "Knowtator"
     :level :level1]))

(defn link-to-about-page []
  [re-com/hyperlink-href
   :label "go to About Page"
   :href "#/about"])

(defn home-panel []
  [re-com/v-box
   :gap "1em"
   :children [[home-title]
              [editor]
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
    :home-panel [home-panel]
    :about-panel [about-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [re-com/v-box
     :height "100%"
     :children [[panels @active-panel]]]))
