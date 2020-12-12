(ns knowtator.views
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [re-com.core :as re-com]
   [breaking-point.core :as bp]
   [re-pressed.core :as rp]
   [knowtator.events :as evts]
   [knowtator.subs :as subs]
   ["rangy/lib/rangy-textrange" :as rangy-txt]))

(def <sub (comp deref re-frame/subscribe))
(def >evt re-frame/dispatch)

(defn home-title []
  [re-com/title
   :label "Knowtator"
   :level :level1])

(defn undo-controls
  []
  [re-com/h-box
   :children [[re-com/md-circle-icon-button
               :md-icon-name "zmdi-undo"
               :on-click #(>evt [::evts/undo])]
              [re-com/md-circle-icon-button
               :md-icon-name "zmdi-redo"
               :on-click #(>evt [::evts/redo])]]])

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
               :on-change #(>evt [::evts/select-profile %])]
              [re-com/checkbox
               :model (<sub [::subs/profile-restriction?])
               :label "Restrict to profile"
               :on-change #(>evt [::evts/toggle-profile-restriction])]]])

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
               :model (<sub [::subs/selected-doc])
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

(defn search-controls
  []
  [re-com/h-box
   :children [[re-com/input-text
               :model (<sub [::subs/search-query])
               :on-change #(>evt [::evts/update-search-text %])
               ;; :attr {:on-key-press #(when (html/key? % :enter)
               ;;                         (>evt [::evts/find-in-selected-doc]))}
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
    (-> rangy-txt .getSelection (.saveCharacterRanges e) (js->clj :keywordize-keys true) first :characterRange)))

(defn popup-text-annotation
  [{:keys [ann content id searched]}]
  (let [e-id (random-uuid)]
    (reagent/create-class
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
             (>evt [::evts/done-searching])
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
                          (>evt [::evts/record-selection selection doc-id]))
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

(defn home-panel []
  [re-com/v-box
   :children [[home-title]
              [undo-controls]
              [profile-controls]
              [color-controls]
              [document-controls]
              [annotation-controls]
              [span-controls]
              [search-controls]
              [doc-display]]])

(defn main-panel []
  [re-com/v-box
   :height "100%"
   :width (str (<sub [::bp/screen-width]))
   :children [(case (<sub [::subs/active-panel])
                :home [home-panel]
                ["No active panel"])]])
