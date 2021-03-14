(ns knowtator.subs
  (:require [clojure.string :as str]
            [knowtator.html-colors :as html-colors]
            [knowtator.model :as model]
            [re-frame.core :as re-frame]))

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
  ::graph
  (fn [db]
    (:graph db)))

(re-frame/reg-sub
  ::profile-maps
  (fn [db]
    (-> db :profiles vals)))

(re-frame/reg-sub
  ::selected-profile
  (fn [db]
    (get-in db [:selection :profile])))

(re-frame/reg-sub
  ::selected-doc
  (fn [db]
    (get-in db [:selection :doc])))

(re-frame/reg-sub
  ::doc-maps
  (fn [db]
    (-> db :docs vals)))

(re-frame/reg-sub
  ::search-query
  (fn [db]
    (get-in db [:search :query])))

(re-frame/reg-sub
  ::doc-contents
  (fn [{:keys [docs]}]
    (zipmap (keys docs)
      (map :content (vals docs)))))

(re-frame/reg-sub
  ::selected-content
  :<- [::selected-doc]
  :<- [::doc-contents]
  (fn [[doc contents]]
    (get contents doc)))

(re-frame/reg-sub
  ::search-matches
  :<- [::search-query]
  :<- [::selected-content]
  (fn [[query content]]))

(re-frame/reg-sub
  ::anns
  :anns)

(re-frame/reg-sub
  ::ann-vals
  (comp vals :anns))

(re-frame/reg-sub
  ::spans
  :spans)

(re-frame/reg-sub
  ::profiles
  :profiles)

(re-frame/reg-sub
  ::profile-restriction?
  #(get-in % [:selection :profile-restriction]))

(re-frame/reg-sub
  ::visual-restriction
  :<- [::selected-doc]
  :<- [::selected-profile]
  :<- [::profile-restriction?]
  (fn [[doc-id profile-id profile-restricted?]]
    (cond-> {:doc doc-id}
      profile-restricted? (assoc :profile profile-id))))

(re-frame/reg-sub
  ::visible-spans
  :<- [::visual-restriction]
  :<- [::anns]
  :<- [::spans]
  (fn [[restriction anns spans] _]
    (model/filter-in-restriction restriction anns spans)))

(re-frame/reg-sub
  ::highlighted-text
  :<- [::selected-content]
  :<- [::visible-spans]
  (fn [[content spans] _]
    (->> spans
      vals
      (model/resolve-span-content content)
      model/split-into-paragraphs)))

(re-frame/reg-sub
  ::ann-color
  :<- [::anns]
  :<- [::profiles]
  (fn [[anns profiles] [_ ann]]
    (cond (not (coll? ann))   (model/ann-color (get anns ann) profiles)
          (empty? (rest ann)) (model/ann-color (get anns (first ann)) profiles)
          :else               "grey")))

(re-frame/reg-sub
  ::selected-span-id
  #(get-in % [:selection :span]))

(re-frame/reg-sub
  ::selected-span?
  :<- [::selected-span-id]
  (fn [sel-id [_ id]]
    (= sel-id id)))

(re-frame/reg-sub
  ::un-searched?
  #(get-in % [:search :un-searched?]))

(re-frame/reg-sub
  ::selected-ann
  #(get-in % [:selection :ann]))

(re-frame/reg-sub
  ::ann-info
  (fn [db [_ ann-id]]
    (get-in db [:anns ann-id])))

(re-frame/reg-sub
  ::selected-concept
  #(get-in % [:selection :concept]))

(re-frame/reg-sub
  ::selected-color
  :<- [::selected-profile]
  :<- [::selected-concept]
  :<- [::profiles]
  (fn [[profile-id concept-id profiles]]
    (let [color (get-in profiles [profile-id :colors concept-id])]
      (if (str/starts-with? color "#")
        color
        (get html-colors/html-colors color)))))
