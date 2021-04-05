(ns knowtator.owl.subs
  (:require [re-frame.core :refer [reg-sub]]
            [knowtator.hierarchy :as h]
            [knowtator.util :as util]))

(reg-sub ::obj-props
  (comp :obj-props :ontology))

(reg-sub ::ann-props
  (comp :ann-props :ontology))

(reg-sub ::classes
  (comp :classes :ontology))

(reg-sub ::selected-concept
  (fn [db _]
    (get-in db [:selection :concepts])))

(reg-sub ::selected-obj-prop
  (fn [db _]
    (get-in db [:selection :obj-props])))

(reg-sub ::class-hierarchy
  (fn [db _]
    (->> db
      :ontology
      :class-hierarchy)))

(reg-sub ::obj-prop-hierarchy
  (fn [db _]
    (->> db
      :ontology
      :obj-prop-hierarchy)))

(defn make-uri->label-map [owl-entities annotation-iri]
  (->> owl-entities
    (map (fn [{:keys                        [annotation]
              {:keys [namespace fragment]} :iri}]
           [(str namespace fragment)
            (or (->> annotation
                  (filter (fn [{:keys [iri]}] (and iri (= iri annotation-iri))))
                  first
                  :literal
                  :value)
              fragment)]))
    (into {})))

(reg-sub ::obj-prop-uri->label
  :<- [::obj-props]
  :<- [::selected-ann-prop]
  (fn [[obj-props ann-prop] _]
    (make-uri->label-map obj-props ann-prop)))

(reg-sub ::classes-uri->label
  :<- [::classes]
  :<- [::selected-ann-prop]
  (fn [[classes ann-prop] _]
    (make-uri->label-map classes ann-prop)))

(reg-sub ::class-hierarchy-zippers
  :<- [::class-hierarchy]
  (fn [h _]
    (h/hierarchy-zippers identity identity h)))

(reg-sub ::obj-prop-hierarchy-zippers
  :<- [::obj-prop-hierarchy]
  (fn [h _]
    (h/hierarchy-zippers identity identity h)))

(reg-sub ::class-map
  :<- [::classes]
  (fn [classes _]
    (->> classes
      (util/map-with-key (comp (partial apply str) (juxt :namespace :fragment) :iri)))))

(reg-sub ::obj-prop-map
  :<- [::obj-props]
  (fn [classes _]
    (->> classes
      (util/map-with-key (comp (partial apply str) (juxt :namespace :fragment) :iri)))))

(reg-sub ::class-collapsed?
  :<- [::class-map]
  (fn [class-map [_ iri]]
    (-> class-map
      (get-in [iri :collapsed?]))))

(reg-sub ::obj-prop-collapsed?
  :<- [::obj-prop-map]
  (fn [obj-prop-map [_ iri]]
    (-> obj-prop-map
      (get-in [iri :collapsed?]))))

(reg-sub ::owl-class-label
  :<- [::classes-uri->label]
  (fn [class-map [_ iri]]
    (get class-map iri)))

(reg-sub ::owl-obj-prop-label
  :<- [::obj-prop-uri->label]
  (fn [obj-prop-map [_ iri]]
    (get obj-prop-map iri)))

(reg-sub ::selected-ann-prop
  (fn [db _]
    (get-in db [:selection :ann-props])))
