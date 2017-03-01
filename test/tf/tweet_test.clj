(ns tf.tweet-test
  (:require  [clojure.test :refer :all]
             [tf.test-data :as test-data]
             [taoensso.timbre :as log]
             [tf.tweet :as tweet]
             [e85th.commons.util :as u]))

(deftest user-info-test
  (is (= (tweet/user-info test-data/sample-tweet)
         {:twitter.user/handle "xfthhxk"
          :twitter.user/photo-url "https://pbs.twimg.com/profile_images/3651430078/3c8b73078f89dd1254c8e2518932cf10_normal.jpeg"
          :twitter.user/friend-score 0
          :twitter.user/twitter-page "https://twitter.com/xfthhxk"})))

(deftest hashtags-test
  (is (= ["NSA"] (tweet/hashtags test-data/sample-tweet))))


(deftest user-description-test
  (is (= "Programmer and Cocktail Afficonado"
         (tweet/user-description test-data/sample-tweet))))

(deftest hashtag-frequencies-test
  (is (= {"ironManSA2014" 1
          "NSA" 1
          "kelp" 1
          "cocktails" 1
          "bryantpark" 1}
         (tweet/hashtag-frequencies (:body test-data/user-timeline-response)))))

(deftest top-hashtags-test
  (let [tweets [{:entities {:hashtags [{:text "NSA"}]}}
                {:entities {:hashtags [{:text "cocktails"}]}}
                {:entities {:hashtags [{:text "cocktails"}
                                       {:text "NSA"}]}}
                {:entities {:hashtags []}}
                {:entities {:hashtags [{:text "cocktails"}
                                       {:text "kelp"}]}}]]
    (is (= ["cocktails" "NSA" "kelp"]
           (tweet/top-hashtags tweets)))))

(deftest user-info-test
  (is (= {:twitter.user/handle "xfthhxk"
          :twitter.user/photo-url "https://pbs.twimg.com/profile_images/3651430078/3c8b73078f89dd1254c8e2518932cf10_normal.jpeg"
          :twitter.user/twitter-page "https://twitter.com/xfthhxk"}
         (tweet/user-info test-data/sample-tweet))))
