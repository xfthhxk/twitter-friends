(ns tf.common.data
  "Data with local-storage as backing store."
  (:require [schema.core :as s]
            [clojure.string :as string]
            [hodgepodge.core :as hp :refer [local-storage]]))

(s/defn api-host :- (s/maybe s/Str)
  []
  (:api-host local-storage))

(s/defn set-api-host!
  [api-host :- s/Str]
  (assoc! local-storage :api-host api-host))

(s/defn api-host-set? :- s/Bool
  []
  (some? (api-host)))
