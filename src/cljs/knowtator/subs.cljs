(ns knowtator.subs
  (:require [clojure.string :as str]
            [knowtator.html-colors :as html-colors]
            [knowtator.model :as model]
            [re-frame.core :as re-frame]
            [knowtator.specs :as specs]
            [clojure.spec.alpha :as s]
            [knowtator.util :as util]))

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
  ::ann-map
  :anns)

(re-frame/reg-sub
  ::anns
  (comp vals :anns))

(re-frame/reg-sub
  ::span-map
  :spans)

(re-frame/reg-sub
  ::profile-map
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
  :<- [::ann-map]
  :<- [::span-map]
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
  :<- [::ann-map]
  :<- [::profile-map]
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
  :<- [::profile-map]
  (fn [[profile-id concept-id profiles]]
    (let [color (get-in profiles [profile-id :colors concept-id])]
      (if (str/starts-with? color "#")
        color
        (get html-colors/html-colors color)))))

(re-frame/reg-sub
  ::selected-review-type
  (fn [db _]
    (get-in db [:selection :review-type])))

(re-frame/reg-sub
  ::review-type-info
  :<- [::selected-review-type]
  :<- [::review-types]
  (fn [[review-type review-types]]
    (get (util/map-with-key :id review-types) review-type)))

(re-frame/reg-sub
  ::selected-review-columns
  :<- [::review-type-info]
  (fn [info]
    (when (some? info)
      (->> info
        :spec
        s/get-spec
        s/describe
        last
        (map name)))))

(re-frame/reg-sub
  ::values-to-review
  :<- [::review-type-info]
  (fn [info]
    (-> info
      :sub
      re-frame/subscribe
      deref)))

(re-frame/reg-sub
  ::docs
  (comp vals :docs))

(re-frame/reg-sub
  ::spans
  (comp vals :spans))

(re-frame/reg-sub
  ::review-types
  (fn [_ _]
    [{:id    :anns
      :spec  ::specs/ann
      :sub   [::anns]
      :label "Annotations"}
     {:id    :spans
      :spec  ::specs/span
      :sub   [::spans]
      :label "Spans"}
     {:id    :docs
      :spec  ::specs/doc
      :sub   [::docs]
      :label "Documents"}]))
