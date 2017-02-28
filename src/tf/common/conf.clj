(ns tf.common.conf
  (:require [schema.core :as s]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [e85th.commons.util :as u]
            [aero.core :as aero]))

(def config-file "config.edn")


(def env-name->profile
  {:production :prd
   :staging :stg
   :test :tst
   :development :dev})

(s/defn read-config
  "Reads a config file from the classpath."
  ([env-name :- s/Keyword]
   (read-config config-file env-name))
  ([file :- s/Str env-name :- s/Keyword]
   (let [profile (env-name->profile env-name)]
     (assert profile)
     (-> (aero/read-config (io/resource file) {:profile profile})
         (dissoc :envs :secrets)
         (assoc :env (name env-name))))))


(s/defn log-file :- s/Str
  [sys-config]
  (get-in sys-config [:log-file]))

(s/defn twitter-api-key
  [sys-config]
  (get-in sys-config [:twitter-creds :api-key]))

(s/defn twitter-api-secret
  [sys-config]
  (get-in sys-config [:twitter-creds :api-secret]))
