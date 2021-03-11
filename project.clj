(defproject knowtator "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.773"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library
                               org.clojure/google-closure-library-third-party]]
                 [thheller/shadow-cljs "2.11.7"]
                 [reagent "0.10.0"]
                 [re-frame "1.1.2"]
                 [day8.re-frame/tracing "0.6.0"]
                 [re-com "2.9.0"]
                 [garden "1.3.10"]
                 [ns-tracker "0.4.0"]
                 [compojure "1.6.2"]
                 [yogthos/config "1.1.7"]
                 [ring "1.8.2"]
                 [re-pressed "0.3.1"]
                 [breaking-point "0.1.2"]

                 [com.velisco/strgen "0.1.8"]
                 [day8.re-frame/undo "0.3.3"]]

  :plugins [[lein-shadow "0.3.1"]
            [lein-garden "0.3.0"]
            [lein-shell "0.5.0"]]

  :min-lein-version "2.9.0"

  :jvm-opts ["-Xmx1G"]

  :source-paths ["src/clj" "src/cljs"]

  :test-paths   ["test/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"
                                    "resources/public/css"]

  :garden {:builds [{:id           "screen"
                     :source-paths ["src/clj"]
                     :stylesheet   knowtator.css/screen
                     :compiler     {:output-to     "resources/public/css/screen.css"
                                    :pretty-print? true}}]}

  :shadow-cljs {:nrepl {:port 8777}

                :builds {:app {:target     :browser
                               :output-dir "resources/public/js/compiled"
                               :asset-path "/js/compiled"
                               :modules    {:app {:init-fn  knowtator.core/init
                                                  :preloads [devtools.preload
                                                             day8.re-frame-10x.preload
                                                             re-frisk.preload]}}
                               :dev        {:compiler-options {:closure-defines {re-frame.trace.trace-enabled?        true
                                                                                 day8.re-frame.tracing.trace-enabled? true}}}
                               :release    {:build-options
                                            {:ns-aliases
                                             {day8.re-frame.tracing day8.re-frame.tracing-stubs}}}

                               :devtools {:http-root    "resources/public"
                                          :http-port    8280
                                          :http-handler knowtator.handler/dev-handler}}
                         :browser-test
                         {:target    :browser-test
                          :ns-regexp "-test$"
                          :runner-ns shadow.test.browser
                          :test-dir  "target/browser-test"
                          :devtools  {:http-root "target/browser-test"
                                      :http-port 8290}}

                         :karma-test
                         {:target    :karma
                          :ns-regexp "-test$"
                          :output-to "target/karma-test.js"}}}

  :shell {:commands {"karma" {:windows         ["cmd" "/c" "karma"]
                              :default-command "karma"}
                     "open"  {:windows ["cmd" "/c" "start"]
                              :macosx  "open"
                              :linux   "xdg-open"}}}

  :aliases {"watch" ["with-profile" "dev" "do"
                     ["shadow" "watch" "app" "browser-test" "karma-test"]]

            "release" ["with-profile" "prod" "do"
                       ["shadow" "release" "app"]]

            "build-report" ["with-profile" "prod" "do"
                            ["shadow" "run" "shadow.cljs.build-report" "app" "target/build-report.html"]
                            ["shell" "open" "target/build-report.html"]]

            "ci"    ["with-profile" "prod" "do"
                     ["shadow" "compile" "karma-test"]
                     ["shell" "karma" "start" "--single-run" "--reporters" "junit,dots"]]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "1.0.2"]
                   [day8.re-frame/re-frame-10x "0.7.0"]
                   [re-frisk "1.3.4"]]
    :source-paths ["dev"]}

   :prod {}

   :uberjar {:source-paths ["env/prod/clj"]
             :omit-source  true
             :main         knowtator.server
             :aot          [knowtator.server]
             :uberjar-name "knowtator.jar"
             :prep-tasks   ["compile" ["release"] ["garden" "once"]]}}

  :prep-tasks [["garden" "once"]])
