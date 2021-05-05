(ns knowtator.db
  (:require [mops.records :as mr]
            [sme-clj.typedef :as types]
            [mops.core :as mops]))

(def default-db
  {:name "re-frame"
   :text-annotation
   {:spans [{:id    :s1
             :ann   :a1
             :start 30
             :end   35}
            {:id    :s2
             :ann   :a1
             :start 36
             :end   44}
            {:id    :s3
             :ann   :a2
             :start 58
             :end   65}
            {:id    :s4
             :ann   :a2
             :start 83
             :end   89}
            {:id    :s5
             :ann   :a1
             :start 124
             :end   132}
            {:id    :s6
             :ann   :a3
             :start 0
             :end   1}]
    :anns [{:id      :a1
            :profile :p1
            :concept :c1
            :doc     :d1}
           {:id      :a2
            :profile :p2
            :concept :c2
            :doc     :d1}
           {:id      :a3
            :profile :p1
            :concept :c1
            :doc     :d2}]
    :profiles [{:id     :p1
                :colors {:c1 "blue"
                         :c2 "green"}}
               {:id     :p2
                :colors {:c1 "red"
                         :c2 "green"}}]
    :docs
    [{:id      :d2
      :content "hey"}
     {:id :d1
      :content
      "Complex trait analysis of the mouse striatum: independent QTLs modulate volume and neuron number\n\nAbstract\n\nBackground\n\nThe striatum plays a pivotal role in modulating motor activity and higher cognitive function. We analyzed variation in striatal volume and neuron number in mice and initiated a complex trait analysis to discover polymorphic genes that modulate the structure of the basal ganglia.\n\nResults\n\nBrain weight, brain and striatal volume, neuron-packing density and number were estimated bilaterally using unbiased stereological procedures in five inbred strains (A/J, C57BL/6J, DBA/2J, BALB/cJ, and BXD5) and an F2 intercross between A/J and BXD5. Striatal volume ranged from 20 to 37 mm3. Neuron-packing density ranged from approximately 50,000 to 100,000 neurons/mm3, and the striatal neuron population ranged from 1.4 to 2.5 million. Inbred animals with larger brains had larger striata but lower neuron-packing density resulting in a narrow range of average neuron populations. In contrast, there was a strong positive correlation between volume and neuron number among intercross progeny. We mapped two quantitative trait loci (QTLs) with selective effects on striatal architecture. Bsc10a maps to the central region of Chr 10 (LRS of 17.5 near D10Mit186) and has intense effects on striatal volume and moderate effects on brain volume. Stnn19a maps to distal Chr 19 (LRS of 15 at D19Mit123) and is associated with differences of up to 400,000 neurons among animals.\n\nConclusion\n\nWe have discovered remarkable numerical and volumetric variation in the mouse striatum, and we have been able to map two QTLs that modulate independent anatomic parameters.\n "}]}
   :selection {:project     "default"
               :docs        :d1
               :anns        nil
               :profiles    :p1
               :concepts    :c1
               :spans       nil
               :review-type :anns
               :ann-props   "http://www.w3.org/2004/02/skos/core#prefLabel"
               :ana-graphs  :default}
   :defaults {:color "#00ffff"}
   :ontology
   {:ann-props [{:type :aproperty
                 :iri  {:fragment  "prefLabel"
                        :namespace "http://www.w3.org/2004/02/skos/core#"}}
                {:type :aproperty
                 :iri  {:fragment  "comment"
                        :namespace "http://www.w3.org/2000/01/rdf-schema#"}}
                {:type :aproperty
                 :iri  {:fragment  "definition"
                        :namespace "http://www.w3.org/2004/02/skos/core#"}}
                {:type :aproperty
                 :iri  {:fragment  "title"
                        :namespace "http://purl.org/dc/elements/1.1/"}}]}
   :analogy
   [(as-> (mr/make-mop-map) mm
      (types/initialize-kg mm)
      (reduce
       (partial apply types/add-entity)
       mm
       [[:mass ::types/Function nil ::types/Entity]
        [:charge ::types/Function nil ::types/Entity]
        [:attracts ::types/Function nil ::types/Entity ::types/Entity]
        [:revolve-around ::types/Function nil ::types/Entity ::types/Entity]
        [:temperature ::types/Function nil ::types/Entity]
        [:gravity ::types/Function nil ::types/Expression ::types/Expression]
        [:opposite-sign ::types/Function nil ::types/Expression
         ::types/Expression]
        [:greater ::types/Relation nil ::types/Entity ::types/Entity]
        [:cause ::types/Relation nil ::types/Expression ::types/Expression]
        [:and ::types/Relation {:ordered? false} ::types/Expression
         ::types/Expression] [:Sun ::types/Entity nil]
        [:Planet ::types/Entity nil] [:Nucleus ::types/Entity nil]
        [:Electron ::types/Entity nil]])
      (apply types/add-concept-graph
             mm
             :solar-system
             (let [attracts    [:attracts :Sun :Planet]
                   mass-sun    [:mass :Sun]
                   mass-planet [:mass :Planet]]
               [[:cause [:and [:greater mass-sun mass-planet] attracts]
                 [:revolve-around :Planet :Sun]]
                [:greater [:temperature :Sun] [:temperature :Planet]]
                [:cause [:gravity mass-sun mass-planet] attracts]]))
      (apply types/add-concept-graph
             mm
             :rutherford-atom
             [[:greater [:mass :Nucleus] [:mass :Electron]]
              [:revolve-around :Electron :Nucleus]
              [:cause [:opposite-sign [:charge :Nucleus] [:charge :Electron]]
               [:attracts :Nucleus :Electron]]])
      (mops/infer-hierarchy mm)
      (assoc mm :id :default))]
   :graph-panels
   [{:id      :gp-0
     :roles   #{:concept-graph :parents}
     :fillers {:concept-graph #{:solar-system}
               :parents       #{::types/Expression ::types/Entity}}}
    {:id      :gp-1
     :roles   #{:concept-graph :parents}
     :fillers {:concept-graph #{:rutherford-atom}
               :parents       #{::types/Expression ::types/Entity}}}]})
