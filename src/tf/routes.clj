(ns tf.routes
  (:require [compojure.api.sweet :refer [defapi defroutes context GET POST ANY]]
            [compojure.api.sweet :as compojure-api]
            [ring.util.http-response :as http-response]
            [tf.common.util :as util]
            [tf.websockets :as websockets]
            [ring.middleware.params :as ring-params]
            [ring.middleware.cookies :as ring-cookies]
            [compojure.route :as route]
            [e85th.backend.web :as web]
            [e85th.commons.token :as token]
            [e85th.commons.util :as u]
            [e85th.backend.middleware :as backend-mw]
            [buddy.auth.middleware :as buddy-auth-mw]
            [tf.ui :as ui]
            [tf.core :as tf]
            [tf.common.conf :as conf]
            [taoensso.timbre :as log]
            [schema.core :as s]))

(defn api-exception-handler
  [f]
  (backend-mw/wrap-api-exception-handling
   f
   (constantly nil)))

(defroutes ui-routes
  (context "" [] :tags ["ui"]
    :components [res]
    (GET "/" []
         ( http-response/see-other "/tf"))

    (GET "/tf*" []
         (web/html-response (ui/index-page res)))))

(defroutes api-routes
  (context "/api/v1" [] :tags ["friends"]
    :components [res]
    (GET "/friends" []
      :summary "For a given twitter handle get simliar users"
      :query-params [handle :- s/Str]
      ;:return [m/Friend]
      (http-response/ok (tf/find-friends res handle)))))

(defapi all-routes
  {:coercion (constantly backend-mw/coercion-matchers)
   :exceptions {:handlers {:compojure.api.exception/default backend-mw/error-actions}}
   :swagger {:ui "/swagger"
             :spec "/swagger.json"
             :data {:info {:title "TF APIs"}}}}

  ;; using var quote #' to facilitate changing route definitions during development
  (compojure-api/middleware
   [api-exception-handler]
   #'api-routes)

  (compojure-api/undocumented
   #'ui-routes
   (route/files "/" )
   (route/resources "/")

   ;; 404 route has to be last
   (fn [req]
     (http-response/not-found {:error "Unknown twitter-friends resource."}))))

(defn make-handler
  [app-resources]
  (-> all-routes
      (compojure.api.middleware/wrap-components {:res app-resources})
      backend-mw/wrap-api-key-in-header
      backend-mw/wrap-cors
      ring-params/wrap-params
      ring-cookies/wrap-cookies
      backend-mw/wrap-swagger-remove-content-length))
