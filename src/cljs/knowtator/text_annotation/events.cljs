(ns knowtator.text-annotation.events
  (:require
   [day8.re-frame.tracing    :refer-macros [fn-traced]]
   [day8.re-frame.undo       :as    undo
                             :refer [undoable]]
   [knowtator.model          :as model]
   [knowtator.owl.events     :as owl-evts]
   [knowtator.util           :as util]
   [com.rpl.specter          :as s]
   [re-frame.core            :refer [reg-event-db reg-event-fx trim-v]]
   [ajax.core                :as ajax]
   [knowtator.general-events :as ge]))

(defn filter-in-doc
  [db coll-id]
  (->> db
       model/realize-spans
       :text-annotation
       coll-id
       (filter #(model/in-restriction?
                 %
                 {:filter-tye    :doc
                  :filter-values #{(get-in db [:selection :docs])}}))))

(reg-event-fx ::cycle
  trim-v
  (fn-traced [{:keys [db]} [coll-id direction]]
    (let [coll        (->>
                       (cond (#{:spans :anns} coll-id) (filter-in-doc db
                                                                      coll-id)
                             :else                     (-> db
                                                           :text-annotation
                                                           coll-id))
                       (sort-by (juxt :start :end :id)))
          new-item-id (model/cycle-selection db coll coll-id direction)]
      (cond (#{:spans} coll-id) {:db       db
                                 :dispatch [::select-span new-item-id]}
            :else               {:db (assoc-in db
                                      [:selection coll-id]
                                      new-item-id)}))))

(reg-event-fx ::select-span-by-loc
  trim-v
  (fn-traced [{:keys [db]} [loc doc-id]]
    (let [span-id (->> db
                       :text-annotation
                       :spans
                       (model/spans-containing-loc loc)
                       (assoc-in db [:text-annotation :spans])
                       model/realize-spans
                       :text-annotation
                       :spans
                       (filter
                        #(model/in-restriction? %
                                                [{:filter-type   :doc
                                                  :filter-values #{doc-id}}]))
                       first
                       :id)]
      (cond-> {:db db} span-id (assoc :dispatch [::select-span span-id])))))

(reg-event-fx ::select-span
  trim-v
  (fn-traced [{:keys [db]} [id]]
    (let [ann (s/select-one (s/comp-paths (model/TXT-OBJ :spans :id id)
                                          (s/keypath :ann))
                            db)]
      {:db       (assoc-in db [:selection :spans] id)
       :dispatch [::select-annotation ann]})))

(reg-event-fx ::select-annotation
  trim-v
  (fn-traced [{:keys [db]} [id]]
    (let [concept (s/select-one (s/comp-paths (model/TXT-OBJ :anns :id id)
                                              (s/keypath :concept))
                                db)]
      {:db       (assoc-in db [:selection :anns] id)
       :dispatch [::owl-evts/select-owl-class concept]})))

(reg-event-db ::grow-selected-span-start
  [(undoable "Growing span start") trim-v]
  #(model/mod-span % :start dec))

(reg-event-db ::shrink-selected-span-start
  [(undoable "Shrinking span start") trim-v]
  #(model/mod-span % :start inc))

(reg-event-db ::shrink-selected-span-end
  [(undoable "Shrinking span end") trim-v]
  #(model/mod-span % :end dec))

(reg-event-db ::grow-selected-span-end
  [(undoable "Growing span end") trim-v]
  #(model/mod-span % :end inc))

(reg-event-fx ::select-doc
  trim-v
  (fn [{:keys [db]} [doc-id]]
    (let [doc (->> db
                   :text-annotation
                   :docs
                   (filter (comp #{doc-id} :id))
                   first)]
      (cond-> {:db (assoc-in db [:selection :docs] doc-id)}
        (nil? (:content doc)) (assoc :dispatch [::load-doc doc-id])))))

(reg-event-fx ::load-doc
  (fn [{:keys [db]} [_ doc-id]]
    (let [project "any"]
      (cond-> {:db db}
        (not= project "default") (assoc
                                  :dispatch-n [[::ge/set-spinny :doc true]
                                               [::ge/set-error :doc false]]
                                  :http-xhrio
                                  [{:method :get
                                    :uri (str "/project" "/doc/" doc-id)
                                    :format (ajax/transit-request-format)
                                    :response-format
                                    (ajax/transit-response-format)
                                    :on-success [::ge/load-success
                                                 :doc
                                                 ::set-first-doc]
                                    :on-failure [::ge/load-failure :doc]}])))))

(reg-event-db ::select-profile
  trim-v
  (fn [db [profile-id]] (assoc-in db [:selection :profiles] profile-id)))

(reg-event-db ::toggle-profile-restriction
  trim-v
  (fn [db] (update-in db [:selection :profile-restriction] not)))

(reg-event-db ::add-doc
  [(undoable "Adding document") trim-v]
  (fn-traced [{{:keys [docs]} :text-annotation
               :as            db}
              _]
    (let [doc-id (model/unique-id (util/map-with-key docs :id)
                                  "d"
                                  (count docs))]
      (-> db
          (assoc-in [:text-annotation :docs doc-id]
                    {:id      doc-id
                     :content (str "I'm called " doc-id)})
          (assoc-in [:selection :docs] doc-id)))))

(reg-event-db ::add-ann
  [(undoable "Adding annotation") trim-v]
  (fn-traced [{{:keys [spans anns]}                       :text-annotation
               {:keys [profiles concepts docs start end]} :selection
               :as                                        db}
              _]
    (let [span-id (model/unique-id (util/map-with-key :id spans)
                                   "s"
                                   (count spans))
          ann-id  (model/unique-id (util/map-with-key :id anns)
                                   "a"
                                   (count anns))]
      (-> db
          (update-in [:text-annotation :anns]
                     conj
                     {:id      ann-id
                      :profile profiles
                      :concept concepts
                      :doc     docs})
          (update-in [:text-annotation :spans]
                     conj
                     {:id    span-id
                      :ann   ann-id
                      :end   end
                      :start start})
          (assoc-in [:selection :anns] ann-id)
          (assoc-in [:selection :spans] span-id)))))

(reg-event-db ::remove-selected-doc
  [(undoable "Removing document") trim-v]
  (fn [db _]
    (-> db
        (model/remove-selected-item :docs)
        (assoc-in [:selection :docs]
                  (-> db
                      :text-annotation
                      :docs
                      first
                      :id)))))

(reg-event-db ::remove-selected-ann
  [(undoable "Removing annotation") trim-v]
  (fn-traced [db _] (model/remove-selected-item db :anns)))
