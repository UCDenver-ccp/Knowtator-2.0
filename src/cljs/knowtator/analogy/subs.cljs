(ns knowtator.analogy.subs
  (:require [re-frame.core :refer [reg-sub]]
            [knowtator.model :as model]
            [knowtator.util :as util]
            [com.rpl.specter :as sp]))

(def default-mop-map
  {:hierarchy
   {:ancestors
    {:Bar #{:sme-clj.typedef/Entity :thing}
     :Beaker #{:sme-clj.typedef/Entity :thing}
     :Coffee #{:sme-clj.typedef/Entity :thing}
     :Heat #{:sme-clj.typedef/Entity :thing}
     :Icecube #{:sme-clj.typedef/Entity :thing}
     :Pipe #{:sme-clj.typedef/Entity :thing}
     :Vial #{:sme-clj.typedef/Entity :thing}
     :Water #{:sme-clj.typedef/Entity :thing}
     :cause #{:sme-clj.typedef/Expression :sme-clj.typedef/Functor
              :sme-clj.typedef/Relation :thing}
     :cause-greater-pressure-Beaker-pressure-Vial-flow-Beaker-Vial-Water-Pipe
     #{:sme-clj.typedef/Expression :thing}
     :clear #{:sme-clj.typedef/Attribute :sme-clj.typedef/Expression
              :sme-clj.typedef/Functor :thing}
     :clear-Beaker #{:sme-clj.typedef/Expression :thing}
     :diameter #{:sme-clj.typedef/Expression :sme-clj.typedef/Function
                 :sme-clj.typedef/Functor :thing}
     :diameter-Beaker #{:sme-clj.typedef/Expression :thing}
     :diameter-Vial #{:sme-clj.typedef/Expression :thing}
     :flat-top #{:sme-clj.typedef/Expression :sme-clj.typedef/Function
                 :sme-clj.typedef/Functor :thing}
     :flat-top-Coffee #{:sme-clj.typedef/Expression :thing}
     :flat-top-Water #{:sme-clj.typedef/Expression :thing}
     :flow #{:sme-clj.typedef/Expression :sme-clj.typedef/Functor
             :sme-clj.typedef/Relation :thing}
     :flow-Beaker-Vial-Water-Pipe #{:sme-clj.typedef/Expression :thing}
     :flow-Coffee-Icecube-Heat-Bar #{:sme-clj.typedef/Expression :thing}
     :greater #{:sme-clj.typedef/Expression :sme-clj.typedef/Functor
                :sme-clj.typedef/Relation :thing}
     :greater-diameter-Beaker-diameter-Vial #{:sme-clj.typedef/Expression
                                              :thing}
     :greater-pressure-Beaker-pressure-Vial #{:sme-clj.typedef/Expression
                                              :thing}
     :greater-temperature-Coffee-temperature-Icecube
     #{:sme-clj.typedef/Expression :thing}
     :liquid #{:sme-clj.typedef/Attribute :sme-clj.typedef/Expression
               :sme-clj.typedef/Functor :thing}
     :liquid-Coffee #{:sme-clj.typedef/Expression :thing}
     :liquid-Water #{:sme-clj.typedef/Expression :thing}
     :pressure #{:sme-clj.typedef/Expression :sme-clj.typedef/Function
                 :sme-clj.typedef/Functor :thing}
     :pressure-Beaker #{:sme-clj.typedef/Expression :thing}
     :pressure-Vial #{:sme-clj.typedef/Expression :thing}
     :simple-heat-flow #{:sme-clj.typedef/ConceptGraph}
     :simple-water-flow #{:sme-clj.typedef/ConceptGraph}
     :sme-clj.typedef/Attribute #{:sme-clj.typedef/Expression
                                  :sme-clj.typedef/Functor :thing}
     :sme-clj.typedef/Entity #{:thing}
     :sme-clj.typedef/Expression #{:thing}
     :sme-clj.typedef/Function #{:sme-clj.typedef/Expression
                                 :sme-clj.typedef/Functor :thing}
     :sme-clj.typedef/Functor #{:sme-clj.typedef/Expression :thing}
     :sme-clj.typedef/Relation #{:sme-clj.typedef/Expression
                                 :sme-clj.typedef/Functor :thing}
     :temperature #{:sme-clj.typedef/Expression :sme-clj.typedef/Function
                    :sme-clj.typedef/Functor :thing}
     :temperature-Coffee #{:sme-clj.typedef/Expression :thing}
     :temperature-Icecube #{:sme-clj.typedef/Expression :thing}}
    :descendants
    {:sme-clj.typedef/Attribute #{:clear :liquid}
     :sme-clj.typedef/ConceptGraph #{:simple-heat-flow :simple-water-flow}
     :sme-clj.typedef/Entity #{:Bar :Beaker :Coffee :Heat :Icecube :Pipe :Vial
                               :Water}
     :sme-clj.typedef/Expression
     #{:cause
       :cause-greater-pressure-Beaker-pressure-Vial-flow-Beaker-Vial-Water-Pipe
       :clear :clear-Beaker :diameter :diameter-Beaker :diameter-Vial :flat-top
       :flat-top-Coffee :flat-top-Water :flow :flow-Beaker-Vial-Water-Pipe
       :flow-Coffee-Icecube-Heat-Bar :greater
       :greater-diameter-Beaker-diameter-Vial
       :greater-pressure-Beaker-pressure-Vial
       :greater-temperature-Coffee-temperature-Icecube :liquid :liquid-Coffee
       :liquid-Water :pressure :pressure-Beaker :pressure-Vial
       :sme-clj.typedef/Attribute :sme-clj.typedef/Function
       :sme-clj.typedef/Functor :sme-clj.typedef/Relation :temperature
       :temperature-Coffee :temperature-Icecube}
     :sme-clj.typedef/Function #{:diameter :flat-top :pressure :temperature}
     :sme-clj.typedef/Functor #{:cause :clear :diameter :flat-top :flow :greater
                                :liquid :pressure :sme-clj.typedef/Attribute
                                :sme-clj.typedef/Function
                                :sme-clj.typedef/Relation :temperature}
     :sme-clj.typedef/Relation #{:cause :flow :greater}
     :thing
     #{:Bar :Beaker :Coffee :Heat :Icecube :Pipe :Vial :Water :cause
       :cause-greater-pressure-Beaker-pressure-Vial-flow-Beaker-Vial-Water-Pipe
       :clear :clear-Beaker :diameter :diameter-Beaker :diameter-Vial :flat-top
       :flat-top-Coffee :flat-top-Water :flow :flow-Beaker-Vial-Water-Pipe
       :flow-Coffee-Icecube-Heat-Bar :greater
       :greater-diameter-Beaker-diameter-Vial
       :greater-pressure-Beaker-pressure-Vial
       :greater-temperature-Coffee-temperature-Icecube :liquid :liquid-Coffee
       :liquid-Water :pressure :pressure-Beaker :pressure-Vial
       :sme-clj.typedef/Attribute :sme-clj.typedef/Entity
       :sme-clj.typedef/Expression :sme-clj.typedef/Function
       :sme-clj.typedef/Functor :sme-clj.typedef/Relation :temperature
       :temperature-Coffee :temperature-Icecube}}
    :parents
    {:Bar #{:sme-clj.typedef/Entity}
     :Beaker #{:sme-clj.typedef/Entity}
     :Coffee #{:sme-clj.typedef/Entity}
     :Heat #{:sme-clj.typedef/Entity}
     :Icecube #{:sme-clj.typedef/Entity}
     :Pipe #{:sme-clj.typedef/Entity}
     :Vial #{:sme-clj.typedef/Entity}
     :Water #{:sme-clj.typedef/Entity}
     :cause #{:sme-clj.typedef/Relation}
     :cause-greater-pressure-Beaker-pressure-Vial-flow-Beaker-Vial-Water-Pipe
     #{:sme-clj.typedef/Expression}
     :clear #{:sme-clj.typedef/Attribute}
     :clear-Beaker #{:sme-clj.typedef/Expression}
     :diameter #{:sme-clj.typedef/Function}
     :diameter-Beaker #{:sme-clj.typedef/Expression}
     :diameter-Vial #{:sme-clj.typedef/Expression}
     :flat-top #{:sme-clj.typedef/Function}
     :flat-top-Coffee #{:sme-clj.typedef/Expression}
     :flat-top-Water #{:sme-clj.typedef/Expression}
     :flow #{:sme-clj.typedef/Relation}
     :flow-Beaker-Vial-Water-Pipe #{:sme-clj.typedef/Expression}
     :flow-Coffee-Icecube-Heat-Bar #{:sme-clj.typedef/Expression}
     :greater #{:sme-clj.typedef/Relation}
     :greater-diameter-Beaker-diameter-Vial #{:sme-clj.typedef/Expression}
     :greater-pressure-Beaker-pressure-Vial #{:sme-clj.typedef/Expression}
     :greater-temperature-Coffee-temperature-Icecube
     #{:sme-clj.typedef/Expression}
     :liquid #{:sme-clj.typedef/Attribute}
     :liquid-Coffee #{:sme-clj.typedef/Expression}
     :liquid-Water #{:sme-clj.typedef/Expression}
     :pressure #{:sme-clj.typedef/Function}
     :pressure-Beaker #{:sme-clj.typedef/Expression}
     :pressure-Vial #{:sme-clj.typedef/Expression}
     :simple-heat-flow #{:sme-clj.typedef/ConceptGraph}
     :simple-water-flow #{:sme-clj.typedef/ConceptGraph}
     :sme-clj.typedef/Attribute #{:sme-clj.typedef/Functor}
     :sme-clj.typedef/Entity #{:thing}
     :sme-clj.typedef/Expression #{:thing}
     :sme-clj.typedef/Function #{:sme-clj.typedef/Functor}
     :sme-clj.typedef/Functor #{:sme-clj.typedef/Expression}
     :sme-clj.typedef/Relation #{:sme-clj.typedef/Functor}
     :temperature #{:sme-clj.typedef/Function}
     :temperature-Coffee #{:sme-clj.typedef/Expression}
     :temperature-Icecube #{:sme-clj.typedef/Expression}}}
   :ids {}
   :mops
   {:Bar {:id      :Bar
          :inst?   false
          :names   #{}
          :parents #{:sme-clj.typedef/Entity}}
    :Beaker {:id      :Beaker
             :inst?   false
             :names   #{}
             :parents #{:sme-clj.typedef/Entity}}
    :Coffee {:id      :Coffee
             :inst?   false
             :names   #{}
             :parents #{:sme-clj.typedef/Entity}}
    :Heat {:id      :Heat
           :inst?   false
           :names   #{}
           :parents #{:sme-clj.typedef/Entity}}
    :Icecube {:id      :Icecube
              :inst?   false
              :names   #{}
              :parents #{:sme-clj.typedef/Entity}}
    :Pipe {:id      :Pipe
           :inst?   false
           :names   #{}
           :parents #{:sme-clj.typedef/Entity}}
    :Vial {:id      :Vial
           :inst?   false
           :names   #{}
           :parents #{:sme-clj.typedef/Entity}}
    :Water {:id      :Water
            :inst?   false
            :names   #{}
            :parents #{:sme-clj.typedef/Entity}}
    :cause {:e1      #{:sme-clj.typedef/Expression}
            :e2      #{:sme-clj.typedef/Expression}
            :id      :cause
            :inst?   false
            :names   #{}
            :parents #{:sme-clj.typedef/Relation}}
    :cause-greater-pressure-Beaker-pressure-Vial-flow-Beaker-Vial-Water-Pipe
    {:concept-graph #{:simple-water-flow}
     :e1 #{:greater-pressure-Beaker-pressure-Vial}
     :e2 #{:flow-Beaker-Vial-Water-Pipe}
     :functor #{:cause}
     :id
     :cause-greater-pressure-Beaker-pressure-Vial-flow-Beaker-Vial-Water-Pipe
     :inst? false
     :names #{}
     :parents #{:sme-clj.typedef/Expression}}
    :clear {:e1      #{:sme-clj.typedef/Entity}
            :id      :clear
            :inst?   false
            :names   #{}
            :parents #{:sme-clj.typedef/Attribute}}
    :clear-Beaker {:concept-graph #{:simple-water-flow}
                   :e1            #{:Beaker}
                   :functor       #{:clear}
                   :id            :clear-Beaker
                   :inst?         false
                   :names         #{}
                   :parents       #{:sme-clj.typedef/Expression}}
    :diameter {:e1      #{:sme-clj.typedef/Entity}
               :id      :diameter
               :inst?   false
               :names   #{}
               :parents #{:sme-clj.typedef/Function}}
    :diameter-Beaker {:concept-graph #{:simple-water-flow}
                      :e1            #{:Beaker}
                      :functor       #{:diameter}
                      :id            :diameter-Beaker
                      :inst?         false
                      :names         #{}
                      :parents       #{:sme-clj.typedef/Expression}}
    :diameter-Vial {:concept-graph #{:simple-water-flow}
                    :e1            #{:Vial}
                    :functor       #{:diameter}
                    :id            :diameter-Vial
                    :inst?         false
                    :names         #{}
                    :parents       #{:sme-clj.typedef/Expression}}
    :flat-top {:e1      #{:sme-clj.typedef/Entity}
               :id      :flat-top
               :inst?   false
               :names   #{}
               :parents #{:sme-clj.typedef/Function}}
    :flat-top-Coffee {:concept-graph #{:simple-heat-flow}
                      :e1            #{:Coffee}
                      :functor       #{:flat-top}
                      :id            :flat-top-Coffee
                      :inst?         false
                      :names         #{}
                      :parents       #{:sme-clj.typedef/Expression}}
    :flat-top-Water {:concept-graph #{:simple-water-flow}
                     :e1            #{:Water}
                     :functor       #{:flat-top}
                     :id            :flat-top-Water
                     :inst?         false
                     :names         #{}
                     :parents       #{:sme-clj.typedef/Expression}}
    :flow {:e1      #{:sme-clj.typedef/Entity}
           :e2      #{:sme-clj.typedef/Entity}
           :e3      #{:sme-clj.typedef/Entity}
           :e4      #{:sme-clj.typedef/Entity}
           :id      :flow
           :inst?   false
           :names   #{}
           :parents #{:sme-clj.typedef/Relation}}
    :flow-Beaker-Vial-Water-Pipe {:concept-graph #{:simple-water-flow}
                                  :e1            #{:Beaker}
                                  :e2            #{:Vial}
                                  :e3            #{:Water}
                                  :e4            #{:Pipe}
                                  :functor       #{:flow}
                                  :id            :flow-Beaker-Vial-Water-Pipe
                                  :inst?         false
                                  :names         #{}
                                  :parents       #{:sme-clj.typedef/Expression}}
    :flow-Coffee-Icecube-Heat-Bar {:concept-graph #{:simple-heat-flow}
                                   :e1 #{:Coffee}
                                   :e2 #{:Icecube}
                                   :e3 #{:Heat}
                                   :e4 #{:Bar}
                                   :functor #{:flow}
                                   :id :flow-Coffee-Icecube-Heat-Bar
                                   :inst? false
                                   :names #{}
                                   :parents #{:sme-clj.typedef/Expression}}
    :greater {:e1      #{:sme-clj.typedef/Expression}
              :e2      #{:sme-clj.typedef/Expression}
              :id      :greater
              :inst?   false
              :names   #{}
              :parents #{:sme-clj.typedef/Relation}}
    :greater-diameter-Beaker-diameter-Vial
    {:concept-graph #{:simple-water-flow}
     :e1            #{:diameter-Beaker}
     :e2            #{:diameter-Vial}
     :functor       #{:greater}
     :id            :greater-diameter-Beaker-diameter-Vial
     :inst?         false
     :names         #{}
     :parents       #{:sme-clj.typedef/Expression}}
    :greater-pressure-Beaker-pressure-Vial
    {:concept-graph #{:simple-water-flow}
     :e1            #{:pressure-Beaker}
     :e2            #{:pressure-Vial}
     :functor       #{:greater}
     :id            :greater-pressure-Beaker-pressure-Vial
     :inst?         false
     :names         #{}
     :parents       #{:sme-clj.typedef/Expression}}
    :greater-temperature-Coffee-temperature-Icecube
    {:concept-graph #{:simple-heat-flow}
     :e1            #{:temperature-Coffee}
     :e2            #{:temperature-Icecube}
     :functor       #{:greater}
     :id            :greater-temperature-Coffee-temperature-Icecube
     :inst?         false
     :names         #{}
     :parents       #{:sme-clj.typedef/Expression}}
    :liquid {:e1      #{:sme-clj.typedef/Entity}
             :id      :liquid
             :inst?   false
             :names   #{}
             :parents #{:sme-clj.typedef/Attribute}}
    :liquid-Coffee {:concept-graph #{:simple-heat-flow}
                    :e1            #{:Coffee}
                    :functor       #{:liquid}
                    :id            :liquid-Coffee
                    :inst?         false
                    :names         #{}
                    :parents       #{:sme-clj.typedef/Expression}}
    :liquid-Water {:concept-graph #{:simple-water-flow}
                   :e1            #{:Water}
                   :functor       #{:liquid}
                   :id            :liquid-Water
                   :inst?         false
                   :names         #{}
                   :parents       #{:sme-clj.typedef/Expression}}
    :pressure {:e1      #{:sme-clj.typedef/Entity}
               :id      :pressure
               :inst?   false
               :names   #{}
               :parents #{:sme-clj.typedef/Function}}
    :pressure-Beaker {:concept-graph #{:simple-water-flow}
                      :e1            #{:Beaker}
                      :functor       #{:pressure}
                      :id            :pressure-Beaker
                      :inst?         false
                      :names         #{}
                      :parents       #{:sme-clj.typedef/Expression}}
    :pressure-Vial {:concept-graph #{:simple-water-flow}
                    :e1            #{:Vial}
                    :functor       #{:pressure}
                    :id            :pressure-Vial
                    :inst?         false
                    :names         #{}
                    :parents       #{:sme-clj.typedef/Expression}}
    :simple-heat-flow {:id      :simple-heat-flow
                       :inst?   false
                       :names   #{}
                       :parents #{:sme-clj.typedef/ConceptGraph}}
    :simple-water-flow {:id      :simple-water-flow
                        :inst?   false
                        :names   #{}
                        :parents #{:sme-clj.typedef/ConceptGraph}}
    :sme-clj.typedef/Attribute {:id      :sme-clj.typedef/Attribute
                                :inst?   false
                                :names   #{}
                                :parents #{:sme-clj.typedef/Functor}}
    :sme-clj.typedef/Entity {:id      :sme-clj.typedef/Entity
                             :inst?   false
                             :names   #{}
                             :parents #{:thing}}
    :sme-clj.typedef/Expression {:id      :sme-clj.typedef/Expression
                                 :inst?   false
                                 :names   #{}
                                 :parents #{:thing}}
    :sme-clj.typedef/Function {:id      :sme-clj.typedef/Function
                               :inst?   false
                               :names   #{}
                               :parents #{:sme-clj.typedef/Functor}}
    :sme-clj.typedef/Functor {:id      :sme-clj.typedef/Functor
                              :inst?   false
                              :names   #{}
                              :parents #{:sme-clj.typedef/Expression}}
    :sme-clj.typedef/Relation {:id       :sme-clj.typedef/Relation
                               :inst?    false
                               :names    #{}
                               :ordered? #{true}
                               :parents  #{:sme-clj.typedef/Functor}}
    :temperature {:e1      #{:sme-clj.typedef/Entity}
                  :id      :temperature
                  :inst?   false
                  :names   #{}
                  :parents #{:sme-clj.typedef/Function}}
    :temperature-Coffee {:concept-graph #{:simple-heat-flow}
                         :e1            #{:Coffee}
                         :functor       #{:temperature}
                         :id            :temperature-Coffee
                         :inst?         false
                         :names         #{}
                         :parents       #{:sme-clj.typedef/Expression}}
    :temperature-Icecube {:concept-graph #{:simple-heat-flow}
                          :e1            #{:Icecube}
                          :functor       #{:temperature}
                          :id            :temperature-Icecube
                          :inst?         false
                          :names         #{}
                          :parents       #{:sme-clj.typedef/Expression}}}})

(reg-sub ::analogy-graphs
  (fn [_ _]
    (->> [(-> default-mop-map
              (model/mop-map->graph)
              (assoc :id :default))]
         (sort-by (comp name :id) util/compare-alpha-num)
         (#(or % [])))))

(reg-sub ::selection :selection)

(reg-sub ::selected-analogy-graph-id
  :<- [::selection]
  (fn [selected _] (:ana-graphs selected)))

(reg-sub ::selected-node-label (fn [_ _] :id))

(reg-sub ::selected-analogy-graph
  :<- [::analogy-graphs]
  :<- [::selected-analogy-graph-id]
  :<- [::selected-node-label]
  (fn [[graphs id node-label] _]
    (-> graphs
        (->> (sp/select-one [(sp/filterer #(= id (:id %))) sp/FIRST]))
        (update :nodes (partial map #(assoc % :label (get % node-label))))
        (#(or %
              {:nodes []
               :edges []})))))
