(ns knowtator.db)

(def default-db
  {:spans    {:s1 {:id    :s1
                   :ann   :a1
                   :start 30
                   :end   35}
              :s2 {:id    :s2
                   :ann   :a1
                   :start 36
                   :end   44}
              :s3 {:id    :s3
                   :ann   :a2
                   :start 58
                   :end   65}
              :s4 {:id    :s4
                   :ann   :a2
                   :start 83
                   :end   89}
              :s5 {:id    :s5
                   :ann   :a1
                   :start 124
                   :end   132}}
   :anns     {:a1 {:id      :a1
                   :profile :p1
                   :concept :c1
                   :doc     :d1}
              :a2 {:id      :a2
                   :profile :p1
                   :concept :c2
                   :doc     :d1}}
   :profiles {:p1 {:c1 "blue"
                   :c2 "green"}}
   :docs     {:d2 {:id      :d2
                   :content "hey"}
              :d1 {:id :d1
                   :content
                   "Complex trait analysis of the mouse striatum: independent QTLs modulate volume and neuron number

Abstract

Background

The striatum plays a pivotal role in modulating motor activity and higher cognitive function. We analyzed variation in striatal volume and neuron number in mice and initiated a complex trait analysis to discover polymorphic genes that modulate the structure of the basal ganglia.

Results

Brain weight, brain and striatal volume, neuron-packing density and number were estimated bilaterally using unbiased stereological procedures in five inbred strains (A/J, C57BL/6J, DBA/2J, BALB/cJ, and BXD5) and an F2 intercross between A/J and BXD5. Striatal volume ranged from 20 to 37 mm3. Neuron-packing density ranged from approximately 50,000 to 100,000 neurons/mm3, and the striatal neuron population ranged from 1.4 to 2.5 million. Inbred animals with larger brains had larger striata but lower neuron-packing density resulting in a narrow range of average neuron populations. In contrast, there was a strong positive correlation between volume and neuron number among intercross progeny. We mapped two quantitative trait loci (QTLs) with selective effects on striatal architecture. Bsc10a maps to the central region of Chr 10 (LRS of 17.5 near D10Mit186) and has intense effects on striatal volume and moderate effects on brain volume. Stnn19a maps to distal Chr 19 (LRS of 15 at D19Mit123) and is associated with differences of up to 400,000 neurons among animals.

Conclusion

We have discovered remarkable numerical and volumetric variation in the mouse striatum, and we have been able to map two QTLs that modulate independent anatomic parameters.
"}}
   :selection {:doc  :d1
               :ann  nil
               :span nil}})
