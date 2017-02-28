(ns tf.net.api
  "API interaction layer to be used with re-frame and http-xhrio co-effect."
  (:require [e85th.ui.net.rpc :as rpc]
            [tf.common.data :as data]
            [schema.core :as s]))

(s/defn ^:private full-url
  "Generates a full url for an endpoint given url-path suffix."
  [url-path]
  (str (data/api-host) "/api" url-path))

(s/defn ^:private new-request
  "Creates a new request which is compatible with re-frame and http-xhrio co-effect."
  ([method url ok err]
   (new-request method url {} ok err))
  ([method url params ok err]
   (-> (rpc/new-re-frame-request method (full-url url) params ok err)
       rpc/with-edn-format)))

(s/defn fetch-friends
  "Creates a http request to fetch friends for the specified handle.
   ok and err are re-frame events for response ok and response error respectively."
  [handle :- s/Str ok err]
  (new-request :get "/v1/friends" {:handle handle} ok err))
