(ns tf.core
  (:require [tf.models :as m]
            [tf.tweet :as tweet]
            [tf.text :as text]
            [twitter.api.restful :as twitter]
            [schema.core :as s]
            [e85th.commons.util :as u]
            [taoensso.timbre :as log]
            [clojure.string :as str]))

;; Twitter recommends: Limit your searches to 10 keywords and operators.
(def max-search-terms 5)
(def default-search-term "#friends")

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

(s/defn search-clause :- (s/maybe s/Str)
  ([words :- [s/Str]]
   (search-clause identity words))
  ([word-modifier-fn words :- [s/Str]]
   (when (seq words)
     (str/join " OR " (map word-modifier-fn words)))))

(def hashtags-search-clause
  (partial search-clause (partial str "#")))

(s/defn score-word-frequencies :- s/Num
  "Generates a score for words that are keys in both input maps.
   Scoring is the sum of each weight times frequency.  This should
   introduce diversity to the sort. For example, if the subject has
   diverse interests, other twitter 'friends' with diversity could have
   higher scores when weights are factored in than a 'friend' with
   only one interest."
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

(s/defn build-search-clause
  "Uses the subject's tweets to build a search clause to find other
   users who can be considered 'friends'.  The input tweets should all be
   from the same user.  Tries to use hashtags for the search, but some
   people don't use hashtags, so then falls back to using the subject's description,
   and finally to words with the highest frequency in tweets.  The last option is
   not prefereble because the stop words aren't exhaustive.  Subject's description is
   useful, but may not capture sentiment.  Hashtags quite often capture sentiment and
   are preferable."
  [tweets]
  (let [tags-search (some->> tweets tweet/top-hashtags (take max-search-terms) hashtags-search-clause)
        desc-search (some->> tweets first tweet/user-description text/text-wo-stop-words (take max-search-terms) search-clause delay)
        words-search (some->> tweets tweet/top-words (take max-search-terms) search-clause delay)]
    (or tags-search @desc-search @words-search default-search-term)))


;; get messages for user with handle
;; find most relevant hashtags in tweets
;; do search for top 5 hashtags using OR
;; based on the tweets from search, assign a score
;; sort by score
(s/defn find-friends :- [m/Friend]
  "Gets messages by the passed in handle.
   Finds most relevant hashtags, user's description
   or top most frequent words in that order.  If a user has
   no tweets and no description then searches for #tweet.
   Scores the search results and returns in descending
   score order.  Higher score indicates more similarity and more
   likely to be 'friends'."
  [{:keys [twitter-creds] :as res} handle :- s/Str]
  (let [tweets (get-user-tweets res handle)
        search-clause (build-search-clause tweets)
        results (get-tweet-search-results res search-clause)
        handle->user-info (u/group-by+ tweet/user-handle tweet/user-info first results)
        handle->score (score-search-results tweets results)
        handle->user-info (reduce (fn [m [handle score]]
                                    (assoc-in m [handle :twitter.user/friend-score] score))
                                  handle->user-info
                                  handle->score)]
    (log/infof "Handle: %s, search clause: %s" handle search-clause)
    (sort-by :twitter.user/friend-score > (vals handle->user-info))))
