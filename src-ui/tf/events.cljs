(ns tf.events
  (:require [taoensso.timbre :as log]
            [schema.core :as s]
            [tf.net.api :as api]
            [tf.data-keys :as dk]
            [e85th.ui.rf.sweet :refer-macros [def-event-db def-event-fx def-db-change]]
            [e85th.ui.util :as u]
            [re-frame.core :as rf]))

(def-db-change set-main-view dk/main-view)
(def-db-change current-handle-changed dk/current-handle)

(def-event-db autocomplete
  [db [_ x]]
  (log/info x)
  db)

(def-event-db rpc-err
  [db [_ err]]
  (log/warn err)
  db)

(def-event-db fetch-friends-ok
  [db [_ friends]]
  (-> db
      (assoc-in dk/friend-list friends)
      (assoc-in dk/busy? false)))

(def-event-fx fetch-friends
  [{:keys [db]} _]
  (let [handle (get-in db dk/current-handle)]
    (log/infof "fetch-friends for handle: %s" handle)
    {:db (assoc-in db dk/busy? true)
     :http-xhrio (api/fetch-friends handle fetch-friends-ok [rpc-err ::fetch-friends-err])}))
