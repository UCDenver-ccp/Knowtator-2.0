(ns knowtator.owl-parser-test
  (:require [knowtator.owl-parser :as sut]
            [clojure.test :refer [deftest testing is]]
            [clojure.java.io :as io]))

(deftest parse-ontology-test
  (testing "Parse basic ontology"
    (let [owl-ontology (-> "test_project_using_uris"
                         io/resource
                         (io/file "Ontologies" "pizza.owl")
                         sut/load-ontology)
          ontology     (sut/parse-ontology owl-ontology)]
      (is (= {:type   [:ontology]
              :prefix "http://www.co-ode.org/ontologies/pizza/pizza.owl#"
              :iri    "http://www.co-ode.org/ontologies/pizza"
              :viri   "http://www.co-ode.org/ontologies/pizza/2.0.0"
              :annotation
              [{:type :annotation
                :iri  "http://purl.org/dc/elements/1.1/description"
                :literal
                {:value
                 "An ontology about pizzas and their toppings.\n\nThis is an example ontology that contains all constructs required for the various versions of the Pizza Tutorial run by Manchester University (see http://owl.cs.manchester.ac.uk/publications/talks-and-tutorials/protg-owl-tutorial)."
                 :lang "en"}}
               {:type    :annotation
                :iri     "http://purl.org/dc/elements/1.1/title"
                :literal {:value "pizza" :lang "en"}}
               {:type    :annotation
                :iri     "http://purl.org/dc/terms/contributor"
                :literal {:value "Alan Rector" :type :RDF_PLAIN_LITERAL}}
               {:type    :annotation
                :iri     "http://purl.org/dc/terms/contributor"
                :literal {:value "Chris Wroe" :type :RDF_PLAIN_LITERAL}}
               {:type    :annotation
                :iri     "http://purl.org/dc/terms/contributor"
                :literal {:value "Matthew Horridge" :type :RDF_PLAIN_LITERAL}}
               {:type    :annotation
                :iri     "http://purl.org/dc/terms/contributor"
                :literal {:value "Nick Drummond" :type :RDF_PLAIN_LITERAL}}
               {:type    :annotation
                :iri     "http://purl.org/dc/terms/contributor"
                :literal {:value "Robert Stevens" :type :RDF_PLAIN_LITERAL}}
               {:type :annotation
                :iri  "http://purl.org/dc/terms/license"
                :literal
                {:value "Creative Commons Attribution 3.0 (CC BY 3.0)"
                 :type  :XSD_STRING}}
               {:type :annotation
                :iri  "http://purl.org/dc/terms/provenance"
                :literal
                {:value
                 "v2.0 Added new conceptAnnotations to the ontology using standard/well-know conceptAnnotation properties\n\nv1.5. Removed protege.owl import and references. Made ontology URI date-independent\n\nv1.4. Added Food class (used in domain/range of hasIngredient), Added several hasCountryOfOrigin restrictions on pizzas, Made hasTopping invers functional"
                 :lang "en"}}
               {:type :label :literal {:value "pizza" :type :XSD_STRING}}
               {:type    :annotation
                :iri     "http://www.w3.org/2002/07/owl#versionInfo"
                :literal {:value "2.0" :type :XSD_STRING}}]}
            (-> ontology
              :ontology
              (update :type vec)))))))
