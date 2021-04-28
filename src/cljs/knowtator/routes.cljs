(ns knowtator.routes
  (:require [bidi.bidi :as bidi]
            [knowtator.events :as events]
            [pushy.core :as pushy]
            [re-frame.core :as rf]))

(defmulti panels identity)
(defmethod panels :default [] [:div "No panel found for this route."])

(def routes
  (atom ["/"
         {""           :analogy
          "about"      :about
          "graph"      :graph
          "annotation" :annotation
          "review"     :review
          "analogy"    :analogy}]))

(defn parse [url] (bidi/match-route @routes url))

(defn url-for [& args] (apply bidi/path-for (into [@routes] args)))

(defn dispatch
  [route]
  (let [panel (keyword (str (name (:handler route)) "-panel"))]
    (rf/dispatch [::events/set-active-panel panel])))

(def history (pushy/pushy dispatch parse))

(defn navigate! [handler] (pushy/set-token! history (url-for handler)))

(defn start! [] (pushy/start! history))

(rf/reg-fx :navigate (fn [handler] (navigate! handler)))
