(ns knowtator.analogy.events
  (:require [com.rpl.specter :as sp]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame.core :refer [reg-event-db trim-v]]))

(reg-event-db ::select-graph-space
  trim-v
  (fn [db [id]] (assoc-in db [:selection :analogy-graphs] id)))

(reg-event-db ::select-concept-graph
  trim-v
  (fn [db [id]] (assoc-in db [:selection :concept-graphs] id)))

(reg-event-db ::add-graph-panel
  trim-v
  (fn [db _]
    (update-in db
               [:graph-panels]
               (fn [graph-panels]
                 (conj graph-panels
                       {:id (keyword (str "gp-" (count graph-panels)))})))))

(defn toggle-selection
  [id items]
  (let [items (or items #{})] (if (items id) (disj items id) (conj items id))))

(reg-event-db ::select-graph
  trim-v
  (fn [db [id]]
    (update-in db [:selection :graphs] (partial toggle-selection id))))

(reg-event-db ::select-role
  trim-v
  (fn-traced [db [role graph-id]]
    (sp/transform [:graph-panels (sp/filterer #(= (:id %) graph-id)) sp/ALL
                   :roles]
                  (partial toggle-selection role)
                  db)))

(reg-event-db ::select-filler
  trim-v
  (fn-traced [db [filler role graph-id]]
    (sp/transform [:graph-panels (sp/filterer #(= (:id %) graph-id)) sp/ALL
                   :fillers role]
                  (partial toggle-selection filler)
                  db)))

(reg-event-db ::select-target
  trim-v
  (fn-traced [db [graph-id]] (assoc-in db [:selection :sme :target] graph-id)))

(reg-event-db ::select-base
  trim-v
  (fn-traced [db [graph-id]] (assoc-in db [:selection :sme :base] graph-id)))
