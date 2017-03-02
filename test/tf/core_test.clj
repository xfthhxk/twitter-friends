(ns tf.core-test
  (:require [e85th.test.http :as http]
            [e85th.commons.util :as u]
            [tf.routes :as routes]
            [com.stuartsierra.component :as component]
            [tf.test-system :as test-system]
            [schema.core :as s]
            [clojure.test :refer :all]
            [taoensso.timbre :as log]))

(def config-file "config.edn")
(def admin-user nil)

(defonce system nil)

(defn init!
  "Call this first before using any other functions."
  []
  (u/set-utc-tz)
  (s/set-fn-validation! true)
  (alter-var-root #'system (constantly (component/start (test-system/make {}))))
  (http/init! {:routes (routes/make-handler (:app system))}))


(def api-call (http/make-transit-api-caller))

(defn with-system
  "Runs tests using the test system."
  [f]
  (when (not system)
    (println "Starting test system")
    (init!))
  (f))


(use-fixtures :once with-system)

(comment
  (init!) )
