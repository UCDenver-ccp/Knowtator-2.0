(ns knowtator.handler
  (:require [clojure.java.io :as io]
            [knowtator.env :refer [defaults]]
            [knowtator.knowtator-xml-parser :as kparser]
            [knowtator.layout :as layout :refer [error-page]]
            [knowtator.middleware :as middleware]
            [knowtator.owl-parser :as oparser]
            [mount.core :as mount]
            [muuntaja.middleware :as muun-m]
            [reitit.coercion.schema :as schema]
            [reitit.core :as r]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as rrc]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [schema.core :as s]))

(declare init-app)
(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(defn home-page [request]
  (layout/render request "index.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/"
    ["" {:get home-page}]
    ["annotation" {:get home-page}]
    ["graph" {:get home-page}]]])

(defn project-routes []
  ["/project"
   ["/project/:file-name" {:name       ::whole-project
                           :middleware [muun-m/wrap-format]
                           :get        {:coercion   schema/coercion
                                        :parameters {:path {:file-name s/Str}}
                                        :handler    (fn [{{{:keys [file-name]} :path} :parameters}]
                                                      (if (= file-name "test_project_using_uris")
                                                        (let [project-file (io/resource file-name)
                                                              project-xml  (kparser/read-project-xmls project-file)
                                                              articles     (kparser/read-articles project-file)]
                                                          {:status 200
                                                           :body   (kparser/parse-project articles project-xml)})
                                                        (let [project-file (io/resource "concepts+assertions 3_2 copy/")
                                                              project-xml  (kparser/read-project-xmls project-file)
                                                              articles     (kparser/read-articles project-file)]
                                                          {:status 200
                                                           :body   (as-> (kparser/parse-project articles project-xml) project
                                                                     (update project :docs (comp vec (partial take 1) (partial sort-by :id)))
                                                                     (update project :anns (comp vec #_(partial take 100) reverse (partial sort-by :id) (partial filter (comp (->> project :docs (map :id) set) :doc))))
                                                                     (update project :graphs (comp vec (partial take 100) reverse (partial sort-by :id) (partial filter (comp (->> project :docs (map :id) set) :doc))))
                                                                     (update project :spans (comp vec #_(partial take 100) reverse (partial sort-by (juxt :ann :start)) (partial filter (comp (->> project :anns (map :id) set) :ann))))
                                                                     (update project :profiles vec))})))}}]
   ["/doc/:id" {:name ::single-doc
                :get  {:coercion   schema/coercion
                       :parameters {:path {:id s/Int}}
                       :responses  {200 {:body {:content s/Str
                                                :id      s/Int}}}
                       :handler    (fn [{{{:keys [id]} :path} :parameters}]
                                     {:status 200
                                      :body   {:id      id
                                               :content "hello" #_ (slurp "/home/harrison/Downloads/concepts+assertions 3_2 copy/concepts+assertions 3_2 copy/Articles/11319941.txt")}})}}]
   ["/ontology/:file-name" {:name       ::ontology
                            :middleware [muun-m/wrap-format]
                            :get        {:coercion   schema/coercion
                                         :parameters {:path {:file-name s/Str}}
                                         :handler    (fn [{{{:keys [file-name]} :path} :parameters}]
                                                       (if (= file-name "test_project_using_uris")
                                                         (let [ontology-file (io/file (io/resource file-name) "Ontologies" "pizza.owl")
                                                               owl-ontology  (oparser/load-ontology ontology-file)
                                                               ontology      (oparser/parse-ontology owl-ontology)]
                                                           {:status 200
                                                            :body   ontology})
                                                         nil))}}]])

(defn app-router []
  (ring/router [(home-routes)
                (project-routes)]
    {:data {:middleware [rrc/coerce-exceptions-middleware
                         rrc/coerce-request-middleware
                         rrc/coerce-response-middleware]}}))

(defn app-route-handler []
  (ring/routes
    (ring/create-resource-handler {:path "/"})
    (wrap-content-type (wrap-webjars (constantly nil)))
    (ring/create-default-handler
      {:not-found          (constantly (error-page {:status 404, :title "404 - Page not found"}))
       :method-not-allowed (constantly (error-page {:status 405, :title "405 - Not allowed"}))
       :not-acceptable     (constantly (error-page {:status 406, :title "406 - Not acceptable"}))})))

(defn start-app-routes []
  (ring/ring-handler
    (app-router)
    (app-route-handler)))

(declare app-routes)
(mount/defstate app-routes
  :start
  (start-app-routes))

(defn app []
  (middleware/wrap-base #'app-routes))
