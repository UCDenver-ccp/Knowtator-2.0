(ns knowtator.subs
  (:require [clojure.string :as str]
            [knowtator.html-colors :as html-colors]
            [knowtator.model :as model]
            [re-frame.core :as rf :refer [reg-sub]]
            [knowtator.util :as util]
            [knowtator.owl.subs :as owl]))

(defn ->db-map
  [k]
  (comp (partial apply zipmap)
        (juxt (partial map :id) identity)
        k
        :text-annotation))

(reg-sub ::name (fn [db] (:name db)))

(reg-sub ::active-panel (fn [db _] (:active-panel db)))

(reg-sub ::re-pressed-example (fn [db _] (:re-pressed-example db)))

(reg-sub ::profiles (comp :profiles :text-annotation))

(reg-sub ::selected-profile (fn [db] (get-in db [:selection :profiles])))

(reg-sub ::selected-doc (fn [db] (get-in db [:selection :docs])))

(reg-sub ::doc-map (->db-map :docs))

(reg-sub ::search-query (fn [db] (get-in db [:search :query])))

(reg-sub ::doc-contents
  (fn [{{:keys [docs]} :text-annotation}]
    (zipmap (map :id docs) (map :content docs))))

(reg-sub ::selected-content
  :<- [::selected-doc]
  :<- [::doc-contents]
  (fn [[doc contents]] (get contents doc)))

(reg-sub ::search-matches
  :<- [::search-query]
  :<- [::selected-content]
  (fn [[query content]]))

(reg-sub ::ann-map (->db-map :anns))

(reg-sub ::anns (comp :anns :text-annotation))

(reg-sub ::profile-map (->db-map :profiles))

(reg-sub ::profile-restriction? #(get-in % [:selection :profile-restriction]))

(reg-sub ::spans-with-spanned-text
  (fn [db _] (get-in (model/realize-spans db) [:text-annotation :spans])))

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
    (->> spans
         (filter #(model/in-restriction? % restriction)))))

(reg-sub ::highlighted-text
  :<- [::selected-content]
  :<- [::visible-spans]
  (fn [[content spans] _]
    (->> spans
         (model/resolve-span-content content)
         model/split-into-paragraphs)))

(reg-sub ::selected-span-id #(get-in % [:selection :spans]))

(reg-sub ::selected-span?
  :<- [::selected-span-id]
  (fn [sel-id [_ id]] (= sel-id id)))

(reg-sub ::un-searched? #(get-in % [:search :un-searched?]))

(reg-sub ::selected-ann #(get-in % [:selection :anns]))

(reg-sub ::selected-ann-info
  :<- [::ann-map]
  :<- [::selected-ann]
  (fn [[anns ann-id] _]
    (when ann-id
      (->> anns
           ann-id
           (map (partial zipmap [:prop :val]))))))

(reg-sub ::default-color #(get-in % [:defaults :color]))

(reg-sub ::ann-color
  :<- [::ann-map]
  :<- [::profile-map]
  :<- [::default-color]
  (fn [[anns profiles default-color] [_ ann-id]]
    (let [colors (->> (cond-> ann-id (not (coll? ann-id)) vector)
                      (map anns)
                      (map (fn [{:keys [profile concept]}]
                             (get-in profiles
                                     [profile
                                       :colors concept]
                                     default-color))))]
      (if (apply = colors) (first colors) :grey))))

(reg-sub ::selected-color
  :<- [::selected-profile]
  :<- [::owl/selected-concept]
  :<- [::profile-map]
  :<- [::default-color]
  (fn [[profile-id concept-id profiles default-color]]
    (let [color (get-in profiles
                        [profile-id
                          :colors concept-id])]
      (if (and color (str/starts-with? color "#"))
        color
        (or (get html-colors/html-colors color) default-color)))))

(reg-sub ::docs
  (comp (partial sort-by (comp name :id) util/compare-alpha-num)
        :docs
        :text-annotation))

(reg-sub ::spans (comp :spans :text-annotation))

(reg-sub ::available-projects
  (fn [_ _] ["concepts+assertions 3_2 copy" "test_project_using_uris"
             "default"]))

(reg-sub ::selected-project (fn [db _] (get-in db [:selection :project])))

(reg-sub ::loading? (fn [db [_ place]] (get-in db [:loading? place] false)))

(reg-sub ::error? (fn [db [_ place]] (get-in db [:error? place] false)))
