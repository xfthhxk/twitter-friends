(ns tf.models
  (:require [schema.core :as s]))

(s/defschema Friend
  {:twitter.user/handle s/Str
   :twitter.user/photo-url s/Str
   :twitter.user/friend-score s/Num})

(s/defschema FriendInfo
  {:user Friend
   :tags #{s/Str}})


(s/defschema HandleToFriendInfo
  {s/Str FriendInfo})
