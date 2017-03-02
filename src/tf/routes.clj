(ns tf.routes
  (:require [compojure.api.sweet :refer [defapi defroutes context GET POST ANY]]
            [compojure.api.sweet :as compojure-api]
            [ring.util.http-response :as http-response]
            [ring.middleware.params :as ring-params]
            [ring.middleware.cookies :as ring-cookies]
            [compojure.route :as route]
            [e85th.backend.web :as web]
            [e85th.backend.middleware :as backend-mw]
            [tf.ui :as ui]
            [tf.core :as tf]
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
             :data {:info {:title "Twitter Friends APIs"}}}}

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
