(ns tf.tweet
  (:require [tf.models :as m]
            [tf.text :as text]
            [twitter.api.restful :as twitter]
            [schema.core :as s]
            [e85th.commons.util :as u]
            [clojure.string :as str]))


(def word-frequencies
  (comp text/word-frequencies :text))

(def term-frequencies
  (comp text/term-frequencies :text))

(def user-handle
  (comp :screen_name :user))

(def user-description
  (comp :description :user))

(s/defn user-info
  [tweet]
  (let [{:keys [profile_image_url_https]} (get-in tweet [:user])
        handle (user-handle tweet)]
    {:twitter.user/handle handle
     :twitter.user/photo-url profile_image_url_https
     :twitter.user/twitter-page (str "https://twitter.com/" handle)}))



(s/defn hashtags :- [s/Str]
  [tweet]
  (->> (get-in tweet [:entities :hashtags])
       (map :text)))


(s/defn hashtag-frequencies :- {s/Str s/Int}
  [tweets]
  (->> (map hashtags tweets)
       flatten
       frequencies))

(s/defn word-frequencies-by-user-handle :- {s/Str {s/Str s/Int}}
  "Returns word frequencies by users screen name across all
   of the users messages in tweets."
  [tweets]
  (u/group-by+ user-handle word-frequencies (partial apply merge-with +) tweets))

(s/defn combined-term-frequencies :- {s/Str s/Num}
  [tweets]
  (apply merge-with + (map term-frequencies tweets)))

(s/defn top-hashtags
  "From a set of tweets extracts and returns the most popular hashtags in order."
  [tweets]
  (->> (hashtag-frequencies tweets)
       (sort-by (comp unchecked-negate-int second))
       (map first)))
