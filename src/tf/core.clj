(ns tf.core
  (:require [tf.models :as m]
            [tf.tweet :as tweet]
            [twitter.api.restful :as twitter]
            [schema.core :as s]
            [e85th.commons.util :as u]
            [clojure.string :as str]))


;; use my term-frequencies as the weights
;; for each user determine word-frequencies for all tweets

(s/defn get-user-tweets
  [{:keys [twitter-creds] :as res} handle :- s/Str]
  (let [result (twitter/statuses-user-timeline :oauth-creds twitter-creds :params {:screen-name handle})]
    (get-in result [:body])))

(s/defn get-tweet-search-results
  [{:keys [twitter-creds] :as res} query :- s/Str]
  (let [result (twitter/search-tweets :oauth-creds twitter-creds :params {:q query})]
    (get-in result [:body :statuses])))

(s/defn hashtags->search-clause
  [tags :- [s/Str]]
  (let [as-hashtag (partial str "#")]
    (str/join " OR " (map as-hashtag tags))))

(s/defn handle-counts :- {s/Str s/Int}
  "Returns a map of handle to count of that handle."
  [friends]
  (u/group-by+ :twitter.user/handle identity count friends))

(s/defn score-word-frequencies
  [word->weight word->freq]
  (->> (u/intersect-with * word->weight word->freq)
       vals
       (reduce +)))

(s/defn score-search-results :- {s/Str s/Num}
  "ref-tweets are all by the same user. Answers with handle->score.
   Higher score indicates greater similarity."
  [ref-tweets found-tweets]
  (let [word->weight (tweet/combined-term-frequencies ref-tweets)
        handle->word->freq (tweet/word-frequencies-by-user-handle found-tweets)]
    (reduce (fn [m [handle word->freq]]
              (assoc m handle (score-word-frequencies word->weight word->freq)))
            {}
            handle->word->freq)))

(s/defn find-friends :- [m/Friend]
  [{:keys [twitter-creds] :as res} handle :- s/Str]
  (let [tweets (get-user-tweets res handle)
        hashtags (tweet/top-hashtags tweets)
        search-clause (hashtags->search-clause (take 5 hashtags))
        results (get-tweet-search-results res search-clause)
        handle->user-info (u/group-by+ tweet/user-handle tweet/user-info first results)
        handle->score (score-search-results tweets results)
        handle->user-info (reduce (fn [m [handle score]]
                                    (assoc-in m [handle :twitter.user/friend-score] score))
                                  handle->user-info
                                  handle->score)]
    (sort-by :twitter.user/friend-score (vals handle->user-info)))

  ;; get messages for user with handle
  ;; find most relevant hashtags in tweets
  ;; do search for top 5 hashtags using OR
  ;; based on the tweets from search, assign a score
  ;; sort by score
  )
