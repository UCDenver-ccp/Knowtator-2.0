(ns knowtator.middleware.formats
  (:require #_[luminus-transit.time :as time]
            [muuntaja.core :as m]))

(def instance
  (m/create
    (-> m/default-options
        (update-in [:formats "application/transit+json" :decoder-opts]
                   identity
                   #_(partial merge time/time-deserialization-handlers))
        (update-in [:formats "application/transit+json" :encoder-opts]
                   identity
                   #_(partial merge time/time-serialization-handlers)))))
