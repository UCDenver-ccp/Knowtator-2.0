(ns knowtator.model.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [miner.strgen :as sg]))

(s/def ::id keyword?)
(s/def :span/start int?)
(s/def :span/end int?)
(s/def :span/ann ::id)
(s/def :ann/profile ::id)
(s/def :ann/concept ::id)
(s/def :ann/doc ::id)
(s/def ::content string?)
(def hex-color-re #"^#[0-9a-fA-F]{3,6}$")
(s/def ::hex-color (s/spec (s/and string? (partial re-matches hex-color-re))
                     :gen #(sg/string-generator hex-color-re)))
(def html-colors #{"IndianRed" "LightCoral" "Salmon" "DarkSalmon" "LightSalmon"
                   "Crimson" "Red" "FireBrick" "DarkRed" "Pink" "LightPink"
                   "HotPink" "DeepPink" "MediumVioletRed" "PaleVioletRed"
                   "Coral" "Tomato" "OrangeRed" "DarkOrange" "Orange"
                   "Gold" "Yellow" "LightYellow" "LemonChiffon" "LightGoldenrodYellow"
                   "PapayaWhip" "Moccasin" "PeachPuff" "PaleGoldenrod" "Khaki"
                   "DarkKhaki" "Lavender" "Thistle" "Plum" "Violet"
                   "Orchid" "Fuchsia" "Magenta" "MediumOrchid" "MediumPurple"
                   "Amethyst" "BlueViolet" "DarkViolet" "DarkOrchid" "DarkMagenta"
                   "Purple" "Indigo" "SlateBlue" "DarkSlateBlue"
                   "GreenYellow" "Chartreuse" "LawnGreen" "Lime" "LimeGreen"
                   "PaleGreen" "LightGreen" "MediumSpringGreen" "SpringGreen" "MediumSeaGreen"
                   "SeaGreen" "ForestGreen" "Green" "DarkGreen" "YellowGreen"
                   "OliveDrab" "Olive" "DarkOliveGreen" "MediumAquamarine" "DarkSeaGreen"
                   "LightSeaGreen" "DarkCyan" "Teal" "Aqua" "Cyan"
                   "LightCyan" "PaleTurquoise" "Aquamarine" "Turquoise" "MediumTurquoise"
                   "DarkTurquoise" "CadetBlue" "SteelBlue" "LightSteelBlue" "PowderBlue"
                   "LightBlue" "SkyBlue" "LightSkyBlue" "DeepSkyBlue" "DodgerBlue"
                   "CornflowerBlue" "MediumSlateBlue" "RoyalBlue" "Blue" "MediumBlue"
                   "DarkBlue" "Navy" "MidnightBlue" "Cornsilk" "BlanchedAlmond"
                   "Bisque" "NavajoWhite" "Wheat" "BurlyWood" "Tan"
                   "RosyBrown" "SandyBrown" "Goldenrod" "DarkGoldenrod" "Peru"
                   "Chocolate" "SaddleBrown" "Sienna" "Brown" "Maroon"
                   "White" "Snow" "Honeydew" "MintCream" "Azure"
                   "AliceBlue" "GhostWhite" "WhiteSmoke" "Seashell" "Beige"
                   "OldLace" "FloralWhite" "Ivory" "AntiqueWhite" "Linen"
                   "LavenderBlush" "MistyRose" "Gainsboro" "LightGrey" "Silver"
                   "DarkGray" "Gray" "DimGray" "LightSlateGray" "SlateGray"
                   "DarkSlateGray" "Black"})

(s/def ::color (s/or :hex ::hex-color :html html-colors :lower-html (set (map str/lower-case html-colors))))

(s/def ::span (s/keys :req-un [::id :span/ann :span/start :span/end]))
(s/def ::ann (s/keys :req-un [::id :ann/doc :ann/profile :ann/concept]))
(s/def ::doc (s/keys :req-un [::id ::content]))
(s/def ::colors (s/map-of :ann/concept ::color))
(s/def ::profile (s/keys :req-un [::id ::colors]))
