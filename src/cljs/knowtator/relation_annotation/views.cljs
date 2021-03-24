(ns knowtator.relation-annotation.views
  (:require ["react-graph-vis" :as rgv]
            [knowtator.relation-annotation.events :as evts]
            [knowtator.relation-annotation.subs :as subs]
            [knowtator.util :refer [<sub >evt]]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn vis [graph & {:keys [options events style]
                    :or {style {:height "640px"}}}]
  [(r/adapt-react-class (aget rgv "default"))
   {:graph   graph
    :options options
    :events  events
    :style   style}])

(defn graph []
  [vis (<sub [::subs/graph])
   :options {:layout {:hierarchical false}
             :edges  {:color "#000000"}}
   :events  {:select       (fn [e]
                             (let [e                     (js->clj e :keywordize-keys true)
                                   {:keys [nodes edges]} e]
                               (println "Nodes:" nodes)
                               (println "Edges:" edges)))
             :double-click (fn [e]
                             (let [{:keys [x y]} (-> e
                                                   (js->clj :keywordize-keys true)
                                                   (get-in [:pointer :canvas]))]
                               (println x y)
                               (rf/dispatch [::evts/add-node])))}])
