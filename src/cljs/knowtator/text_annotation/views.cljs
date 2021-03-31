(ns knowtator.text-annotation.views
  (:require ["rangy/lib/rangy-textrange" :as rangy-txt]
            [knowtator.events :as events]
            [knowtator.html-util :as html]
            [knowtator.subs :as subs]
            [knowtator.util :refer [<sub >evt]]
            [re-com.core :as re-com]
            [re-frame-datatable.core :as dt]
            [reagent.core :as r]))

(defn popup-text-annotation
  [{:keys [content id ann]}]
  (let [e-id (random-uuid)]
    (r/create-class
      {:reagent-render (fn []
                         (let [selected? (some #(<sub [::subs/selected-span? %]) (cond-> id (not (coll? id)) vector))]
                           (when-let [e (and selected?
                                          (.getElementById js/document e-id))]
                             (.scrollIntoView e
                               (clj->js {:behavior :smooth  #_ [:auto :smooth]
                                         :block    :center  #_ [:start :center :end :nearest]
                                         :inline   :nearest #_ [:start :center :end :nearest]})))
                           (let [color (<sub [::subs/ann-color ann])]
                             [re-com/p-span
                              {:id    e-id
                               :style (cond-> {:background-color color
                                               :border  :solid
                                               :border-color color
                                               :cursor :pointer}
                                        (coll? id)   (assoc :border-color :grey)
                                        selected?    (assoc :border-color :black)
                                        #_#_searched (assoc :color :red))}
                              content])))
       :component-did-mount
       (fn [_]
         (let [e (.getElementById js/document e-id)]
           e
           #_(when (and (<sub [::subs/un-searched?]) searched)
               (>evt [::events/done-searching])
               (.scrollIntoView e {:behavior :smooth  #_ [:auto :smooth]
                                   :block    :end     #_ [:start :center :end :nearest]
                                   :inline   :nearest #_ [:start :center :end :nearest]}))))})))

(defn editor-paragraph [paragraph]
  [re-com/p
   {:style {:text-align   :justify
            :text-justify :inter-word}}
   (doall
     (for [text paragraph]
       (if (string? text)
         (if (empty? text)
           " "
           text)
         ^{:key (str (random-uuid))}
         [popup-text-annotation text])))])

(defn doc-header [doc-id]
  [re-com/title
   :label doc-id
   :level :level2])

(defn editor
  [doc-id]
  [re-com/scroller
   :height "300px"
   :child [:div.text-annotation-editor
           {:on-click #(let [selection (html/text-selection (.-target %) "text-annotation-editor")]
                         (>evt [::events/record-selection selection doc-id]))
            :style    {:padding "10px"}}
           (doall
             (for [paragraph (<sub [::subs/highlighted-text])]
               ^{:key (str (random-uuid))}
               [:p
                [editor-paragraph paragraph]
                " "]))]])

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
