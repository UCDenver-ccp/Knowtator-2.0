(ns knowtator.handler
  (:require
   [clojure.java.io         :as io]
   [clojure.string          :as str]
   [knowtator.env           :refer [defaults]]
   [knowtator.knowtator-xml-parser :as kparser]
   [knowtator.layout        :as    layout
                            :refer [error-page]]
   [knowtator.middleware    :as middleware]
   [knowtator.owl-parser    :as oparser]
   [mount.core              :as mount]
   [muuntaja.middleware     :as muun-m]
   [reitit.coercion.schema  :as schema]
   [reitit.ring             :as ring]
   [reitit.ring.coercion    :as rrc]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.webjars :refer [wrap-webjars]]
   [schema.core             :as s]))

(declare init-app)
(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(defn home-page [request] (layout/render request "index.html"))

(defn home-routes
  []
  [""
   {:middleware [middleware/wrap-csrf middleware/wrap-formats]}
   ["/"
    ["" {:get home-page}]
    ["annotation" {:get home-page}]
    ["graph" {:get home-page}]
    ["about" {:get home-page}]
    ["review" {:get home-page}]
    ["analogy" {:get home-page}]]])

(def projects (atom {}))
(def last-loaded-project (atom nil))

(defn project-handler
  [{{{:keys [file-name]} :path} :parameters}]
  (println "reading")
  (let [project (time
                 (let [project-file (io/resource file-name)
                       project-xml  (kparser/read-project-xmls project-file)
                       articles     (kparser/read-articles project-file)
                       project      (kparser/parse-project articles
                                                           project-xml)]
                   project))]
    (println "Read in project")
    (swap! projects assoc file-name project)
    (reset! last-loaded-project file-name)
    {:status 200
     :body   (-> project
                 (select-keys [:docs :profiles])
                 (update :docs
                   (fn [docs] (map (fn [doc] (dissoc doc :content)) docs))))}))

(defn ontology-handler
  [{{{:keys [file-name]} :path} :parameters}]
  (let [ontology-file (->> "Ontologies"
                           (io/file (io/resource file-name))
                           file-seq
                           (filter #(str/ends-with? (str %) ".owl"))
                           first)
        owl-ontology  (oparser/load-ontology ontology-file)
        ontology      (oparser/parse-ontology owl-ontology)]
    {:status 200
     :body   ontology}))

(defn project-routes
  []
  ["/project"
   ["/project/:file-name"
    {:name       ::whole-project
     :middleware [muun-m/wrap-format]
     :get        {:coercion   schema/coercion
                  :parameters {:path {:file-name s/Str}}
                  :handler    project-handler}}]
   ["/doc/:id"
    {:name ::single-doc
     :get  {:coercion   schema/coercion
            :parameters {:path {:id        s/Str
                                :file-name s/Str}}
            :responses  {200 {:body {:content s/Str
                                     :id      s/Int}}}
            :handler    (fn [{{{:keys [id]} :path} :parameters}]
                          (println "Doc" id)
                          {:status 200
                           :body   {:id      id
                                    :content "hello"}})}}]
   ["/ontology/:file-name"
    {:name       ::ontology
     :middleware [muun-m/wrap-format]
     :get        {:coercion   schema/coercion
                  :parameters {:path {:file-name s/Str}}
                  :handler    ontology-handler}}]])

(defn app-router
  []
  (ring/router [(home-routes) (project-routes)]
               {:data {:middleware [rrc/coerce-exceptions-middleware
                                    rrc/coerce-request-middleware
                                    rrc/coerce-response-middleware]}}))

(defn app-route-handler
  []
  (ring/routes
   (ring/create-resource-handler {:path "/"})
   (wrap-content-type (wrap-webjars (constantly nil)))
   (ring/create-default-handler
    {:not-found          (constantly (error-page {:status 404
                                                  :title
                                                  "404 - Page not found"}))
     :method-not-allowed (constantly (error-page {:status 405
                                                  :title  "405 - Not allowed"}))
     :not-acceptable     (constantly (error-page {:status 406
                                                  :title
                                                  "406 - Not acceptable"}))})))

(defn start-app-routes [] (ring/ring-handler (app-router) (app-route-handler)))

(declare app-routes)
(mount/defstate app-routes :start (start-app-routes))

(defn app [] (middleware/wrap-base #'app-routes))
