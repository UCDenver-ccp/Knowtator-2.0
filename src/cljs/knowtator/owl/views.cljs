(ns knowtator.owl.views
  (:require [knowtator.owl.subs :as subs]
            [clojure.zip :as zip]
            [knowtator.util :as util :refer [<sub >evt]]
            [re-com.core :as re-com :refer [at]]
            [knowtator.hierarchy :as h]
            [knowtator.owl.events :as evts]))

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
                   [zipper-hierarchy h {:model          [::subs/selected-concept]
                                        :collapse-model [::subs/collapsed?]
                                        :collapse-fn    #(>evt [::evts/toggle-collapse-owl-class %])
                                        :select-node-fn #(>evt [::evts/select-owl-class %])}]))]))

(defn owl-controls []
  [re-com/h-box
   :src (at)
   :children [[re-com/label
               :label "Annotation properties"]
              [re-com/single-dropdown
               :src (at)
               :choices (<sub [::subs/ann-props])
               :label-fn  (comp :fragment :iri)
               :id-fn (comp (partial apply str) (juxt :namespace :fragment) :iri)
               :model (<sub [::subs/selected-ann-prop])
               :on-change #(>evt [::evts/select-ann-prop %])]]])
