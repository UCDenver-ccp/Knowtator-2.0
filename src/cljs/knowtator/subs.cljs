(ns knowtator.subs
  (:require [clojure.string :as str]
            [knowtator.html-colors :as html-colors]
            [knowtator.model :as model]
            [re-frame.core :as rf :refer [reg-sub]]))

(defn ->db-map [k]
  (comp (partial apply zipmap) (juxt (partial map :id) identity) k :text-annotation))

(reg-sub ::name
  (fn [db]
    (:name db)))

(reg-sub ::active-panel
  (fn [db _]
    (:active-panel db)))

(reg-sub ::re-pressed-example
  (fn [db _]
    (:re-pressed-example db)))

(reg-sub ::profiles
  (comp :profiles :text-annotation))

(reg-sub ::selected-profile
  (fn [db]
    (get-in db [:selection :profiles])))

(reg-sub ::selected-doc
  (fn [db]
    (get-in db [:selection :docs])))

(reg-sub ::doc-map
  (->db-map :docs))

(reg-sub ::search-query
  (fn [db]
    (get-in db [:search :query])))

(reg-sub ::doc-contents
  (fn [{{:keys [docs]} :text-annotation}]
    (zipmap (map :id docs)
      (map :content docs))))

(reg-sub ::selected-content
  :<- [::selected-doc]
  :<- [::doc-contents]
  (fn [[doc contents]]
    (get contents doc)))

(reg-sub ::search-matches
  :<- [::search-query]
  :<- [::selected-content]
  (fn [[query content]]))

(reg-sub ::ann-map
  (->db-map :anns))

(reg-sub ::anns
  (comp :anns :text-annotation))

(reg-sub ::profile-map
  (->db-map :profiles))

(reg-sub ::profile-restriction?
  #(get-in % [:selection :profile-restriction]))

(reg-sub ::spans-with-spanned-text
  (fn [db _]
    (get-in (model/realize-spans db) [:text-annotation :spans])))

(reg-sub ::visual-restriction
  :<- [::selected-doc]
  :<- [::selected-profile]
  :<- [::profile-restriction?]
  (fn [[doc-id profile-id profile-restricted?]]
    (cond-> [{:filter-type   :doc
              :filter-values #{doc-id}}]
      profile-restricted? (conj {:filter-type   :profile
                                 :filter-values #{profile-id}}))))

(reg-sub ::visible-spans
  :<- [::visual-restriction]
  :<- [::spans-with-spanned-text]
  (fn [[restriction spans] _]
    (filter #(model/in-restriction? % restriction) spans)))

(reg-sub ::highlighted-text
  :<- [::selected-content]
  :<- [::visible-spans]
  (fn [[content spans] _]
    (->> spans
      (model/resolve-span-content content)
      model/split-into-paragraphs)))

(reg-sub ::ann-color
  :<- [::ann-map]
  :<- [::profiles]
  (fn [[anns profiles] [_ ann]]
    (cond (not (coll? ann))   (model/ann-color (get anns ann) profiles)
          (empty? (rest ann)) (model/ann-color (get anns (first ann)) profiles)
          :else               "grey")))

(reg-sub ::selected-span-id
  #(get-in % [:selection :spans]))

(reg-sub ::selected-span?
  :<- [::selected-span-id]
  (fn [sel-id [_ id]]
    (= sel-id id)))

(reg-sub ::un-searched?
  #(get-in % [:search :un-searched?]))

(reg-sub ::selected-ann
  #(get-in % [:selection :anns]))

(reg-sub ::ann-info
  :<- [::ann-map]
  (fn [anns [_ ann-id]]
    (get-in anns [ann-id])))

(reg-sub ::selected-concept
  #(get-in % [:selection :concepts]))

(reg-sub ::default-color
  #(get-in % [:defaults :color]))

(reg-sub ::selected-color
  :<- [::selected-profile]
  :<- [::selected-concept]
  :<- [::profile-map]
  :<- [::default-color]
  (fn [[profile-id concept-id profiles default-color]]
    (let [color (get-in profiles [profile-id :colors concept-id])]
      (if (and color (str/starts-with? color "#"))
        color
        (or (get html-colors/html-colors color)
          default-color)))))

(reg-sub ::docs
  (comp :docs :text-annotation))

(reg-sub ::spans
  (comp :spans :text-annotation))
