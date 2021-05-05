(ns knowtator.analogy.views
  (:require [knowtator.analogy.events :as evts]
            [knowtator.analogy.subs :as subs]
            [knowtator.util :refer [<sub >evt]]
            [re-com.core :as re-com]
            [re-frame-datatable.core :as dt]
            [knowtator.relation-annotation.views :as ra]))

(defn fillers-table
  [fillers-table-id role graph-id]
  [dt/datatable (subs/table-name fillers-table-id role)
   [::subs/fillers-for-role role]
   [{::dt/column-key   [:id]
     ::dt/render-fn    (fn [val] [re-com/checkbox
                                   :model     (<sub [::subs/selected-filler? val
                                                     role graph-id])
                                   :on-change #(>evt [::evts/select-filler val
                                                      role graph-id])
                                   :label     ((if (keyword? val) name str)
                                               val)])
     ::dt/sorting      {::dt/enabled? true}
     ::dt/column-label "Filler"}] {::dt/table-classes ["table" "ui" "celled"]}])

(defn roles-table
  [roles-table-id fillers-table-id graph-id]
  (let [roles-sub [::subs/selected-mop-map-roles]]
    [dt/datatable roles-table-id roles-sub
     [{::dt/column-key   [:id]
       ::dt/sorting      {::dt/enabled? true}
       ::dt/column-label "ID"
       ::dt/render-fn    (fn [role] [re-com/checkbox
                                      :model     (<sub [::subs/selected-role?
                                                        role graph-id])
                                      :on-change #(>evt [::evts/select-role role
                                                         graph-id])
                                      :label     (name role)])}
      {::dt/column-key   [:mops]
       ::dt/column-label "Mops Count"
       ::dt/render-fn    count}
      {::dt/column-key   [:fillers]
       ::dt/column-label "Fillers"
       ::dt/render-fn    (fn [fillers {role :id}]
                           (if (<sub [::subs/selected-role? role graph-id])
                             [re-com/scroller
                               :height "200px"
                               :child  [fillers-table fillers-table-id role
                                        graph-id]]
                             (str (count fillers)
                                  (let [x (count (<sub [::subs/selected-fillers
                                                        role graph-id]))]
                                    (when-not (zero? x) (str "(" x ")"))))))}]
     {::dt/table-classes ["table" "ui" "celled"]}]))

(defn analogy-graph
  [graph-id]
  (let [roles-table-id   (subs/table-name ::roles-table graph-id)
        fillers-table-id (subs/table-name ::fillers-table graph-id)]
    [re-com/h-box
      :children
      [[re-com/v-box
         :children [[re-com/checkbox
                      :label     "Base"
                      :model     (= (<sub [::subs/selected-base]) graph-id)
                      :on-change #(>evt [::evts/select-base graph-id])]
                    [re-com/checkbox
                      :label     "Target"
                      :model     (= (<sub [::subs/selected-target]) graph-id)
                      :on-change #(>evt [::evts/select-target graph-id])]]]
       [re-com/h-split
         :panel-1 [ra/graph graph-id [::subs/selected-analogy-graph graph-id]
                   [::subs/selected-analogy-graph-id]
                   {:events {:click identity}}]
         :panel-2 [re-com/scroller
                    :height "50vh"
                    :child  [roles-table roles-table-id fillers-table-id
                             graph-id]]]]]))

(defn add-graph-panel-button
  []
  [re-com/button
    :label    "Add graph panel"
    :on-click #(>evt [::evts/add-graph-panel])])
