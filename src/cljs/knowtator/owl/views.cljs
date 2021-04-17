(ns knowtator.owl.views
  (:require [knowtator.owl.subs :as subs]
            [clojure.zip :as zip]
            [knowtator.util :as util :refer [<sub >evt]]
            [re-com.core :as re-com :refer [at]]
            [knowtator.hierarchy :as h]
            [knowtator.owl.events :as evts]))

(defn zipper-hierarchy
  ([{:keys [node label-fn selection-model collapse-model collapse-fn
            select-node-fn]
     :as   args}]
   (let [id         (zip/node node)
         collapsed? (<sub (conj collapse-model id))
         selected?  (= id (<sub selection-model))]
     [:li
      [re-com/h-box
        :children [[re-com/md-icon-button
                     :md-icon-name (cond
                                     ((complement zip/branch?) node) ""
                                     collapsed? "zmdi-caret-right"
                                     :else "zmdi-caret-down")
                     :on-click     #(collapse-fn id)]
                   [re-com/hyperlink
                     :label    (label-fn id)
                     :on-click #(select-node-fn id)
                     :style    {:font-weight (if selected? :bold :normal)}]]]
      (when-not collapsed?
        [:ul {:style {:list-style-type :none
                      :padding-top     0
                      :padding-bottom  0
                      :padding-left    10}}
          (for [c (h/child-nodes node)]
            ^{:key (str (random-uuid))}
            [zipper-hierarchy (assoc args :node c)])])]))
  ([id {:as   args
        :keys [model]}]
   [re-com/scroller
     :height "500px"
     :size   "initial"
     :align  :start
     :src    (at)
     :child  [re-com/v-box
               :children [(for [root (<sub model)]
                            ^{:key (str (random-uuid))}
                            [:ul {:style {:list-style-type :none
                                          :padding-top     0
                                          :padding-bottom  0
                                          :padding-left    10}}
                              ^{:key (str (random-uuid))}
                              [zipper-hierarchy
                               (assoc args
                                 :node root)]])]]]))

(defn owl-class-hierarchy
  []
  [zipper-hierarchy
    :owl-class-hierarchy {:model           [::subs/class-hierarchy-zippers]
                          :label-fn        #(<sub [::subs/owl-class-label %])
                          :selection-model [::subs/selected-concept]
                          :collapse-model  [::subs/class-collapsed?]
                          :collapse-fn     #(>evt
                                              [::evts/toggle-collapse-owl-class
                                               %])
                          :select-node-fn  #(>evt [::evts/select-owl-class
                                                   %])}])

(defn owl-obj-prop-hierarchy
  []
  [zipper-hierarchy
    :owl-obj-prop-hierarchy {:model [::subs/obj-prop-hierarchy-zippers]
                             :label-fn #(<sub [::subs/owl-obj-prop-label %])
                             :selection-model [::subs/selected-obj-prop]
                             :collapse-model [::subs/obj-prop-collapsed?]
                             :collapse-fn
                               #(>evt [::evts/toggle-collapse-owl-obj-prop %])
                             :select-node-fn #(>evt [::evts/select-owl-obj-prop
                                                     %])}])

(defn owl-hierarchies
  []
  [re-com/v-box
    :children [[re-com/horizontal-tabs
                 :model     (<sub [::subs/selected-owl-hierarchy])
                 :tabs      (<sub [::subs/available-owl-hierarchies])
                 :on-change #(>evt [::evts/select-owl-hierarchy %])]
               (case (<sub [::subs/selected-owl-hierarchy])
                 :owl-class-hierarchy    [owl-class-hierarchy]
                 :owl-obj-prop-hierarchy [owl-obj-prop-hierarchy])]])

(defn owl-controls
  []
  [re-com/h-box
    :src      (at)
    :children [[re-com/label
                 :label "Annotation properties"]
               [re-com/single-dropdown
                 :src       (at)
                 :choices   (<sub [::subs/ann-props])
                 :label-fn  (comp :fragment :iri)
                 :id-fn     (comp (partial apply str)
                                  (juxt :namespace :fragment)
                                  :iri)
                 :model     (<sub [::subs/selected-ann-prop])
                 :on-change #(>evt [::evts/select-ann-prop %])]]])
