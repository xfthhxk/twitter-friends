(ns tf.routes
  "Defines routes and helper functions. This namespace needs to be included during app startup
   so that pushy can be initialized."
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :as rf]
            [taoensso.timbre :as log]
            [tf.events :as e]
            [clojure.string :as str]))

(def routes
  ["/tf/" {"" :tf/home
           [:handle "/"] :tf/friends}])


(def parse-url* (partial bidi/match-route routes))

(defn- parse-url
  [url]
  ;(log/debugf "input url: %s" url)
  (or (parse-url* url)
      (parse-url* (str url "/"))
      (parse-url* (str/replace url #"/$" ""))))

(defn- dispatch-route
  [matched-route]
  (rf/dispatch [e/set-main-view matched-route]))

(def url-for (partial bidi/path-for routes))


(defn init!
  "Putting in function to avoid multiple dispatches during dev when code is reloaded"
  []
  (pushy/start! (pushy/pushy #'dispatch-route #'parse-url)))
