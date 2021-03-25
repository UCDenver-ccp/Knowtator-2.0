(defproject knowtator "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [garden "1.3.10"]]

  :plugins [[lein-garden "0.3.0"]]
  :min-lein-version "2.9.0"
  :garden {:builds [{:id           "text-annotation-editor"
                     :source-paths ["src/clj"]
                     :stylesheet   knowtator.css/text-annotation-editor
                     :compiler     {:output-to     "resources/public/css/text-annotation-editor.css"
                                    :pretty-print? true}}
                    {:id           "knowtator"
                     :source-paths ["src/clj"]
                     :stylesheet   knowtator.css/knowtator
                     :compiler     {:output-to     "resources/public/css/knowtator.css"
                                    :pretty-print? true}}
                    {:id           "datatable"
                     :source-paths ["src/clj"]
                     :stylesheet   knowtator.css/datatable
                     :compiler     {:output-to     "resources/public/css/datatable.css"
                                    :pretty-print? true}}]})
