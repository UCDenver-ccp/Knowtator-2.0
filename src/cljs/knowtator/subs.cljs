(ns knowtator.subs
  (:require [clojure.string :as str]
            [knowtator.html-colors :as html-colors]
            [knowtator.model :as model]
            [re-frame.core :as rf :refer [reg-sub]]))

(reg-sub
  ::name
  (fn [db]
    (:name db)))

(reg-sub
  ::active-panel
  (fn [db _]
    (:active-panel db)))

(reg-sub
  ::re-pressed-example
  (fn [db _]
    (:re-pressed-example db)))

(reg-sub
  ::graph
  (fn [db]
    (:graph db)))

(reg-sub
  ::profiles
  (comp vals :profiles))

(reg-sub
  ::selected-profile
  (fn [db]
    (get-in db [:selection :profiles])))

(reg-sub
  ::selected-doc
  (fn [db]
    (get-in db [:selection :docs])))

(reg-sub
  ::doc-map
  :docs)

(reg-sub
  ::search-query
  (fn [db]
    (get-in db [:search :query])))

(reg-sub
  ::doc-contents
  (fn [{:keys [docs]}]
    (zipmap (keys docs)
      (map :content (vals docs)))))

(reg-sub
  ::selected-content
  :<- [::selected-doc]
  :<- [::doc-contents]
  (fn [[doc contents]]
    (get contents doc)))

(reg-sub
  ::search-matches
  :<- [::search-query]
  :<- [::selected-content]
  (fn [[query content]]))

(reg-sub
  ::ann-map
  :anns)

(reg-sub
  ::anns
  (comp vals :anns))

(reg-sub
  ::span-map
  :spans)

(reg-sub
  ::profile-map
  :profiles)

(reg-sub
  ::profile-restriction?
  #(get-in % [:selection :profile-restriction]))

(reg-sub
  ::visual-restriction
  :<- [::selected-doc]
  :<- [::selected-profile]
  :<- [::profile-restriction?]
  (fn [[doc-id profile-id profile-restricted?]]
    (cond-> {:doc doc-id}
      profile-restricted? (assoc :profile profile-id))))

(reg-sub
  ::visible-spans
  :<- [::visual-restriction]
  :<- [::ann-map]
  :<- [::span-map]
  (fn [[restriction anns spans] _]
    (model/filter-in-restriction restriction anns spans)))

(reg-sub
  ::highlighted-text
  :<- [::selected-content]
  :<- [::visible-spans]
  (fn [[content spans] _]
    (->> spans
      vals
      (model/resolve-span-content content)
      model/split-into-paragraphs)))

(reg-sub
  ::ann-color
  :<- [::ann-map]
  :<- [::profile-map]
  (fn [[anns profiles] [_ ann]]
    (cond (not (coll? ann))   (model/ann-color (get anns ann) profiles)
          (empty? (rest ann)) (model/ann-color (get anns (first ann)) profiles)
          :else               "grey")))

(reg-sub
  ::selected-span-id
  #(get-in % [:selection :spans]))

(reg-sub
  ::selected-span?
  :<- [::selected-span-id]
  (fn [sel-id [_ id]]
    (= sel-id id)))

(reg-sub
  ::un-searched?
  #(get-in % [:search :un-searched?]))

(reg-sub
  ::selected-ann
  #(get-in % [:selection :anns]))

(reg-sub
  ::ann-info
  (fn [db [_ ann-id]]
    (get-in db [:anns ann-id])))

(reg-sub
  ::selected-concept
  #(get-in % [:selection :concepts]))

(reg-sub
  ::selected-color
  :<- [::selected-profile]
  :<- [::selected-concept]
  :<- [::profile-map]
  (fn [[profile-id concept-id profiles]]
    (let [color (get-in profiles [profile-id :colors concept-id])]
      (if (str/starts-with? color "#")
        color
        (get html-colors/html-colors color)))))


(reg-sub
  ::docs
  (comp vals :docs))

(reg-sub
  ::spans
  (comp vals :spans))

(reg-sub
  ::spans-with-spanned-text
  :<- [::doc-map]
  :<- [::ann-map]
  :<- [::spans]
  (fn [[doc-map ann-map spans]]
    (->> spans
      (map #(assoc % :doc (get-in ann-map [(:ann %) :doc])))
      (group-by :doc)
      (mapcat (fn [[doc-id spans]]
                (map (fn [{:keys [start end] :as span}]
                       (assoc span :content
                         (subs (get-in doc-map [doc-id :content]) start end)))
                  spans))))))
