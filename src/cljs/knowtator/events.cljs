(ns knowtator.events
  (:require
   [re-frame.core :as re-frame]
   [re-pressed.core :as rp]
   [knowtator.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))

(re-frame/reg-event-fx
  ::navigate
  (fn-traced [_ [_ handler]]
   {:navigate handler}))

(re-frame/reg-event-fx
 ::set-active-panel
 (fn-traced [{:keys [db]} [_ active-panel]]
   {:db (assoc db :active-panel active-panel)
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

(re-frame/reg-event-db
  ::set-re-pressed-example
  (fn [db [_ value]]
    (assoc db :re-pressed-example value)))

(re-frame/reg-event-db
  ::add-node
  (fn [db _]
    (update-in db [:graph :nodes] conj {:id (count (lazy-cat
                                                     (get-in db [:graph :nodes])
                                                     (get-in db [:graph :edges])))})))
