(ns tf.net.api
  (:require [e85th.ui.net.rpc :as rpc]
            [taoensso.timbre :as log]
            [tf.common.data :as data]
            [re-frame.core :as rf]
            [schema.core :as s]
            [e85th.ui.util :as u]))

(s/defn ^:private full-url
  [url-path]
  (str (data/api-host) "/api" url-path))

(defn suggest-url
  []
  (full-url "/v1/search/suggest"))

(s/defn new-request
  ([method url ok err]
   (new-request method url {} ok err))
  ([method url params ok err]
   (-> (rpc/new-re-frame-request method (full-url url) params ok err)
       rpc/with-edn-format
       (rpc/with-bearer-auth (data/tf-token)))))

(s/defn call!
  "For testing really. Use effects to actually make calls.
   re-frame handling. ok and err can be either keywords or a vector.
   If vector then the first should be a keyword to conform to re-frame dispatch
   semantics."
  ([method url params ok err]
   (call! (new-request method url params ok err)))
  ([req]
   (let [ensure-handler-fn (fn [{:keys [handler error-handler] :as r}]
                             (cond-> r
                               (vector? handler) (assoc :handler #(rf/dispatch (conj handler %)))
                               (vector? error-handler) (assoc :error-handler #(rf/dispatch (conj error-handler %)))))
         normalize (comp ensure-handler-fn)]
     (rpc/call (normalize req)))))

(s/defn fetch-friends
  "Creates a http request to fetch friends for the specified handle.
   ok and err are re-frame events for response ok and response error respectively."
  [handle :- s/Str ok err]
  (new-request :get "/v1/friends" {:handle handle} ok err))
