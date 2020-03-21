(ns knowtator.model)

(defn ann-color
  [{:keys [profile concept]} profiles]
  (get-in profiles [profile concept]))
