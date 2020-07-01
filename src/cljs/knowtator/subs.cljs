(ns knowtator.subs
  (:require
   [re-frame.core :as re-frame]
   [knowtator.model :as model]))

(re-frame/reg-sub
  ::name
  (fn [db]
    (:name db)))

(re-frame/reg-sub
  ::active-panel
  (fn [db _]
    (:active-panel db)))

(re-frame/reg-sub
  ::re-pressed-example
  (fn [db _]
    (:re-pressed-example db)))

(re-frame/reg-sub
  ::visible-doc-id
  (fn [db]
    (get-in db [:selection :doc])))

(re-frame/reg-sub
  ::docs
  :docs)

(re-frame/reg-sub
  ::doc-ids
  :<- [::docs]
  #(->> %
     vals
     (map (fn [doc] (assoc doc :label (:id doc))))))

(re-frame/reg-sub
  ::visible-doc-content
  :<- [::visible-doc-id]
  :<- [::docs]
  (fn [[doc-id docs] _]
    (get-in docs [doc-id :content])))


(re-frame/reg-sub
  ::anns
  :anns)

(re-frame/reg-sub
  ::spans
  :spans)

(re-frame/reg-sub
  ::visible-spans
  :<- [::visible-doc-id]
  :<- [::anns]
  :<- [::spans]
  #(apply model/filter-in-doc %))

(re-frame/reg-sub
  ::highlighted-text
  :<- [::visible-doc-content]
  :<- [::visible-spans]
  (fn [[content spans] _]
    (->> spans
      vals
      model/make-overlapping-spans
      model/sort-spans-by-loc
      (model/resolve-span-content content)
      model/split-into-paragraphs)))

(defn selected-span
  [db]
  (get-in db [:selection :span]))

(re-frame/reg-sub
  ::selected-span-id
  selected-span)

(re-frame/reg-sub
  ::selected-span?
  :<- [::selected-span-id]
  (fn [sel-id [_ id]]
    (= sel-id id)))

(re-frame/reg-sub
  ::selected-ann-id
  (fn [db _]
    (get-in db [:spans (get-in db [:selection :span]) :ann])))

(re-frame/reg-sub
  ::selected-ann-spans
  :<- [::selected-ann-id]
  :<- [::spans]
  (fn [[ann-id spans]]
    (model/filter-in-ann ann-id spans)))

(re-frame/reg-sub
  ::selected-span-content
  :<- [::visible-doc-content]
  :<- [::selected-span-id]
  :<- [::selected-ann-spans]
  (fn [[content span-id spans] _]
    (->> (if span-id
           (assoc-in spans [span-id :selected] true)
           spans)
      vals
      (model/resolve-span-content content)
      (remove nil?))))

(re-frame/reg-sub
  ::profiles
  :profiles)

(re-frame/reg-sub
  ::ann-color
  :<- [::anns]
  :<- [::profiles]
  (fn [[anns profiles] [_ ann]]
    (cond (not (coll? ann))   (model/ann-color (get anns ann) profiles)
          (empty? (rest ann)) (model/ann-color (get anns (first ann)) profiles)
          :else               "grey")))

(re-frame/reg-sub
  ::selected-ann-map
  :<- [::selected-ann-id]
  :<- [::anns]
  (fn [[ann-id anns]]
    (get anns ann-id)))

(re-frame/reg-sub
  ::search-text
  (fn [db]
    (get-in db [:search :search-text])))

(re-frame/reg-sub
  ::un-searched?
  #(get-in % [:search :un-searched?]))
