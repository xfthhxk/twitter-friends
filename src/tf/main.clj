(ns tf.main
  (:require [clojure.tools.cli :as cli]
            [tf.common.conf :as conf]
            [tf.common.util :as util]
            [e85th.commons.util :as u]
            [tf.system :as system]
            [com.stuartsierra.component :as component]
            [schema.core :as s]
            [clojure.string :as str]
            [taoensso.timbre :as log])
  (:gen-class))

(def allowed-modes #{:standalone})

(defonce ^{:doc "This only exists to get a reference to the running system for remote debugging"}
  system nil)

(def cli-options
  [[nil "--mode MODE" "Mode to run in. One off: standalone"
    :parse-fn keyword
    :validate [allowed-modes (format "Unknown mode. Possible values: %s" (str/join allowed-modes))]]
   [nil "--env ENV" "Run with ENV."
    :parse-fn u/normalize-env
    :validate [u/known-env? (format "Unknown env. Possible values: %s" (str/join ", " (u/known-envs true)))]]
   ["-h" "--help"]
   ["-v" "--version" "Echo to stdout the current version."]])


(defn usage
  "Generates the programs usage string"
  [options-summary]
  (->> ["tf"
        ""
        "Usage: "
        "       tf --mode standalone"
        ""
        "Options:"
        options-summary
        ""]
       (str/join \newline)))

(s/defn make-system
  ([]
   (make-system :development :standalone ""))

  ([env-name :- s/Keyword operation-mode :- s/Keyword log-suffix :- (s/maybe s/Str)]
   (u/set-utc-tz)

   (printf "Reading configuration with %s profile\n" env-name)

   (let [sys-config (conf/read-config env-name)]
     (u/init-logging (-> sys-config conf/log-file (u/log-file-with-suffix log-suffix)))
     (log/info (util/build-properties-with-header))
     (log/infof "Environment: %s" env-name)
     (log/warn "Turning schema validation on globally.")
     (s/set-fn-validation! true) ;; globally turn on all validations

     (system/new-system sys-config operation-mode))))

(defn run-standalone
  [system]
  (log/infof "TF is running in standalone mode."))

(defn -main
  [& args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)
        {:keys [mode env]} options
        [operation-mode log-suffix] (case mode
                                      :standalone [:standalone ""])]

    (cond
      (:help options) (u/exit 0 (usage summary))
      (:version options) (u/exit 0 (util/build-properties))
      errors (u/exit 1 (str "Errors parsing command:\n" (str/join \newline errors))))

    (log/infof "tf started with options: %s." options)
    (let [sys (component/start (make-system env))]
      (u/add-shutdown-hook (partial component/stop sys))
      (reset! system sys)
      (run-standalone sys))))
