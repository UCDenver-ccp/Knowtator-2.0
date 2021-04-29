(ns knowtator.analogy.views
  (:require [knowtator.analogy.events :as evts]
            [knowtator.analogy.subs :as subs]
            [knowtator.util :refer [<sub >evt]]
            [re-com.core :as re-com]))

(defn concept-graph-chooser
  []
  [re-com/single-dropdown
    :model     (<sub [::subs/selected-concept-graph-id])
    :choices   (<sub [::subs/concept-graphs-for-selected-mop-map])
    :label-fn  :id
    :on-change #(>evt [::evts/select-concept-graph %])])

