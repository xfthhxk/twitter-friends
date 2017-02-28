(ns tf.views
  (:require [re-frame.core :as rf]
            [tf.subs :as subs]
            [tf.events :as e]
            [tf.routes :as routes]
            [e85th.ui.rf.inputs :as inputs]
            [taoensso.timbre :as log]
            [tf.common.data :as data]
            [tf.net.api :as api]
            [e85th.ui.net.rpc :as rpc]
            [kioo.reagent :as k :refer-macros [defsnippet deftemplate]]))


(defsnippet main-section "templates/ui/main.html" [:#tf-all]
  [view]
  {[:main] (k/content view)})


(defn welcome-page
  []
  [:div
   [:h1 "Hello TF!"]])

(defsnippet friend-item* "templates/ui/friends.html" [:.friend-list [:.friend-item first-child]]
  [{:keys [twitter.user/handle twitter.user/photo-url twitter.user/friend-score]}]
  {[:.friend-item] (k/set-attr :key handle)
   [:.friend-handle] (k/content handle)
   [:.friend-profile-img] (k/set-attr :src photo-url)
   [:.friend-score] (k/content friend-score)})

(defsnippet friend-list* "templates/ui/friends.html" [:.friend-list]
  [friends]
  {[:.friend-list-items] (k/content (map friend-item* friends))})

(defn friend-list
  []
  (let [friends (rf/subscribe [subs/friend-list])]
    (fn []
      [friend-list* @friends])))

(defsnippet friend-controls* "templates/ui/friends.html" [:.friend-controls]
  []
  {[:.twitter-handle] (k/substitute [inputs/std-text subs/current-handle e/current-handle-changed])
   [:.search-action] (k/substitute [inputs/button subs/busy? e/fetch-friends "Find Friends"])})

(defsnippet friends-main "templates/ui/friends.html" [:.friends-main]
  []
  {[:.friend-controls] (k/substitute [friend-controls*])
   [:.friend-list] (k/substitute [friend-list])})


(def main-panel friends-main)
