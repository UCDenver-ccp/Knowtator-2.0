(ns knowtator.owl-parser-test
  (:require [clojure.java.io :as io]
            [clojure.test :as t :refer [deftest is testing]]
            [knowtator.owl-parser :as sut]
            [tawny.owl :as to]))

(deftest parse-ontology-test
  (testing "Parse basic ontology"
    (let [owl-ontology (-> "test_project_using_uris"
                         io/resource
                         (io/file "Ontologies" "pizza.owl")
                         sut/load-ontology)
          ontology     (sut/parse-ontology owl-ontology)]
      (testing "Ontology meta data"
        (is (= {:type   :ontology
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
                :ontology))))

      (testing "Assertion types"
        (t/are [_ iri f expected] (= expected (-> owl-ontology
                                                (to/entity-for-iri (to/iri iri))
                                                (sut/into-map [:annotation :disjoint :super :equivalent] [:type])
                                                f))
          :some      "http://www.co-ode.org/ontologies/pizza/pizza.owl#ParmaHamTopping" :super
          ["http://www.co-ode.org/ontologies/pizza/pizza.owl#HamTopping"
           {:type :some
            :data
            #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#Mild"
              "http://www.co-ode.org/ontologies/pizza/pizza.owl#hasSpiciness"}}]

          :and       "http://www.co-ode.org/ontologies/pizza/pizza.owl#ThinAndCrispyPizza" :equivalent
          [{:type :and
            :data
            #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"
              {:type :only
               :data
               #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#hasBase"
                 "http://www.co-ode.org/ontologies/pizza/pizza.owl#ThinAndCrispyBase"}}}}]

          :not       "http://www.co-ode.org/ontologies/pizza/pizza.owl#VegetarianPizza" :equivalent
          [{:type :and
            :data
            #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"
              {:type :not
               :data
               #{{:type :some
                  :data
                  #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#hasTopping"
                    "http://www.co-ode.org/ontologies/pizza/pizza.owl#MeatTopping"}}}}
              {:type :not
               :data
               #{{:type :some
                  :data
                  #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#hasTopping"
                    "http://www.co-ode.org/ontologies/pizza/pizza.owl#FishTopping"}}}}}}]

          :or        "http://www.co-ode.org/ontologies/pizza/pizza.owl#VegetarianTopping" :equivalent
          [{:type :and
            :data
            #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#PizzaTopping"
              {:type :or
               :data
               #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#VegetableTopping"
                 "http://www.co-ode.org/ontologies/pizza/pizza.owl#NutTopping"
                 "http://www.co-ode.org/ontologies/pizza/pizza.owl#FruitTopping"
                 "http://www.co-ode.org/ontologies/pizza/pizza.owl#SauceTopping"
                 "http://www.co-ode.org/ontologies/pizza/pizza.owl#CheeseTopping"
                 "http://www.co-ode.org/ontologies/pizza/pizza.owl#HerbSpiceTopping"}}}}]

          :only      "http://www.co-ode.org/ontologies/pizza/pizza.owl#ThinAndCrispyPizza" :equivalent
          [{:type :and
            :data
            #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"
              {:type :only
               :data
               #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#hasBase"
                 "http://www.co-ode.org/ontologies/pizza/pizza.owl#ThinAndCrispyBase"}}}}]

          :oneof     "http://www.co-ode.org/ontologies/pizza/pizza.owl#Country" :equivalent
          [{:type :and
            :data
            #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#DomainConcept"
              {:type :oneof
               :data
               #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#France"
                 "http://www.co-ode.org/ontologies/pizza/pizza.owl#Italy"
                 "http://www.co-ode.org/ontologies/pizza/pizza.owl#Germany"
                 "http://www.co-ode.org/ontologies/pizza/pizza.owl#England"
                 "http://www.co-ode.org/ontologies/pizza/pizza.owl#America"}}}}]

          :has-value "http://www.co-ode.org/ontologies/pizza/pizza.owl#MozzarellaTopping" :super
          ["http://www.co-ode.org/ontologies/pizza/pizza.owl#CheeseTopping"
           {:type :some
            :data
            #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#Mild"
              "http://www.co-ode.org/ontologies/pizza/pizza.owl#hasSpiciness"}}
           {:type :has-value
            :data
            ["http://www.co-ode.org/ontologies/pizza/pizza.owl#hasCountryOfOrigin"
             "http://www.co-ode.org/ontologies/pizza/pizza.owl#Italy"]}]

          :at-least  "http://www.co-ode.org/ontologies/pizza/pizza.owl#InterestingPizza" :equivalent
          [{:type :and
            :data
            #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"
              {:type  :at-least
               :value 3
               :data
               #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#hasTopping"
                 "http://www.w3.org/2002/07/owl#Thing"}}}}]))

      (testing "OWL classes"
        (is (= [{:super      ["http://www.co-ode.org/ontologies/pizza/pizza.owl#HamTopping"
                              {:type :some
                               :data #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#Mild"
                                       "http://www.co-ode.org/ontologies/pizza/pizza.owl#hasSpiciness"}}]
                 :type       :class
                 :iri        "http://www.co-ode.org/ontologies/pizza/pizza.owl#ParmaHamTopping"
                 :annotation [{:type    :annotation
                               :iri     "http://www.w3.org/2004/02/skos/core#prefLabel"
                               :literal {:value "Parma Ham"
                                         :lang  "en"}}
                              {:type    :label
                               :literal {:value "ParmaHamTopping"
                                         :lang  "en"}}
                              {:type    :label
                               :literal {:value "CoberturaDePrezuntoParma"
                                         :lang  "pt"}}]}
                {:disjoint   ["http://www.co-ode.org/ontologies/pizza/pizza.owl#CaperTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#GarlicTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#OliveTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#TomatoTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#SpinachTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#OnionTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#PepperTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#LeekTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#MushroomTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#PetitPoisTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#ArtichokeTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#RocketTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#AsparagusTopping"]
                 :super      [{:type :some
                               :data #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#Mild"
                                       "http://www.co-ode.org/ontologies/pizza/pizza.owl#hasSpiciness"}}
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#VegetableTopping"]
                 :type       :class
                 :iri        "http://www.co-ode.org/ontologies/pizza/pizza.owl#LeekTopping"
                 :annotation [{:type    :annotation
                               :iri     "http://www.w3.org/2004/02/skos/core#prefLabel"
                               :literal {:value "Leek"
                                         :lang  "en"}}
                              {:type    :label
                               :literal {:value "LeekTopping"
                                         :lang  "en"}}
                              {:type    :label
                               :literal {:value "CoberturaDeLeek"
                                         :lang  "pt"}}]}
                {:disjoint   ["http://www.co-ode.org/ontologies/pizza/pizza.owl#CaperTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#GarlicTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#OliveTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#TomatoTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#SpinachTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#OnionTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#PepperTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#LeekTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#MushroomTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#PetitPoisTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#ArtichokeTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#RocketTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#AsparagusTopping"]
                 :super      ["http://www.co-ode.org/ontologies/pizza/pizza.owl#VegetableTopping"
                              {:type :some
                               :data #{"http://www.co-ode.org/ontologies/pizza/pizza.owl#Mild"
                                       "http://www.co-ode.org/ontologies/pizza/pizza.owl#hasSpiciness"}}]
                 :type       :class
                 :iri        "http://www.co-ode.org/ontologies/pizza/pizza.owl#CaperTopping"
                 :annotation [{:type    :annotation
                               :iri     "http://www.w3.org/2004/02/skos/core#prefLabel"
                               :literal {:value "Caper" :lang "en"}}
                              {:type    :label
                               :literal {:value "CaperTopping"
                                         :lang  "en"}}
                              {:type    :label
                               :literal {:value "CoberturaDeCaper"
                                         :lang  "pt"}}]}
                {:super      ["http://www.co-ode.org/ontologies/pizza/pizza.owl#CheeseTopping"
                              "http://www.co-ode.org/ontologies/pizza/pizza.owl#VegetableTopping"]
                 :type       :class
                 :iri        "http://www.co-ode.org/ontologies/pizza/pizza.owl#CheeseyVegetableTopping"
                 :annotation [{:type    :comment
                               :literal {:value "This class will be unsatisfiable. This is because we have given it 2 disjoint parents, which means it could never have any instances (as nothing can be both a CheeseTopping and a VegetableTopping). NB Called ProbeInconsistentTopping in the ProtegeOWL Tutorial."
                                         :lang  "en"}}
                              {:type    :label
                               :literal {:value "CheesyVegetableTopping"
                                         :lang  "en"}}
                              {:type    :label
                               :literal {:value "CoberturaDeQueijoComVegetais"
                                         :lang  "pt"}}]}]
              (->> ontology
                :classes
                (take 4)))))

      (testing "OWL annotation properties"
        (is (= [{:type :aproperty
                 :iri  "http://www.w3.org/2004/02/skos/core#prefLabel"}
                {:type :aproperty
                 :iri  "http://www.w3.org/2000/01/rdf-schema#comment"}
                {:type :aproperty
                 :iri  "http://www.w3.org/2004/02/skos/core#definition"}
                {:type :aproperty
                 :iri  "http://purl.org/dc/elements/1.1/title"}]
              (->> ontology
                :ann-props
                (take 4)))))

      (testing "OWL object properties"
        (is (= [{:inverse        "http://www.co-ode.org/ontologies/pizza/pizza.owl#isToppingOf"
                 :super          ["http://www.co-ode.org/ontologies/pizza/pizza.owl#hasIngredient"]
                 :type           :oproperty
                 :characteristic :inversefunctional
                 :iri            "http://www.co-ode.org/ontologies/pizza/pizza.owl#hasTopping"
                 :domain         ["http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"]
                 :annotation     [{:type :comment
                                   :literal
                                   {:value
                                    "Note that hasTopping is inverse functional because isToppingOf is functional"
                                    :lang "en"}}]
                 :range          ["http://www.co-ode.org/ontologies/pizza/pizza.owl#PizzaTopping"]}
                {:type           :oproperty
                 :characteristic :functional
                 :iri            "http://www.co-ode.org/ontologies/pizza/pizza.owl#hasSpiciness"
                 :annotation     [{:type    :comment
                                   :literal {:value
                                             "A property created to be used with the ValuePartition - Spiciness."
                                             :lang "en"}}]
                 :range          ["http://www.co-ode.org/ontologies/pizza/pizza.owl#Spiciness"]}
                {:inverse        "http://www.co-ode.org/ontologies/pizza/pizza.owl#hasIngredient"
                 :type           :oproperty
                 :characteristic :transitive
                 :iri            "http://www.co-ode.org/ontologies/pizza/pizza.owl#isIngredientOf"
                 :annotation     [{:type    :comment
                                   :literal {:value "The inverse property tree to hasIngredient - all subproperties and attributes of the properties should reflect those under hasIngredient."
                                             :lang  "en"}}]}
                {:inverse        "http://www.co-ode.org/ontologies/pizza/pizza.owl#hasBase"
                 :super          ["http://www.co-ode.org/ontologies/pizza/pizza.owl#isIngredientOf"]
                 :type           :oproperty
                 :characteristic :functional
                 :iri            "http://www.co-ode.org/ontologies/pizza/pizza.owl#isBaseOf"}]
              (->> ontology
                :obj-props
                (take 4))))))))
