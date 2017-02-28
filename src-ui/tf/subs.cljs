(ns tf.subs
  "Subscriptions for the app."
  (:require [e85th.ui.rf.sweet :refer-macros [def-sub-db]]
            [tf.db-keys :as dk]))

(def-sub-db friend-list dk/friend-list)
(def-sub-db current-handle dk/current-handle)
(def-sub-db busy? dk/busy?)
