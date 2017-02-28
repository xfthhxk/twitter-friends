(ns tf.subs
  (:require [re-frame.core :as rf]
            [e85th.ui.rf.sweet :refer-macros [def-sub-db def-sub]]
            [taoensso.timbre :as log]
            [tf.data-keys :as dk]))

(def-sub-db main-view dk/main-view)
(def-sub-db friend-list dk/friend-list)
(def-sub-db current-handle dk/current-handle)
(def-sub-db busy? dk/busy?)
