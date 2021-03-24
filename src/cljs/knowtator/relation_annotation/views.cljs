(ns knowtator.relation-annotation.views
  (:require ["react-graph-vis" :as rgv]
            [knowtator.relation-annotation.events :as evts]
            [knowtator.relation-annotation.subs :as subs]
            [knowtator.util :as util :refer [<sub >evt]]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(def visjs-graph (aget rgv "default"))

(defn handler-fn
  [f]
  (fn [e]
    (f (js->clj e :keywordize-keys true))))

(defn vis [graph & {:keys [options events style]
                    :or {style {:height "640px"}}}]
  [(r/adapt-react-class visjs-graph)
   {:graph   graph
    :options options
    :events  (util/map-vals handler-fn events)
    :style   style}])

(defn graph []
  [vis (<sub [::subs/graph])
   :options {:layout {:hierarchical false}
             :edges  {:color "#000000"}}
   :events  {:select       (fn [{:keys [nodes edges]}]
                             (println "Nodes:" nodes)
                             (println "Edges:" edges))
             :double-click (fn [{{{:keys [x y]} :canvas} :pointer}]
                             (println x y)
                             (rf/dispatch [::evts/add-node]))}])
