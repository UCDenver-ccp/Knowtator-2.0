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
    (get-in db [:selection :docs])
    :document1))

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
    #_(let [restriction [{:filter-type   :doc
                          :filter-values #{:document1}}]
            spans       [{:id :document1-26 :ann :mention_0 :start 0 :end 4}
                         {:id :document1-28 :ann :mention_1 :start 10 :end 14}
                         {:id :document1-29 :ann :mention_1 :start 15 :end 24}
                         {:id :span-15 :ann :mention_3 :start 0 :end 3}
                         {:id :document3-11 :ann :mention_0 :start 0 :end 1}
                         {:id :document3-14 :ann :mention_1 :start 28 :end 36}
                         {:id :document3-17 :ann :mention_2 :start 28 :end 36}]
            db          {:text-annotation {:anns   [{:id      :mention_0
                                                     :doc     :document1
                                                     :profile :Default
                                                     :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"}
                                                    {:id      :mention_1
                                                     :doc     :document1
                                                     :profile :profile1
                                                     :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#IceCream"}
                                                    {:id      :mention_3
                                                     :doc     :document2
                                                     :profile :Default
                                                     :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"}
                                                    {:id      :annotation-4
                                                     :doc     :document3
                                                     :profile :Default
                                                     :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}
                                                    {:id      :annotation-5
                                                     :doc     :document3
                                                     :profile :profile1
                                                     :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}
                                                    {:id      :annotation-6
                                                     :doc     :document3
                                                     :profile :Default
                                                     :concept "http://www.co-ode.org/ontologies/pizza.owl#Food"}
                                                    {:id      :annotation-7
                                                     :doc     :document3
                                                     :profile :profile1
                                                     :concept "http://www.co-ode.org/ontologies/pizza.owl#Food"}
                                                    {:id      :annotation-8
                                                     :doc     :document3
                                                     :profile :Default
                                                     :concept "http://www.co-ode.org/ontologies/pizza.owl#Food"}
                                                    {:id      :mention_2
                                                     :doc     :document3
                                                     :profile :Default
                                                     :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}]
                                           :docs   [{:file-name "document1.txt"
                                                     :id        :document1
                                                     :content   "This is a test document."}
                                                    {:file-name "document2.txt"
                                                     :id        :document2
                                                     :content   "And another one!"}
                                                    {:file-name "document3.txt"
                                                     :id        :document3
                                                     :content   "A second test document has appeared!"}
                                                    {:file-name "document4.txt"
                                                     :id        :document4
                                                     :content   "Look at me."}
                                                    {:file-name "long_article.txt"
                                                     :id        :long_article}]
                                           :spans  [{:id :document1-26 :ann :mention_0 :start 0 :end 4}
                                                    {:id :document1-28 :ann :mention_1 :start 10 :end 14}
                                                    {:id :document1-29 :ann :mention_1 :start 15 :end 24}
                                                    {:id :span-15 :ann :mention_3 :start 0 :end 3}
                                                    {:id :document3-11 :ann :mention_0 :start 0 :end 1}
                                                    {:id :document3-14 :ann :mention_1 :start 28 :end 36}
                                                    {:id :document3-17 :ann :mention_2 :start 28 :end 36}]
                                           :graphs [{:id    :graph_0
                                                     :doc   :document1
                                                     :nodes [{:id  :node_0
                                                              :ann :mention_0}
                                                             {:id  :node_1
                                                              :ann :mention_1}]
                                                     :edges [{:id :edge_0

                                                              :from :node_0
                                                              :to   :node_1}]}
                                                    {:id    :graph_2
                                                     :doc   :document2
                                                     :nodes [{:id  :node_0
                                                              :ann :mention_3}
                                                             {:id  :node_1
                                                              :ann :mention_3}]
                                                     :edges [{:id   :edge_0
                                                              :to   :node_1
                                                              :from :node_0}]}
                                                    {:id    (keyword "Old Knowtator Relations")
                                                     :doc   :document3
                                                     :nodes [{:id  :document3-19
                                                              :ann :mention_0}
                                                             {:id  :document3-20
                                                              :ann :mention_1}
                                                             {:id  :document3-22
                                                              :ann :mention_2}]
                                                     :edges [{:id   :document3-21
                                                              :to   :document3-20
                                                              :from :document3-19}
                                                             {:id   :document3-23
                                                              :from :document3-19
                                                              :to   :document3-22}]}]
                                           :profiles [{:id :Default
                                                       :colors
                                                       {"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza" "#ff0000"}}
                                                      {:id :profile1
                                                       :colors
                                                       {"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza" "#ff3333"
                                                        "http://www.co-ode.org/ontologies/pizza/pizza.owl#IceCream"
                                                        "#00ffff"}}]}}
            real-spans-db (model/realize-spans db)
            spans         (get-in real-spans-db [:text-annotation :spans])]
        (map (fn [{:keys [filter-type filter-values]}]
               (if (seq filter-values)
                 (filter-values (get (first spans) filter-type))
                 true))
          restriction)
        (filter #(model/in-restriction? % restriction) spans))
    (filter #(model/in-restriction? % restriction) spans)))



(reg-sub ::highlighted-text
  :<- [::selected-content]
  :<- [::visible-spans]
  (fn [[content spans] _]
    #_[content spans]
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

(reg-sub ::selected-color
  :<- [::selected-profile]
  :<- [::selected-concept]
  :<- [::profile-map]
  (fn [[profile-id concept-id profiles]]
    (let [color (get-in profiles [profile-id :colors concept-id])]
      (if (str/starts-with? color "#")
        color
        (get html-colors/html-colors color)))))

(reg-sub ::docs
  (comp :docs :text-annotation))

(reg-sub ::spans
  (comp :spans :text-annotation))
