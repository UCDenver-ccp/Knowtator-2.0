(ns knowtator.review.views
  (:require [knowtator.review.events :as evts]
            [knowtator.review.subs :as subs]
            [knowtator.util :refer [<sub >evt]]
            [re-com.core :as re-com]
            [re-frame-datatable.core :as dt]))

(defn filterer
  []
  [re-com/v-box
    :children
      [[re-com/h-box
         :children [[re-com/single-dropdown
                      :model     (<sub [::subs/selected-review-filter-type])
                      :choices   (<sub [::subs/review-filter-types])
                      :on-change #(>evt [::evts/select-review-filter-type %])]
                    [re-com/input-text
                      :model     ""
                      :on-change #(>evt [::evts/add-review-filter %])]]]
       [dt/datatable
         ::filter-table [::subs/flattened-review-filters]
         [{::dt/column-key   [:filter-type]
           ::dt/sorting      {::dt/enabled? true}
           ::dt/column-label "Type"
           ::dt/render-fn    (fn [val]
                               (:label (<sub [::subs/review-filter-type val])))}
          {::dt/column-key   [:item]
           ::dt/column-label "Value"
           ::dt/render-fn    (fn [val {:keys [filter-type]}]
                               [re-com/input-text
                                 :model     (str (name val))
                                 :on-change #(>evt
                                               [::evts/update-review-filter-item
                                                val % filter-type])])}]
           {::dt/table-classes ["table" "ui" "celled"]
            ::dt/selection     {::dt/enabled? true}}]]])

(defn chooser
  []
  [re-com/single-dropdown
    :choices   (<sub [::subs/review-types])
    :model     (<sub [::subs/selected-review-type])
    :on-change #(>evt [::evts/select-review-type %])])

(defn table
  []
  [(fn [cols] ; Anonymous function used to force re-rendering of table columns
     (when cols
       [dt/datatable
         ::text-annotation-table [::subs/values-to-review
                                  [::dt/selected-items
                                   ::filter-table
                                   [::subs/flattened-review-filters]]]
         (for [col cols]
           {::dt/column-key   [(keyword col)]
            ::dt/sorting      {::dt/enabled? true}
            ::dt/column-label col})
           {::dt/table-classes ["ui" "table" "celled"]
            ::dt/selection {::dt/enabled? true}
            ::dt/footer-component
              (fn [] [:tr
                      [:th {:col-span 6}
                        [:strong
                         "Total selected: "
                         (count
                           (<sub
                             [::dt/selected-items
                              ::text-annotation-table
                              [::subs/values-to-review
                               [::dt/selected-items
                                ::filter-table
                                [::subs/flattened-review-filters]]]]))]]])}]))
   (<sub [::subs/selected-review-columns])])
