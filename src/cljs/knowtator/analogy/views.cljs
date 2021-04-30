(ns knowtator.analogy.views
  (:require [knowtator.analogy.events :as evts]
            [knowtator.analogy.subs :as subs]
            [knowtator.util :refer [<sub >evt]]
            [re-com.core :as re-com :refer [at]]
            [re-frame-datatable.core :as dt]
            [knowtator.relation-annotation.views :as ra]
            [reagent.core :as r]))

(defn fillers-table
  [fillers-table-id role]
  [dt/datatable (subs/table-name fillers-table-id role)
   [::subs/fillers-for-role role]
   [{::dt/column-key   [:id]
     ::dt/render-fn    (fn [val] ((if (keyword? val) name str) val))
     ::dt/sorting      {::dt/enabled? true}
     ::dt/column-label "Filler"}]
   {::dt/table-classes ["table" "ui" "celled"]
    ::dt/selection     {::dt/enabled? true}}])

(defn roles-table
  [roles-table-id fillers-table-id]
  (let [roles-sub [::subs/selected-mop-map-roles]]
    [dt/datatable roles-table-id roles-sub
     [{::dt/column-key   [:id]
       ::dt/sorting      {::dt/enabled? true}
       ::dt/column-label "ID"}
      {::dt/column-key   [:mops]
       ::dt/column-label "Mops Count"
       ::dt/render-fn    count}
      {::dt/column-key   [:fillers]
       ::dt/column-label "Fillers"
       ::dt/render-fn    (fn [fillers {role :id}]
                           (if (some (fn [{:keys [id]}] (= role id))
                                     (<sub [::dt/selected-items roles-table-id
                                            roles-sub]))
                             [re-com/scroller
                               :height "200px"
                               :child  [fillers-table fillers-table-id role]]
                             (count fillers)))}]
     {::dt/table-classes ["table" "ui" "celled"]
      ::dt/selection     {::dt/enabled? true}}]))

(defn analogy-graph
  [graph-id]
  (let [roles-table-id   (subs/table-name ::roles-table graph-id)
        fillers-table-id (subs/table-name ::fillers-table graph-id)]
    [re-com/h-split
      :panel-1 [ra/graph graph-id
                [::subs/selected-analogy-graph
                 (<sub [::subs/selected-slots roles-table-id fillers-table-id])]
                [::subs/selected-analogy-graph-id]]
      :panel-2 [re-com/scroller
                 :height "50vh"
                 :child  [roles-table roles-table-id fillers-table-id]]]))

(defn add-graph-panel-button
  []
  [re-com/button
    :label    "Add graph panel"
    :on-click #(>evt [::evts/add-graph-panel])])
