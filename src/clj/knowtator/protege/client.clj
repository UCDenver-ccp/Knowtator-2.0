(ns knowtator.protege.client
  (:require
   [aleph.http              :as http]
   [knowtator.owl-parser    :as op]
   [manifold.stream         :as s]
   [ring.middleware.transit :refer [decode]]
   [tawny.render            :as tr]
   [tawny.owl               :as to]
   [meander.epsilon         :as m]
   [tawny.protocol          :as tp]))

(def host "localhost")
(def port 10003)
(def url (format "ws://%s:%d" host port))

(comment
  (def edits (atom []))
  (let [protege-conn (->> @(http/websocket-client (str url "/protege"))
                          (s/map decode))]
    @(s/consume #(swap! edits conj %) protege-conn)
    #_(println "new: " @(s/take! protege-conn))
    #_@(s/consume #(println "message: " %) protege-conn)))

(defn incorporate-axiom
  [axiom ontology]
  (let [{:keys [axiomType]} axiom]
    (case axiomType
      :Declaration (let [[entity-type [iri-type iri]] (:entity axiom)
                         new-entity                   {:type entity-type
                                                       :iri  (op/->iri iri)}]
                     new-entity
                     #_(update ontology :classes conj new-entity))
      :SubClassOf)))
(-> @edits
    first
    (nth 1)
    (incorporate-axiom knowtator.owl-parser-test/test-ontology-map)
    #_#_#_#_#_first :entity second second tp/as-iri)

(-> knowtator.owl-parser-test/test-ontology-map
    #_keys
    #_:classes
    #_first)
{:annotation [{:iri     "http://www.w3.org/2004/02/skos/core#prefLabel"
               :literal {:lang  "en"
                         :value "Parma Ham"}
               :type    :annotation}
              {:literal {:lang  "en"
                         :value "ParmaHamTopping"}
               :type    :label}
              {:literal {:lang  "pt"
                         :value "CoberturaDePrezuntoParma"}
               :type    :label}]
 :iri {:fragment  "ParmaHamTopping"
       :namespace "http://www.co-ode.org/ontologies/pizza/pizza.owl#"}
 :super ["http://www.co-ode.org/ontologies/pizza/pizza.owl#HamTopping"
         {:data
          #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#Mild"
            "http://www.co-ode.org/ontologies/pizza/pizza.owl#hasSpiciness"}
          :type :some}]
 :type :class}
'({:annotated            false
   :annotationAxiom      false
   :annotations          ()
   :anonymousIndividuals ()
   :axiomType            :Declaration
   :bottomEntity         false
   :change               :added
   :entity               (owl-class
                          (iri
                           "http://www.co-ode.org/ontologies/pizza#Georgia"))
   :logicalAxiom         false
   :topEntity            false}
  {:GCI false
   :annotated false
   :annotationAxiom false
   :annotations ()
   :anonymousIndividuals ()
   :axiomType :SubClassOf
   :bottomEntity false
   :change :added
   :logicalAxiom true
   :subClass (owl-class (iri "http://www.co-ode.org/ontologies/pizza#Georgia"))
   :superClass
   (owl-class (iri "http://www.co-ode.org/ontologies/pizza/pizza.owl#Country"))
   :topEntity false})

