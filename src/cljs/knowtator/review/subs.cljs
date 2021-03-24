(ns knowtator.review.subs
  (:require
   [clojure.spec.alpha :as s]
   [knowtator.specs :as specs]
   [knowtator.util :as util]
   [re-frame.core :as rf :refer [reg-sub]]
   [knowtator.subs :as subs]
   [knowtator.model :as model]))

(reg-sub
  ::restricted-anns
  :<- [::subs/anns]
  (fn [anns [_ filt-table]]
    (let [filts @(rf/subscribe filt-table)]
      (->> anns
        (remove #((complement model/in-restriction?) % filts))))))

(reg-sub
  ::review-types
  (fn [_ _]
    [{:id    :anns
      :spec  ::specs/ann
      :sub   [::restricted-anns]
      :label "Annotations"}
     {:id    :spans
      :spec  ::specs/spans-with-spanned-text
      :sub   [::subs/spans-with-spanned-text]
      :label "Spans"}
     {:id    :docs
      :spec  ::specs/doc
      :sub   [::subs/docs]
      :label "Documents"}]))

(reg-sub
  ::selected-review-type
  (fn [db _]
    (get-in db [:selection :review-type])))

(reg-sub
  ::review-type-info
  :<- [::selected-review-type]
  :<- [::review-types]
  (fn [[review-type review-types]]
    (get (util/map-with-key :id review-types) review-type)))

(reg-sub
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

(reg-sub
  ::values-to-review
  :<- [::review-type-info]
  (fn [info [_ filter-table]]
    (-> info
      :sub
      (conj filter-table)
      rf/subscribe
      deref)))

(reg-sub
  ::review-filters
  (fn [db _]
    (vals (get-in db [:selection :review-filters]))))

(reg-sub
  ::flattened-review-filters
  :<- [::review-filters]
  (fn [filts _]
    (for [filt filts
          item (:filter-values filt)]
      (assoc filt :item item))))

(reg-sub
  ::review-filter-types
  (fn [_ _]
    [{:id    :doc
      :label "Document"}
     {:id    :ann
      :label "Annotation"}
     {:id    :span
      :label "Span"}
     {:id    :profile
      :label "Profile"}]))

(reg-sub
  ::review-filter-type
  :<- [::review-filter-types]
  (fn [types [_ id]]
    (->> types
      (util/map-with-key :id)
      id)))

(reg-sub
  ::selected-review-filter-type
  (fn [db _]
    (get-in db [:selection :review-filter-type])))
