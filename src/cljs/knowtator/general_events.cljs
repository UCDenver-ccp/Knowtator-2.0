(ns knowtator.general-events
  (:require
   [re-frame.core :refer [reg-event-fx reg-event-db]]))

(reg-event-fx ::load-success
  (fn [{:keys [db]} [_ place evt data]]
    {:db         db
     :dispatch-n [[evt data] [::set-spinny place false]]}))

(reg-event-fx ::load-failure
  (fn [{:keys [db]} [_ place _]]
    {:db         db
     :dispatch-n [[::report-failure]
                  [::set-error place true]
                  [::set-spinny place false]]}))

(reg-event-db ::set-spinny
  (fn [db [_ place val]] (assoc-in db [:loading? place] val)))

(reg-event-db ::set-error
  (fn [db [_ place val]] (assoc-in db [:error? place] val)))

(reg-event-db ::report-failure
  (fn [db [_ result]] (println "Error") (println result) db))
