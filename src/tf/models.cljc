(ns tf.models
  (:require [schema.core :as s]))

(s/defschema Friend
  {:twitter.user/handle s/Str
   :twitter.user/photo-url s/Str
   :twitter.user/twitter-page s/Str
   :twitter.user/friend-score s/Num})
