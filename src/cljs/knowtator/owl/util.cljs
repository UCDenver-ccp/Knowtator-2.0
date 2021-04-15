(ns knowtator.owl.util)

(defn map->iri [iri-m]
  ((comp (partial apply str) (juxt :namespace :fragment) :iri) iri-m))
