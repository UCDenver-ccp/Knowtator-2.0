(ns knowtator.relation-annotation.views
  (:require ["react-graph-vis" :as rgv]
            [knowtator.relation-annotation.events :as evts]
            [knowtator.relation-annotation.subs :as subs]
            [knowtator.subs :as ksubs]
            [knowtator.util :as util :refer [<sub >evt]]
            [reagent.core :as r]
            [re-com.core :as re-com]
            [knowtator.hierarchy :as h]
            [clojure.zip :as zip]))

(def visjs-graph (aget rgv "default"))

(defn handler-fn
  [f]
  (fn [e]
    (f (js->clj e :keywordize-keys true))))

(defn manipulation-handler-fn
  [f]
  (fn [data callback]
    (callback (f (js->clj data :keywordize-keys true)))))

(defn vis [id graph & {:keys [options events style]
                       :or   {style {:height "640px"}}}]
  (when-let [graph (<sub graph)]
    [(r/adapt-react-class visjs-graph)
     {:graph   graph
      :options (-> options
                 (update :manipulation (partial util/map-vals manipulation-handler-fn))
                 (assoc-in [:manipulation :enabled] true))
      :events  (util/map-vals handler-fn events)
      :style   style}]))

(defn graph []
  [vis :relation-annotation-graph [::subs/selected-realized-graph]
   :options {:layout       {:hierarchical false}
             :edges        {:color "#000000"}
             :physics      {:enabled    (<sub [::subs/graph-physics])
                            :barnes-hut {:spring-length (<sub [::subs/edge-length])}}
             :interaction  {:hover true}
             :manipulation {:add-node    (fn [node-data]
                                           (println node-data)
                                           (>evt [::evts/add-node (<sub [::subs/selected-graph-space-id]) node-data]))
                            :add-edge    (fn [edge-data]
                                           (println edge-data)
                                           (>evt [::evts/add-edge (<sub [::subs/selected-graph-space-id]) edge-data]))
                            :edit-node   (fn [node-data]
                                           (println node-data))
                            :edit-edge   (fn [edge-data]
                                           (println edge-data))
                            :delete-node (fn [node-ids]
                                           (println node-ids))
                            :delete-edge (fn [edge-ids]
                                           (println edge-ids))}}
   :events  {:click       (fn [{:keys                   [nodes]
                               {{:keys [x y]} :canvas} :pointer}]
                            (>evt [::evts/toggle-node-physics (<sub [::subs/selected-graph-space-id]) (first nodes) x y]))
             :select-node (fn [{:keys [nodes] :as data}]
                            (println nodes data)
                            (>evt [::evts/select-ann-node (<sub [::subs/selected-graph-space-id]) (first nodes)]))}])

(defn zipper-hierarchy
  ([{:keys [node model
            collapse-model collapse-fn
            select-node-fn]
     :as   args}]
   (let [id         (zip/node node)
         collapsed? (<sub (conj collapse-model id))
         selected?  (= id (<sub model))]
     [:li
      [re-com/h-box
       :children [[re-com/md-icon-button
                   :md-icon-name (cond ((complement zip/branch?) node) ""
                                       collapsed?                      "zmdi-caret-right"
                                       :else                           "zmdi-caret-down")
                   :on-click #(collapse-fn id)]
                  [re-com/hyperlink
                   :label (<sub [::subs/owl-class-label id])
                   :on-click #(select-node-fn id)
                   :style {:font-weight (if selected?
                                          :bold
                                          :normal)}]]]
      (when-not collapsed?
        [:ul {:style {:list-style-type :none
                      :padding-top     0
                      :padding-bottom  0
                      :padding-left    10}}
         (for [c (h/child-nodes node)]
           ^{:key (str (random-uuid))}
           [zipper-hierarchy (assoc args :node c)])])]))
  ([root args]
   [:ul {:style {:list-style-type :none
                 :padding-top     0
                 :padding-bottom  0
                 :padding-left    10}}
    ^{:key (str (random-uuid))} [zipper-hierarchy (assoc args :node root)]]))

(defn owl-hierarchy []
  (re-com/v-box
    :children [(let [hs (<sub [::subs/class-hierarchy-zippers])]
                 (for [h hs]
                   ^{:key (str (random-uuid))}
                   [zipper-hierarchy h {:model          [::ksubs/selected-concept]
                                        :collapse-model [::subs/collapsed?]
                                        :collapse-fn    #(>evt [::evts/toggle-collapse-owl-class %])
                                        :select-node-fn #(>evt [::evts/select-owl-class %])}]))]))
