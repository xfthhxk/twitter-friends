(ns tf.tweet
  "Deals with parsing a tweet or a seq of tweets obtained from the Twitter API."
  (:require [tf.models :as m]
            [tf.text :as text]
            [schema.core :as s]
            [e85th.commons.util :as u]
            [clojure.string :as str]))

(def ^{:doc "Gets the tweet text"} text :text)

(def ^{:doc "Word frequencies for a tweet's text."} word-frequencies
  (comp text/word-frequencies text))

(def ^{:doc "Term frequencies for a tweet's text."} term-frequencies
  (comp text/term-frequencies text))

(def ^{:doc "Extracts a tweet's user's handle."} user-handle
  (comp :screen_name :user))

(def ^{:doc "Extracts a tweet's user's description."} user-description
  (comp :description :user))

(s/defn user-info
  "Pulls out user info from a tweet and returns a map with the handle photo-url and twitter-page."
  [tweet]
  (let [{:keys [profile_image_url_https]} (get-in tweet [:user])
        handle (user-handle tweet)]
    {:twitter.user/handle handle
     :twitter.user/photo-url profile_image_url_https
     :twitter.user/twitter-page (str "https://twitter.com/" handle)}))

(s/defn hashtags :- [s/Str]
  "Pulls out hashtags from a tweet."
  [tweet]
  (->> (get-in tweet [:entities :hashtags])
       (map :text)))


(s/defn hashtag-frequencies :- {s/Str s/Int}
  "Frequencies of hashtags in a tweet."
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
  "Term frequencies for all words in all tweets."
  [tweets]
  (if (seq tweets)
    (apply merge-with + (map term-frequencies tweets))
    {}))

(s/defn combined-word-frequencies :- {s/Str s/Num}
  "Word frequencies for all words in all tweets."
  [tweets]
  (if (seq tweets)
    (apply merge-with + (map word-frequencies tweets))
    {}))

(s/defn top-hashtags
  "From a set of tweets extracts and returns the most popular hashtags in order."
  [tweets]
  (->> (hashtag-frequencies tweets)
       (sort-by second >) ;; descending sort
       (map first)))

(s/defn top-words
  "From a set of tweets extract and returns the most popular words in order."
  [tweets]
  (some->> tweets
           combined-word-frequencies
           (sort-by second >) ;; descending sort
           (map first)))
