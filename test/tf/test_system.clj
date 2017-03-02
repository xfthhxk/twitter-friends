(ns tf.test-system
  (:require [com.stuartsierra.component :as component]
            [e85th.commons.util :as u]
            [e85th.commons.components :as commons-comp]
            [schema.core :as s]
            [taoensso.timbre :as log]))

(s/defn add-server-components
  "Adds server components "
  [sys-config component-vector]
  (conj component-vector
        ;; app will have all dependent resources
        :app (-> component-vector commons-comp/component-keys commons-comp/new-app)))

(s/defn all-components
  "Answers with a seq of alternating keywords and components required for component/system-map.
   Starts with a base set of components and adds in server components."
  [sys-config]
  (let [base [:sys-config sys-config]]
    (add-server-components sys-config base)))

(s/defn make
  "Creates a system."
  [sys-config]
  (apply component/system-map (all-components sys-config)))
