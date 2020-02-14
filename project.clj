(defproject knowtator-clj "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library]]
                 [thheller/shadow-cljs "2.8.69"]
                 [reagent "0.8.1"]
                 [re-frame "0.10.9"]
                 [garden "1.3.9"]
                 [ns-tracker "0.4.0"]
                 [compojure "1.6.1"]
                 [yogthos/config "1.1.6"]
                 [ring "1.7.1"]

                 ;My clj deps
                 [com.cognitect/transit-clj "0.8.313"]
                 [ring-transit "0.1.5"]

                 ;My cljs deps
                 [day8.re-frame/http-fx "0.1.6"]
                 [cljsjs/d3 "5.12.0-0"]
                 [rid3 "0.2.1"]
                 [district0x/re-frame-window-fx "1.0.2"]
                 [cljsjs/mousetrap "1.6.3-0"]]

  :plugins [[lein-garden "0.3.0"]
            [lein-shell "0.5.0"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "resources/public/css"]

  :garden {:builds [{:id           "screen"
                     :source-paths ["src/clj"]
                     :stylesheet   knowtator-clj.css/screen
                     :compiler     {:output-to     "resources/public/css/screen.css"
                                    :pretty-print? true}}]}

  :shell {:commands {"open" {:windows ["cmd" "/c" "start"]
                             :macosx  "open"
                             :linux   "xdg-open"}}}

  :aliases {"dev"          ["with-profile" "dev" "do"
                            ["clean"]
                            ["run" "-m" "shadow.cljs.devtools.cli" "watch" "app"]]
            "prod"         ["with-profile" "prod" "do"
                            ["clean"]
                            ["run" "-m" "shadow.cljs.devtools.cli" "release" "app"]]
            "build-report" ["with-profile" "prod" "do"
                            ["clean"]
                            ["run" "-m" "shadow.cljs.devtools.cli" "run" "shadow.cljs.build-report" "app" "target/build-report.html"]
                            ["shell" "open" "target/build-report.html"]]
            "karma"        ["with-profile" "prod" "do"
                            ["clean"]
                            ["run" "-m" "shadow.cljs.devtools.cli" "compile" "karma-test"]
                            ["shell" "karma" "start" "--single-run" "--reporters" "junit,dots"]]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.10"]
                   [day8.re-frame/re-frame-10x "0.4.5"]
                   [day8.re-frame/tracing "0.5.3"]]}

   :prod {:dependencies [[day8.re-frame/tracing-stubs "0.5.3"]]}

   :uberjar {:source-paths ["env/prod/clj"]
             :dependencies [[day8.re-frame/tracing-stubs "0.5.3"]]
             :omit-source  true
             :main         knowtator-clj.server
             :aot          [knowtator-clj.server]
             :uberjar-name "knowtator-clj.jar"
             :prep-tasks   ["compile" ["prod"] ["garden" "once"]]}})
