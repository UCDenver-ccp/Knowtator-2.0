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
  :<- [::review-filter]
  (fn [[anns filt] _]
    (println filt)
    (->> anns
      (remove #(when filt
                 ((complement model/in-restriction?) % filt))))))

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
  (fn [info]
    (-> info
      :sub
      rf/subscribe
      deref)))

(reg-sub
  ::review-filter
  (fn [db _]
    (get-in db [:selection :review-filter])))

(reg-sub
  ::review-filter-string
  :<- [::review-filter]
  :<- [::selected-review-filter-type]
  (fn [[filt filt-type] _]
    (when-let [filt (get-in filt [filt-type])]
      (when (keyword? filt)
        (-> filt
          name
          str)))))

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
  ::selected-review-filter-type
  (fn [db _]
    (get-in db [:selection :review-filter-type])))
