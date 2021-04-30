(ns knowtator.analogy.views
  (:require [knowtator.analogy.events :as evts]
            [knowtator.analogy.subs :as subs]
            [knowtator.util :refer [<sub >evt]]
            [re-com.core :as re-com]
            [re-frame-datatable.core :as dt]))

(defn concept-graph-chooser
  []
  [re-com/single-dropdown
    :model     (<sub [::subs/selected-concept-graph-id])
    :choices   (<sub [::subs/concept-graphs-for-selected-mop-map])
    :label-fn  :id
    :on-change #(>evt [::evts/select-concept-graph %])])

(defn roles-table
  []
  [dt/datatable
    ::role-table [::subs/selected-mop-map-roles]
    [{::dt/column-key   [:id]
      ::dt/sorting      {::dt/enabled? true}
      ::dt/column-label "ID"}
     {::dt/column-key   [:count]
      ::dt/column-label "Count"}]
    {::dt/table-classes ["table" "ui" "celled"]
     ::dt/selection     {::dt/enabled? true}}])

