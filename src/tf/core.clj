(ns tf.core
  (:require [tf.models :as m]
            [twitter.api.restful :as twitter]
            [schema.core :as s]
            [clojure.string :as str]))

(s/defn get-user-tweets
  [{:keys [twitter-creds] :as res} handle :- s/Str]
  (let [result (twitter/statuses-user-timeline :oauth-creds twitter-creds :params {:screen-name handle})]
    (get-in result [:body])))

(s/defn get-tweet-search-results
  [{:keys [twitter-creds] :as res} query :- s/Str]
  (let [result (twitter/search-tweets :oauth-creds twitter-creds :params {:q query})]
    (get-in result [:body :statuses])))

(s/defn extract-user-info
  [tweet]
  (let [{:keys [screen_name profile_image_url_https]} (get-in tweet [:user])]
    {:twitter.user/handle screen_name
     :twitter.user/photo-url profile_image_url_https
     :twitter.user/friend-score 0
     :twitter.user/twitter-page (str "https://twitter.com/" screen_name)}))

(s/defn extract-hashtags :- [s/Str]
  [tweet]
  (->> (get-in tweet [:entities :hashtags])
       (map :text)))

(s/defn extract-user-description :- [s/Str]
  [tweet]
  (get-in tweet [:user :description]))

(s/defn tweets->hashtag-frequencies
  [tweets]
  (-> (map extract-hashtags tweets) flatten frequencies))

(s/defn top-hashtags
  "From a set of tweets extracts and returns the most popular hashtags in order."
  [tweets]
  (let [negate (partial * -1)]
    ;; this can be made more efficient esp reverse if sort-by did the right thing
    (->> tweets
         (tweets->hashtag-frequencies)
         (sort-by (comp negate second))
         (map first))))

(def get-top-hashtags
  (comp top-hashtags get-user-tweets))

(defn hashtags->search-clause
  [tags]
  (let [as-hashtag (partial str "#")]
    (str/join " OR " (map as-hashtag tags))))

;(hashtags->search-clause ["a" "b" "c"])

;; Takes a tweet and returns a tuple [Friend [s/Str]]
(def tweet->user+tags (juxt extract-user-info extract-hashtags))


(s/defn find-friends :- [m/Friend]
  [{:keys [twitter-creds] :as res} handle :- s/Str]
  (let [tweets (get-user-tweets res handle)
        hashtags (top-hashtags tweets)
        search-clause (hashtags->search-clause (take 5 hashtags))
        results (get-tweet-search-results res search-clause)
        all-user+tags (map tweet->user+tags results)]
    (map first all-user+tags))

  ;; get messages for user with handle
  ;; find most relevant hashtags in tweets
  ;; do search for top 5 hashtags using OR
  ;; based on the tweets from search, assign a score
  ;; sort by score
  )
