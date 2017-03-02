(ns tf.main
  (:require [clojure.tools.cli :as cli]
            [tf.common.util :as util]
            [e85th.commons.util :as u]
            [tf.system :as system]
            [tf.core :as tf]
            [com.stuartsierra.component :as component]
            [schema.core :as s]
            [clojure.string :as str]
            [clojure.pprint :as pprint]
            [taoensso.timbre :as log])
  (:gen-class))

(def allowed-modes #{:cli :server})

(defonce ^{:doc "This only exists to get a reference to the running system for remote debugging"}
  system nil)

(def cli-options
  [[nil "--mode MODE" "Mode to run in. One off: cli or server"
    :parse-fn keyword
    :validate [allowed-modes (format "Unknown mode. Possible values: %s" (str/join allowed-modes))]]
   [nil "--handle Twitter Handle" "Twitter Handle for subject."]
   [nil "--api-key Twitter API key" "Twitter API key"]
   [nil "--api-secret Twitter API secret" "Twitter API secret"]
   [nil "--env ENV" "Run with ENV. However theres is only 1 env."
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
        "       tf --mode cli"
        ""
        "Options:"
        options-summary
        ""]
       (str/join \newline)))

(s/defn make-system
  ([]
   (make-system :development :server ""))
  ([env-name operation-mode log-suffix]
   (make-system env-name operation-mode log-suffix {}))
  ([env-name :- s/Keyword operation-mode :- s/Keyword log-suffix :- (s/maybe s/Str) options-map]
   (u/set-utc-tz)

   (printf "Reading configuration with %s profile\n" env-name)

   (let [sys-config {} #_(conf/read-config env-name)]
     ;(u/init-logging (-> sys-config conf/log-file (u/log-file-with-suffix log-suffix)))
     (log/info (util/build-properties-with-header))
     (log/infof "Environment: %s" env-name)
     (log/warn "Turning schema validation on globally.")
     (s/set-fn-validation! true) ;; globally turn on all validations

     (system/new-system sys-config operation-mode options-map))))

(s/defn run-cli
  "Prints a table to stdout of the handle, score and twitter page rather than the profile image.
   Profile image url is unwieldly."
  [system handle :- s/Str]
  (let [format-score #(format "%.2f" (float %))]
    (pprint/print-table [:twitter.user/handle :twitter.user/friend-score :twitter.user/twitter-page]
                        (->> (tf/find-friends system handle)
                             (map #(update-in % [:twitter.user/friend-score] format-score))))))

(defn -main
  [& args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)
        {:keys [mode handle env] :or {mode :server env :development}} options]

    (cond
      (:help options) (u/exit 0 (usage summary))
      (:version options) (u/exit 0 (util/build-properties))
      (not (:api-key options)) (u/exit 1 "API key is required.")
      (not (:api-secret options)) (u/exit 1 "API secret is required.")
      errors (u/exit 1 (str "Errors parsing command:\n" (str/join \newline errors))))

    (log/infof "Twitter Friends started with options: %s." options)
    (try
      (let [sys (component/start (make-system env mode "" options))]
        (u/add-shutdown-hook (partial component/stop sys))
        (alter-var-root #'system (constantly sys))
        (when (= :cli mode)
          (when-not (seq handle)
            (u/exit 1 "Please specify a handle."))
          (run-cli sys handle)
          (u/exit 0 "")))
      (catch Exception ex
        (u/exit 1 (str "\nTwitter Friends Error: " ex))))))
