(ns knowtator.relation-annotation.views
  (:require ["react-graph-vis" :as rgv]
            [knowtator.relation-annotation.events :as evts]
            [knowtator.relation-annotation.subs :as subs]
            [knowtator.util :as util :refer [<sub >evt]]
            [reagent.core :as r]))

(def visjs-graph (aget rgv "default"))

(defn handler-fn
  [f]
  (fn [e]
    (f (js->clj e :keywordize-keys true))))

(defn vis [id graph & {:keys [options events style]
                       :or   {style {:height "640px"}}}]
  [(r/adapt-react-class visjs-graph)
   {:graph   (<sub graph)
    :options options
    :events  (util/map-vals handler-fn events)
    :style   style}])

(defn graph []
  [vis :relation-annotation-graph [::subs/graph]
   :options {:layout       {:hierarchical false}
             :edges        {:color "#000000"}
             :physics      (<sub [::subs/graph-physics])
             :interaction  {:hover true}
             :manipulation {:enabled true}}
   :events  {:click        (fn [{:keys                   [nodes]
                                {{:keys [x y]} :canvas} :pointer}]
                             (>evt [::evts/toggle-node-physics (first nodes) x y]))
             :double-click (fn [{{{:keys [x y]} :canvas} :pointer}]
                             (>evt [::evts/add-node x y]))}])
