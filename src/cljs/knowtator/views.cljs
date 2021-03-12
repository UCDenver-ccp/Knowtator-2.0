(ns knowtator.views
  (:require
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [re-com.core :as re-com :refer [at]]
   [breaking-point.core :as bp]
   [knowtator.events :as events]
   [knowtator.routes :as routes]
   [knowtator.subs :as subs]
   ["react-graph-vis" :as rgv]
   #_["rangy/lib/rangy-textrange" :as rangy-txt]
   ))

(def <sub (comp deref re-frame/subscribe))
(def >evt re-frame/dispatch)


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
        :src        (at)
        :alert-type :info
        :body       rpe])]))

(defn home-title []
  (let [name (re-frame/subscribe [::subs/name])]
    [re-com/title
     :src   (at)
     :label (str "Hello from " @name ". This is the Home Page.")
     :level :level1]))

(defn link-to-about-page []
  [re-com/hyperlink
   :src      (at)
   :label    "go to About Page"
   :on-click #(re-frame/dispatch [::events/navigate :about])])

(defn home-panel []
  [re-com/v-box
   :src      (at)
   :gap      "1em"
   :children [[home-title]
              [link-to-about-page]
              [display-re-pressed-example]
              [:div
               [:h3 (str "screen-width: " @(re-frame/subscribe [::bp/screen-width]))]
               [:h3 (str "screen: " @(re-frame/subscribe [::bp/screen]))]]
              ]])

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
   :on-click #(re-frame/dispatch [::events/navigate :home])])

(defn about-panel []
  [re-com/v-box
   :src      (at)
   :gap      "1em"
   :children [[about-title]
              [link-to-home-page]]])

(defmethod routes/panels :about-panel [] [about-panel])

(defn graph-panel []
  (let [name    (re-frame/subscribe [::subs/name])
        graph   (re-frame/subscribe [::subs/graph])
        options {:layout {:hierarchical false}
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
                                   (re-frame/dispatch [::events/add-node])))}]

    [:div
     [(r/adapt-react-class (aget rgv "default"))
      {:graph   @graph
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

(defn doc-header [doc-id]
  [re-com/title
   :label doc-id
   :level :level2])

(defn class?
  [e c]
  (-> e .-classList (.contains c)))

(defn get-parent-element-of-class
  [e c]
  (if (class? e c)
    e
    (recur (.-parentElement e) c)))

(defn text-selection
  [e c]
  (let [e (get-parent-element-of-class e c)]
    #_(-> rangy-txt .getSelection (.saveCharacterRanges e) (js->clj :keywordize-keys true) first :characterRange)))

(defn popup-text-annotation
  [{:keys [ann content id searched]}]
  (let [e-id (random-uuid)]
    (r/create-class
      {:reagent-render (fn []
                         ;; TODO always scrolls e to top
                         (when-let [e (and (<sub [::subs/selected-span? id])
                                        (.getElementById js/document e-id))]
                           (.scrollIntoView e))
                         [re-com/p-span
                          {:id    e-id
                           :style (cond-> {:background-color (<sub [::subs/ann-color ann])
                                           :border           (when (<sub [::subs/selected-span? id]) :solid)}
                                    searched (assoc :color :red))}
                          content])
       :component-did-mount
       (fn [comp]
         (let [e (.getElementById js/document e-id)]
           (when (and (<sub [::subs/un-searched?]) searched)
             (>evt [::events/done-searching])
             (.scrollIntoView e))))})))

(defn editor-paragraph [paragraph]
  [re-com/p
   {:style {:text-align   :justify
            :text-justify :inter-word}}
   (doall
     (for [text paragraph]
       (if (string? text)
         text
         ^{:key (str (random-uuid))}
         [popup-text-annotation text])))])

(defn editor
  [doc-id]
  [:div
   [doc-header doc-id]
   [re-com/scroller
    :height "300px"
    :child [:div.text-annotation-editor
            {:on-click #(let [selection (text-selection (.-target %) "text-annotation-editor")]
                          (>evt [::events/record-selection selection doc-id]))
             :style    {:padding "10px"}}
            (doall
              (for [paragraph (<sub [::subs/highlighted-text])]
                ^{:key (str (random-uuid))}
                [editor-paragraph paragraph]))]]])

(defn annotation-info
  [ann-id]
  [re-com/v-box
   :children [[re-com/title
               :label "Annotation"
               :level :level3]
              (let [ann (<sub [::subs/ann-info ann-id])]
                (for [[k v] ann]
                  ^{:key (str (random-uuid))}
                  [re-com/h-box
                   :gap "10px"
                   :children [[re-com/label
                               :label (name k)]
                              [re-com/label
                               :label (str v)]]]))]])

(defn doc-display []
  [re-com/h-box
   :width (str (- (<sub [::bp/screen-width]) 50) "px")
   :children [[editor (<sub [::subs/selected-doc])]
              [annotation-info (<sub [::subs/selected-ann])]]])



(defn annotation-panel []
  [re-com/v-box
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
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [re-com/v-box
     :src      (at)
     :height   "100%"
     :children [(routes/panels @active-panel)]]))
