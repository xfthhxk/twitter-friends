(defproject xfthhxk/twitter-friends "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.473"]
                 [org.clojure/tools.cli "0.3.5"]
                 [com.taoensso/timbre "4.7.4"]
                 [e85th/commons "0.1.7"]
                 [e85th/backend "0.1.17" :exclusions [[com.google.guava/guava-jdk5]]]
                 [e85th/ui "0.1.15"]
                 [prismatic/schema "1.1.2"]
                 [twitter-api "1.8.0"]

                 [http-kit "2.2.0"]

                 [metosin/compojure-api "1.1.4"]
                 [metosin/ring-http-response "0.8.0"]

                 [enlive "1.1.6"] ; html transforms
                 [org.jsoup/jsoup "1.8.3"] ; for use with enlive
                 [re-frame "0.8.0"]
                 [day8.re-frame/http-fx "0.0.4"]
                 [reagent "0.6.0"]
                 [cljsjs/react-dom "15.3.1-0"]

                 [funcool/hodgepodge "0.1.4"] ;; local storage
                 [kioo "0.4.2"]]


  :source-paths ["src"]

  ;; quell multiple binding messages from java logging frameworks
  :exclusions [ch.qos.logback/logback-classic
               ch.qos.logback/logback-core]

  :main tf.main
  :aot :all

  :version-script "git describe --tags || date | md5 || date | md5sum"

  :test-selectors {:default (complement :integration)
                   :integration :integration
                   :all (constantly true)}

  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :tf-ui :compiler :output-dir]
                                    [:cljsbuild :builds :tf-ui :compiler :output-to]]

  :cljsbuild {:builds {:tf-ui {:source-paths ["src-ui" "checkouts/ui/src/cljs"]
                                :figwheel true
                                :compiler {:main tf.main
                                           :output-to "resources/public/js/tf-ui.js"
                                           :output-dir "resources/public/js/out/tf-ui"
                                           :asset-path "/js/out/tf-ui"
                                           :optimizations :none
                                           :pretty-print true
                                           :parallel-build true
                                           :closure-defines {goog.DEBUG true}}}}}
  :figwheel {:css-dirs ["resources/public/css"]}
  :plugins [[com.jakemccrary/lein-test-refresh "0.10.0"]
            [codox "0.8.13"]
            [lein-cljsbuild "1.1.4"]
            [lein-version-script "0.1.0"]
            [test2junit "1.1.2"]]

  :profiles {:dev  [:project/dev  :profiles/dev]
             :test [:project/test :profiles/test]
             :uberjar {:aot :all
                       :hooks [leiningen.cljsbuild]
                       :cljsbuild {:jar true
                                   :builds {:tf-ui {:compiler {:optimizations :advanced
                                                                :closure-defines {goog.DEBUG false}
                                                                :pretty-print false}}}}}
             :profiles/dev  {}
             :profiles/test {}
             :project/dev   {:dependencies [[reloaded.repl "0.2.2"]
                                            [org.clojure/tools.namespace "0.2.11"]
                                            [org.clojure/tools.nrepl "0.2.12"]
                                            [re-frisk "0.3.2"]
                                            [e85th/test "0.1.2"]
                                            [expectations "2.2.0-alpha1"]]
                             :source-paths   ["dev/src"]
                             :resource-paths ["dev/resources"]
                             :plugins [[lein-figwheel "0.5.7"]]
                             :repl-options {:init-ns user}
                             :env {:port "7000"}}
             :project/test  {}}

  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]

  :codox {:sources ["src/clj"]
          :defaults {:doc "FIXME: write docs"}
          :src-dir-uri "http://github.com/xfthhxk/twitter-friends/blob/master/"
          :src-linenum-anchor-prefix "L"})
