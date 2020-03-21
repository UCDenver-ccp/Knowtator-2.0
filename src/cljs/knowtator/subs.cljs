(ns knowtator.subs
  (:require
   [re-frame.core :as re-frame]
   [knowtator.span :as span]
   [knowtator.db :as db]))

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
  #(apply db/filter-in-doc %))

(defn resolve-span-content
  [content spans]
  (let [[container i] (reduce (fn [[container i] {:keys [start end] :as span}]
                                [(conj container
                                   (subs content i start)
                                   (assoc span :content (subs content start end)))
                                 end])
                        [[] 0] spans)]
    (conj container (subs content i))))
(re-frame/reg-sub
  ::highlighted-text
  :<- [::visible-doc-content]
  :<- [::visible-spans]
  (fn [[content spans] _]
    (->> spans
      vals
      span/make-overlapping-spans
      span/sort-spans-by-loc
      (resolve-span-content content))))

(re-frame/reg-sub
  ::selected-span-id
  (fn [db _]
    (get-in db [:selection :span])))

(re-frame/reg-sub
  ::selected-ann-id
  (fn [db _]
    (get-in db [:spans (get-in db [:selection :span]) :ann])))

(re-frame/reg-sub
  ::selected-ann-spans
  :<- [::selected-ann-id]
  :<- [::spans]
  (fn [[ann-id spans]]
    (db/filter-in-ann ann-id spans)))


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
      (resolve-span-content content)
      (remove nil?))))

(re-frame/reg-sub
  ::profiles
  :profiles)

(defn ann-color
  [{:keys [profile concept]} profiles]
  (get-in profiles [profile concept]))

(re-frame/reg-sub
  ::ann-color
  :<- [::anns]
  :<- [::profiles]
  (fn [[anns profiles] [_ ann]]
    (cond (not (coll? ann)) (ann-color (get anns ann) profiles)
          (empty? (rest ann)) (ann-color (get anns (first ann)) profiles)
          :else "grey")))
