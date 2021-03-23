(ns knowtator.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [knowtator.html-colors :as html-colors]
            [miner.strgen :as sg]))

(s/def ::id keyword?)
(s/def :span/start (s/and int? (complement neg?)))
(s/def :span/end (s/and int? (complement neg?)))
(s/def :span/ann ::id)
(s/def :ann/profile ::id)
(s/def :ann/concept ::id)
(s/def :ann/doc ::id)
(s/def ::content string?)
(def hex-color-re #"^#[0-9a-fA-F]{3,6}$")
(s/def ::hex-color (s/spec (s/and string? (partial re-matches hex-color-re))
                     :gen #(sg/string-generator hex-color-re)))

(s/def ::color (s/or :hex ::hex-color :html html-colors/html-colors :lower-html (->> html-colors/html-colors
                                                                                  keys
                                                                                  (map str/lower-case)
                                                                                  set)))

(s/def ::span (s/keys :req-un [::id :span/ann :span/start :span/end]))

(s/def :span-overlap/id (s/coll-of ::id :kind set?))
(s/def :span-overlap/ann (s/coll-of :span/ann :kind set?))
(s/def :span-overlap/span (s/keys :req-un [:span/start :span/end :span-overlap/id :span-overlap/ann]))

(s/def ::ann (s/keys :req-un [::id :ann/doc :ann/profile :ann/concept]))
(s/def ::doc (s/keys :req-un [::id ::content]))
(s/def ::colors (s/map-of :ann/concept ::color))
(s/def ::profile (s/keys :req-un [::id ::colors]))

(s/def ::restriction (s/coll-of ::id :kind vector?))


(s/def ::content string?)
(s/def ::spans-with-spanned-text (s/keys :req-un [::id :span/ann :span/start :span/end ::content]))
