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
      ::dt/column-label "Count"}
     {::dt/column-key [:fillers]
      ::dt/column-label "Fillers"
      ::dt/render-fn
      (fn [fillers {:keys [id]}]
        (when ((set (map :id
                         (<sub [::dt/selected-items ::role-table
                                [::subs/selected-mop-map-roles]])))
               id)
          [dt/datatable (keyword *ns* (id (name ::filler-table) id))
           [::subs/fillers-for-role id]
           [{::dt/column-key   [:id]
             ::dt/render-fn    (fn [val] ((if (keyword? val) name str) val))
             ::dt/sorting      {::dt/enabled? true}
             ::dt/column-label "Filler"}]
           {::dt/table-classes ["table" "ui" "celled"]
            ::dt/selection     {::dt/enabled? true}}]))}]
    {::dt/table-classes ["table" "ui" "celled"]
     ::dt/selection     {::dt/enabled? true}}])

