(ns tf.events
  "re-frame event handlers. These functions return data, it is actually re-frame that
   forces the side-effects to occur."
  (:require [taoensso.timbre :as log]
            [tf.net.api :as api]
            [tf.db-keys :as dk]
            [e85th.ui.rf.sweet :refer-macros [def-event-db def-event-fx def-db-change]]))

(def-db-change current-handle-changed dk/current-handle)

;; NB. :notify fx uses toastr.js to display an error falling back to js/alert.
(def-event-fx fetch-friends-err
  [{:keys [db] :as cofx} event-v]
  (log/warnf "rpc err: %s" event-v)
  {:db (assoc-in db dk/busy? false)
   :notify [:alert {:message "Error fetching friends. Is the handle correct?"}]})

(def-event-db fetch-friends-ok
  [db [_ friends]]
  (-> db
      (assoc-in dk/friend-list friends)
      (assoc-in dk/busy? false)))

(def-event-fx fetch-friends
  [{:keys [db]} _]
  (let [handle (get-in db dk/current-handle)]
    (log/debugf "fetch-friends for handle: %s" handle)
    {:db (assoc-in db dk/busy? true)
     :http-xhrio (api/fetch-friends handle fetch-friends-ok fetch-friends-err)}))
