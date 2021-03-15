(ns knowtator.events
  (:require [ajax.core :as ajax]
            [clojure.string :as str]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [day8.re-frame.undo :as undo :refer [undoable]]
            [knowtator.db :as db]
            [knowtator.model :as model]
            [re-frame.core :as rf :refer [reg-event-db reg-event-fx]]
            [re-pressed.core :as rp]))

(reg-event-db
  ::initialize-db
  (fn-traced [_ _]
    db/default-db))

(reg-event-fx
  ::navigate
  (fn-traced [_ [_ handler]]
    {:navigate handler}))

(reg-event-fx
  ::set-active-panel
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
                 :clear-keys
                 [[{:keyCode 27} ;; escape
                   ]]}]}))

(reg-event-db
  ::set-re-pressed-example
  (fn [db [_ value]]
    (assoc db :re-pressed-example value)))

(reg-event-db
  ::add-node
  (fn [db _]
    (update-in db [:graph :nodes] conj {:id (count (lazy-cat
                                                     (get-in db [:graph :nodes])
                                                     (get-in db [:graph :edges])))})))


(reg-event-fx
  ::import-owl
  (fn [state _]
    (assoc state :http-xhiro {:method          :get
                              :uri             "/ontology"
                              :timeout         3000
                              :response-format (ajax/transit-response-format)
                              :on-success      [:add-ontology]
                              :on-failure      [:handle-failure]})))

(reg-event-db
  ::select-span
  (fn [db [_ loc doc-id]]
    (let [anns             (:anns db)
          {:keys [id ann]} (->> db
                             :spans
                             (model/filter-in-restriction {:doc doc-id} anns)
                             (model/spans-containing-loc loc)
                             vals
                             first)]
      (-> db
        (assoc-in [:selection :span] id)
        (assoc-in [:selection :ann] ann)))))

(reg-event-fx
  ::record-selection
  (fn [{:keys [db]} [_ {:keys [start end] :as text-range} doc-id]]
    (cond-> {:db (update db :selection merge text-range)}
      (= start end) (assoc :dispatch [::select-span start doc-id]))))

(reg-event-db
  ::select-profile
  (fn [db [_ profile-id]]
    (assoc-in db [:selection :profile] profile-id)))

(reg-event-db
  ::toggle-profile-restriction
  (fn [db]
    (update-in db [:selection :profile-restriction] not)))

(reg-event-db
  ::select-doc
  (fn [db [_ doc-id]]
    (assoc-in db [:selection :doc] doc-id)))


(reg-event-db
  ::add-doc
  (undoable "Adding document")
  (fn [db [_]]
    (let [doc-id (model/unique-id db :docs "d" (-> db :docs count))]
      (-> db
        (assoc-in [:docs doc-id]  {:id      doc-id
                                   :content (str "I'm called " doc-id)})
        (assoc-in [:selection :doc] doc-id)))))

(reg-event-db
  ::add-ann
  (undoable "Adding annotation")
  (fn [db [_]]
    (let [span-id                                 (model/unique-id db :spans "s" (-> db :spans count))
          ann-id                                  (model/unique-id db :anns "a" (-> db :anns count))
          {:keys [profile concept doc end start]} (:selection db)]
      (-> db
        (assoc-in [:anns ann-id]  {:id      ann-id
                                   :profile profile
                                   :concept concept
                                   :doc     doc})
        (assoc-in [:spans span-id] {:id    span-id
                                    :ann   ann-id
                                    :end   end
                                    :start start})
        (assoc-in [:selection :ann] ann-id)
        (assoc-in [:selection :span] span-id)))))

(reg-event-db
  ::remove-selected-doc
  (undoable "Removing document")
  (fn [db [_]]
    (-> db
      (update :docs dissoc (get-in db [:selection :doc]))
      (assoc-in [:selection :doc] (-> db
                                    :docs
                                    keys
                                    first)))))
(reg-event-db
  ::remove-selected-ann
  (undoable "Removing annotation")
  (fn [db [_]]
    (-> db
      (update :anns dissoc (get-in db [:selection :ann]))
      (update :spans dissoc (get-in db [:selection :span]))
      (assoc-in [:selection :ann] nil)
      (assoc-in [:selection :span] nil))))


(reg-event-db
  ::select-prev-doc
  #(model/cycle-selection % :doc :docs :prev))

(reg-event-db
  ::select-next-doc
  #(model/cycle-selection % :doc :docs :next))

(reg-event-db
  ::select-prev-span
  #(model/cycle-selection % :span :spans :prev))

(reg-event-db
  ::select-next-span
  #(model/cycle-selection % :span :spans :next))

(reg-event-db
  ::grow-selected-span-start
  (undoable "Growing span start")
  #(model/mod-span % :start dec))

(reg-event-db
  ::shrink-selected-span-start
  (undoable "Shrinking span start")
  #(model/mod-span % :start inc))

(reg-event-db
  ::shrink-selected-span-end
  (undoable "Shrinking span end")
  #(model/mod-span % :end dec))

(reg-event-db
  ::grow-selected-span-end
  (undoable "Growing span end")
  #(model/mod-span % :end inc))

(reg-event-db
  ::find-in-selected-doc
  (fn [db]
    (let [doc-id            (get-in db [:selection :doc])
          doc               (get-in db [:docs doc-id :content])
          text              (get-in db [:search :query])
          last-search-start (get-in db [:spans :last-search-span :start])
          result            (or
                              (str/index-of doc text (inc last-search-start))
                              (str/index-of doc text))]
      ;; TODO the last-search-span, when overlapped with selected span, causes division on overlapped span.
      (-> db
        (assoc-in [:spans :last-search-span] {:id       :last-search-span
                                              :searched result
                                              :ann      :last-search-ann
                                              :start    result
                                              :end      (when result (+ result (count text)))})
        (assoc-in [:anns :last-search-ann] {:id  :last-search-ann
                                            :doc doc-id})
        (assoc-in [:search :un-searched?] true)))))

(reg-event-fx
  ::update-search-text
  (fn [{:keys [db]} [_ text]]
    {:db       (assoc-in db [:search :query] text)
     :dispatch [::find-in-selected-doc text]}))

(reg-event-db
  ::done-searching
  (fn [db]
    (assoc-in db [:search :un-searched?] false)))


(reg-event-db
  ::set-concept-color
  (undoable "Setting color for concept")
  (fn [db [_ color]]
    (assoc-in db [:profiles
                  (get-in db [:selection :profile])
                  :colors
                  (get-in db [:selection :concept])]
      color)))

(reg-event-db
  ::select-review-type
  (fn [db [_ id]]
    (assoc-in db [:selection :review-type] id)))
