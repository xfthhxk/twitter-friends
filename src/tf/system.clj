(ns tf.system
  (:require [com.stuartsierra.component :as component]
            [e85th.backend.components :as backend-comp]
            [e85th.commons.components :as commons-comp]
            [twitter.oauth :as oauth]
            [schema.core :as s]
            [tf.common.util :as util]
            [tf.routes :as routes]))



(s/defn add-server-components
  "Adds server components "
  [sys-config component-vector]
  (conj component-vector
        ;; app will have all dependent resources
        :app (-> component-vector commons-comp/component-keys commons-comp/new-app)
        :http (backend-comp/new-http-kit-server {:port 9001} routes/make-handler)
        :repl (backend-comp/new-repl-server {:port 9000})))

(s/defn all-components
  "Answers with a seq of alternating keywords and components required for component/system-map.
   Starts with a base set of components and adds in other components based on the operation mode."
  [sys-config operation-mode :- s/Keyword options-map]
  (let [base [:sys-config sys-config
              :twitter-creds (oauth/make-oauth-creds (:api-key options-map) (:api-secret options-map))]
        f (get {:server add-server-components} operation-mode (constantly base))]
    (f sys-config base)))

(s/defn new-system
  "Creates a system."
  [sys-config operation-mode :- s/Keyword options-map]
  (apply component/system-map (all-components sys-config operation-mode options-map)))
