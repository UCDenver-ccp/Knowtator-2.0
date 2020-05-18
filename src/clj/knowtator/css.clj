(ns knowtator.css
  (:require [garden.def :refer [defstyles]]
            [garden.selectors :as sel]
            [garden.stylesheet  :as s]
            [clojure.string :as str]))

(defstyles screen
  [:body {:color "red"}]
  [:.level1 {:color "green"}])

(defstyles screen
  (sel/defselector *)
  (sel/defpseudoelement before)
  (sel/defpseudoelement after)
  (s/at-import "https://fonts.googleapis.com/css?family=Open+Sans")
  [* (* before) (* after) {:box-sizing :border-box}]
  [:body {:margin           "30px"
          :background-color "#f0f0f0"}]
  [:.container {:margin-bottom "-25px !important"}]
  [:.container :.backdrop :textarea :.foredrop {:width  "100%"
                                                :height "1000px"}]
  [:.highlights :textarea {:padding        "10px"
                           :font           [(str/join " " ["20px/28px" "'Open Sans'"]) :sans-serif]
                           :letter-spacing "1px"}]

  [:.container {:display                  :block
                :margin                   (str/join " " [0 "auto"])
                :transform                "translateZ(0)"
                :-webkit-text-size-adjust :none}]

  [:.backdrop {:position         :absolute
               :z-index          -1
               :border           (str/join " " ["2px" "solid" "#685972"])
               :background-color "#fff"
               :overflow         :visible
               :pointer-events   :none
               :transition       [:transform "1s"]}]

  [:.foredrop {:position         :absolute
               :z-index          1
               :border           (str/join " " ["2px" "solid" "#685972"])
               :background-color :transparent
               :overflow         :visible
               :pointer-events   :none
               :transition       [:transform "1s"]}]

  [:.highlights {:white-space :pre-wrap
                 :word-wrap   :break-word
                 :color       :transparent}]

  [:textarea {:display          :block
              :position         :absolute
              :bottom           0
              :left             0
              :font             [(str/join " " ["20px/28px" "'Open Sans'"]) :sans-serif]
              :z-index          0
              :margin           0
              :border           (str/join " " ["2px" "solid" "#74637f"])
              :border-radius    0
              :color            "#444"
              :background-color :transparent
              :overflow         :hidden
              :resize           :none
              :transition       [:transform "1s"]}]

  [:mark {:border-radius    "3px"
          :color            :transparent
          :background-color "#b1d5e5"}]

  [:button {:display          :block
            :width            "300px"
            :margin           ["30px" :auto 0]
            :padding          "10px"
            :border           :none
            :border-radius    "6px"
            :color            "#fff"
            :background-color "#74637f"
            :font             ["18px" "'Opens Sans'", :sans-serif]
            :letter-spacing   "1px"
            :appearance       :none
            :cursor           :pointer}]

  [:.perspective :.backdrop {:transform ["perspective(1500px)"
                                         "translateX(-125px)"
                                         "rotateY(45deg)"
                                         "scale(0.9)"]}]
  [:.perspective :.foredrop {:transform ["perspective(1500px)"
                                         "translateX(-125px)"
                                         "rotateY(45deg)"
                                         "scale(0.9)"]}]
  [:.perspective :textarea {:transform ["perspective (1500px)"
                                        "translateX(155px)"
                                        "rotateY(45deg)"
                                        "scale(1.1)"]}]

  [:textarea:focus, :button:focus {:outline    :none
                                   :box-shadow [0 0 0 "2px" "#c6aada"]}]
  [:.popup {:position            :relative
            :display             :inline-block
            :cursor              :pointer
            :-webkit-user-select :none
            ;; :-moz-user-select :none
            #_:user-select       #_:none}
   [:.popuptext {:visibility       :hidden
                 :width            "160px"
                 :background-color "#555"
                 :color            "#fff"
                 :text-align       :center
                 :border-radius    "6px"
                 :padding          "8px 0"
                 :position         :absolute
                 :z-index          1
                 :bottom           "125%"
                 :left             "50%"
                 :margin-left      "-80px"}
    [(sel/& sel/after) {:content      "\"\""
                        :position     :absolute
                        :top          "100%"
                        :left         "50%"
                        :margin-left  "-5px"
                        :border-width "5px"
                        :border-style :solid
                        :border-color "#555 transparent transparent transparent"}]]
   [:.show {:visibility         :visible
            :-web-kit-animation "fadeIn 1s"
            :animation          "fadeIn 1s"}]])
