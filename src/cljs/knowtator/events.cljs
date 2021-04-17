(ns knowtator.events
  (:require [ajax.core :as ajax]
            [clojure.string :as str]
            [day8.re-frame.http-fx]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [day8.re-frame.undo :as undo :refer [undoable]]
            [knowtator.db :as db]
            [knowtator.owl.events :as owl]
            [knowtator.text-annotation.events :as txt]
            [re-frame.core :as rf :refer [reg-event-db reg-event-fx]]
            [re-pressed.core :as rp]))

(reg-event-fx ::initialize-db
  (fn-traced [_ _]
    (let [db db/default-db]
      {:db (-> db
               (assoc-in [:graph :nodes]
                         (->> db
                              :anns
                              (map-indexed (fn [i ann]
                                             {:id  (keyword (str "n" (inc i)))
                                              :ann (:id ann)}))))
               (update
                :graph
                (fn [{:keys [nodes]
                      :as   graph}]
                  (assoc
                   graph
                   :edges
                   (->> (for [source (take 2 (shuffle nodes))
                              target (take 3 (shuffle nodes))]
                          {:from (:id source)
                           :to   (:id target)})
                        (map-indexed
                         (fn [i edge]
                           (assoc edge :id (keyword (str "e" (inc i))))))))))
               (assoc-in [:selection :doc]
                         (-> db
                             :text-annotation
                             :docs
                             first
                             :id)))})))

(reg-event-fx ::load-project
  (fn [{:keys [db]} [_ project]]
    (cond-> {:db db}
      (not= project "default") (assoc
                                :dispatch-n [[::set-spinny :project true]
                                             [::set-error :project false]
                                             [::set-spinny :ontology true]
                                             [::set-error :ontology false]]
                                :http-xhrio
                                [{:method :get
                                  :uri (str "/project/project/" project)
                                  :format (ajax/transit-request-format)
                                  :response-format
                                  (ajax/transit-response-format)
                                  :on-success [::load-project-success :project
                                               ::set-project]
                                  :on-failure [::load-project-failure :project]}
                                 {:method :get
                                  :uri (str "/project/ontology/" project)
                                  :format (ajax/transit-request-format)
                                  :response-format
                                  (ajax/transit-response-format)
                                  :on-success [::load-project-success :ontology
                                               ::owl/set-ontology]
                                  :on-failure [::load-project-failure
                                               :ontology]}]))))

(reg-event-fx ::load-project-success
  (fn [{:keys [db]} [_ place evt project-data]]
    {:db         db
     :dispatch-n [[evt project-data] [::set-spinny place false]]}))

(reg-event-fx ::load-project-failure
  (fn [{:keys [db]} [_ place _]]
    {:db         db
     :dispatch-n [[::report-failure] [::set-error place true]
                  [::set-spinny place false]]}))

(reg-event-db ::set-spinny
  (fn [db [_ place val]] (assoc-in db [:loading? place] val)))

(reg-event-db ::set-error
  (fn [db [_ place val]] (assoc-in db [:error? place] val)))

(reg-event-db ::select-project
  (fn [db [_ project]] (assoc-in db [:selection :project] project)))

(reg-event-db ::set-project
  (fn [db [_ result]]
    (-> db
        (assoc :loading-project? false)
        (assoc :text-annotation result)
        (assoc-in [:selection :docs]
                  (->> result
                       :docs
                       (sort-by :id)
                       first
                       :id))
        (assoc-in [:selection :profiles]
                  (->> result
                       :profiles
                       (sort-by :id)
                       first
                       :id))
        (assoc-in [:selection :concepts]
                  (->> result
                       :anns
                       (sort-by :id)
                       first
                       :concept))
        (assoc-in [:selection :graphs]
                  (->> result
                       :graphs
                       (sort-by :id)
                       first
                       :id)))))

(reg-event-db ::report-failure
  (fn [db [_ result]] (println "Error") (println result) db))

(reg-event-fx ::navigate (fn-traced [_ [_ handler]] {:navigate handler}))

(reg-event-fx ::set-active-panel
  (fn-traced [{:keys [db]} [_ active-panel]]
    {:db       (assoc db :active-panel active-panel)
     :dispatch [::rp/set-keydown-rules
                 {:event-keys [[[::set-re-pressed-example "Hello, world!"]
                                [{:keyCode 72} ;; h
                                 {:keyCode 69} ;; e
                                 {:keyCode 76} ;; l
                                 {:keyCode 76} ;; l
                                 {:keyCode 79} ;; o
                                ]]]
                  :clear-keys [[{:keyCode 27} ;; escape
                               ]]}]}))

(reg-event-db ::set-re-pressed-example
  (fn [db [_ value]] (assoc db :re-pressed-example value)))

(reg-event-fx ::import-owl
  (fn [state _]
    (assoc state
           :http-xhiro
           {:method          :get
            :uri             "/ontology"
            :timeout         3000
            :response-format (ajax/transit-response-format)
            :on-success      [:add-ontology]
            :on-failure      [:handle-failure]})))

(reg-event-fx ::record-selection
  (fn-traced [{:keys [db]}
              [_ {:keys [start end]
                  :as   text-range}
                doc-id]]
    (cond-> {:db (update db :selection merge text-range)}
      (= start end) (assoc :dispatch [::txt/select-span-by-loc start doc-id]))))

(reg-event-db ::find-in-selected-doc
  (fn [db]
    (let [doc-id            (get-in db [:selection :docs])
          doc               (get-in db [:docs doc-id :content])
          text              (get-in db [:search :query])
          last-search-start (get-in db [:spans :last-search-span :start])
          result            (or (str/index-of doc text (inc last-search-start))
                                (str/index-of doc text))]
      ;; TODO the last-search-span, when overlapped with selected span, causes
      ;; division on overlapped span.
      (-> db
          (assoc-in [:spans :last-search-span]
                    {:id       :last-search-span
                     :searched result
                     :ann      :last-search-ann
                     :start    result
                     :end      (when result (+ result (count text)))})
          (assoc-in [:anns :last-search-ann]
                    {:id  :last-search-ann
                     :doc doc-id})
          (assoc-in [:search :un-searched?] true)))))

(reg-event-fx ::update-search-text
  (fn [{:keys [db]} [_ text]]
    {:db       (assoc-in db [:search :query] text)
     :dispatch [::find-in-selected-doc text]}))

(reg-event-db ::done-searching
  (fn [db] (assoc-in db [:search :un-searched?] false)))

(reg-event-db ::set-concept-color
  (undoable "Setting color for concept")
  (fn [db [_ color]]
    (assoc-in db
     [:text-annotation :profiles (get-in db [:selection :profiles]) :colors
      (get-in db [:selection :concepts])]
     color)))
