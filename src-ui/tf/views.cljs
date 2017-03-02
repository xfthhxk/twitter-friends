(ns tf.views
  "Views for the app."
  (:require [re-frame.core :as rf]
            [tf.subs :as subs]
            [tf.events :as e]
            [e85th.ui.rf.inputs :as inputs]
            [goog.string :as gstr]
            [kioo.reagent :as k :refer-macros [defsnippet deftemplate]]))


(defsnippet main-section "templates/ui/main.html" [:#tf-all]
  [view]
  {[:main] (k/content view)})

(defsnippet friend-item* "templates/ui/friends.html" [:.friend-list [:.friend-item first-child]]
  [{:keys [twitter.user/handle twitter.user/photo-url twitter.user/friend-score :twitter.user/twitter-page]}]
  {[:.friend-item] (k/set-attr :key handle)
   [:.friend-twitter-page] (k/set-attr :href twitter-page)
   [:.friend-handle] (k/content handle)
   [:.friend-profile-img] (k/set-attr :src photo-url)
   [:.friend-score] (k/content (gstr/format "%.2f" friend-score))})


(defsnippet friend-list* "templates/ui/friends.html" [:.friend-list]
  [friends]
  {[:.friend-list-items] (k/content (map friend-item* friends))})

(defn friend-list
  []
  [friend-list* @(rf/subscribe [subs/friend-list])])

(defsnippet friend-controls* "templates/ui/friends.html" [:.friend-controls]
  []
  {[:.twitter-handle] (k/substitute [inputs/std-text subs/current-handle e/current-handle-changed])
   [:.search-action] (k/substitute [inputs/button subs/busy? e/fetch-friends "Find Friends"])})

(defsnippet friends-main "templates/ui/friends.html" [:.friends-main]
  []
  {[:.friend-controls] (k/substitute [friend-controls*])
   [:.friend-list] (k/substitute [friend-list])})

(defn main-panel
  []
  [main-section [friends-main]])
