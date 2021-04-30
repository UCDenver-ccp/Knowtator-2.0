(ns knowtator.analogy.views
  (:require [knowtator.analogy.events :as evts]
            [knowtator.analogy.subs :as subs]
            [knowtator.util :refer [<sub >evt]]
            [re-com.core :as re-com]
            [re-frame-datatable.core :as dt]
            [knowtator.relation-annotation.views :as ra]))

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
    ::roles-table [::subs/selected-mop-map-roles]
    [{::dt/column-key   [:id]
      ::dt/sorting      {::dt/enabled? true}
      ::dt/column-label "ID"}
     {::dt/column-key   [:mops]
      ::dt/column-label "Mops Count"
      ::dt/render-fn    count}
     {::dt/column-key   [:fillers]
      ::dt/column-label "Filler Count"
      ::dt/render-fn    count}
     {::dt/column-key [:fillers]
      ::dt/column-label "Fillers"
      ::dt/render-fn
      (fn [_ {:keys [id]}]
        (when ((set (map :id
                         (<sub [::dt/selected-items ::roles-table
                                [::subs/selected-mop-map-roles]])))
               id)
          [re-com/scroller
            :height "200px"
            :child  [dt/datatable (subs/table-name ::fillers-table id)
                     [::subs/fillers-for-role id]
                     [{::dt/column-key   [:id]
                       ::dt/render-fn    (fn [val]
                                           ((if (keyword? val) name str) val))
                       ::dt/sorting      {::dt/enabled? true}
                       ::dt/column-label "Filler"}]
                     {::dt/table-classes ["table" "ui" "celled"]
                      ::dt/selection     {::dt/enabled? true}}]]))}]
    {::dt/table-classes ["table" "ui" "celled"]
     ::dt/selection     {::dt/enabled? true}}])

(defn analogy-graph
  []
  [ra/graph
   [::subs/selected-analogy-graph
    (<sub [::subs/selected-slots ::roles-table ::fillers-table])]
   [::subs/selected-analogy-graph-id]])
