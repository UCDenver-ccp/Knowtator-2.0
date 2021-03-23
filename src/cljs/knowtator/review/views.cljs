(ns knowtator.review.views
  (:require [knowtator.util :refer [<sub >evt]]
            [re-frame-datatable.core :as dt]
            [knowtator.review.subs :as subs]
            [knowtator.review.events :as evts]
            [re-com.core :as re-com]
            [knowtator.util :as util]))

(defn filterer []
  [re-com/v-box
   :children
   (cons
     [re-com/h-box
      :children [[re-com/single-dropdown
                  :model (<sub [::subs/selected-review-filter-type])
                  :choices (<sub [::subs/review-filter-types])
                  :on-change #(>evt [::evts/select-review-filter-type %])]
                 [re-com/input-text
                  :model ""
                  :on-change #(>evt [::evts/add-review-filter %])]]]
     (for [[filt-type filt-items] (<sub [::subs/review-filter])
           filt-item-i            (range (count filt-items))]
       [re-com/h-box
        :children [[re-com/label
                    :label (get-in (util/map-with-key :id (<sub [::subs/review-filter-types]))
                             [filt-type :label])]
                   [re-com/input-text
                    :model (<sub [::subs/review-filter-item-string filt-type filt-item-i])
                    :on-change #(>evt [::evts/update-review-filter-item % filt-type filt-item-i])]]]))])

(defn chooser []
  [re-com/single-dropdown
   :choices (<sub [::subs/review-types])
   :model (<sub [::subs/selected-review-type])
   :on-change #(>evt [::evts/select-review-type %])])

(defn table []
  [(fn [cols] ; Anonymous function used to force re-rendering of table columns
     (when cols
       [dt/datatable :text-annotation-table [::subs/values-to-review]
        (for [col cols]
          {::dt/column-key   [(keyword col)]
           ::dt/sorting      {::dt/enabled? true}
           ::dt/column-label col})
        {::dt/table-classes    ["ui" "table"]
         ::dt/selection        {::dt/enabled? true}
         ::dt/footer-component (fn []
                                 [:tr
                                  [:th {:col-span 6}
                                   [:strong
                                    "Total selected: "
                                    (count (<sub [::dt/selected-items :text-annotation-table [::subs/values-to-review]]))]]])}]))
   (<sub [::subs/selected-review-columns])])
