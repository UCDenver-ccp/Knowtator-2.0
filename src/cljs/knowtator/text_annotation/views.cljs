(ns knowtator.text-annotation.views
  (:require ["rangy/lib/rangy-textrange" :as rangy-txt]
            [knowtator.events :as events]
            [knowtator.subs :as subs]
            [knowtator.util :refer [<sub >evt]]
            [re-com.core :as re-com]
            [reagent.core :as r]
            [re-frame-datatable.core :as dt]))

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
                                           :border           (when (<sub [::subs/selected-span? id]) :solid)
                                           :cursor :pointer}
                                    searched (assoc :color :red))}
                          content])
       :component-did-mount
       (fn [_]
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

(defn doc-header [doc-id]
  [re-com/title
   :label doc-id
   :level :level2])

(defn editor
  [doc-id]
  [re-com/scroller
   :height "300px"
   :child [:div.text-annotation-editor
           {:on-click #(let [selection (text-selection (.-target %) "text-annotation-editor")]
                         (>evt [::events/record-selection selection doc-id]))
            :style    {:padding "10px"}}
           (doall
             (for [paragraph (<sub [::subs/highlighted-text])]
               ^{:key (str (random-uuid))}
               [editor-paragraph paragraph]))]])

(defn annotation-info
  []
  [re-com/v-box
   :children [[re-com/title
               :label "Annotation"
               :level :level3]
              [dt/datatable ::annotation-id [::subs/selected-ann-info]
               [{::dt/column-key   [:prop]
                 ::dt/sorting      {::dt/enabled? true}
                 ::dt/column-label "Property"}
                {::dt/column-key   [:val]
                 ::dt/column-label "Value"}]
               {::dt/table-classes ["table" "ui" "celled"]}]]])
