(ns tf.tweet-test
  (:require  [clojure.test :refer :all]
             [tf.test-data :as test-data]
             [taoensso.timbre :as log]
             [tf.tweet :as tweet]
             [e85th.commons.util :as u]
             [clojure.string :as str]))

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


(deftest top-words-test
  (is (= ["four" "three" "two" "one"] ;; NB exclamation is removed, lower cased
         (tweet/top-words [{:text "one two TWO three three three four four four four!"}]))))

(deftest combined-word-frequencies-test
  (is (= {"four" 8 "three" 6 "two" 4 "one" 2}
         (tweet/combined-word-frequencies [{:text "one two TWO three three three four four four four!"}
                                           {:text "one two TWO three three three four four four four!"}]))))

(deftest combined-term-frequencies-test
  ;; NB. terms are unique lower cased tokens. terms-count = 4, count-of-fours = 8, term frequency is 8/4 or 2
  (is (= {"four" 2 "three" 3/2 "two" 1 "one" 1/2}
         (tweet/combined-term-frequencies [{:text "one two TWO three three three four four four four!"}
                                           {:text "one two TWO three three three four four four four!"}])))

  ;; alternative scoring could have {"love" 1/4 "pizza" 1/2 "wine" 1/4 "cheese" 1/4}
  ;; but this approach weighs terms from shorter documents more
  (is (= {"love" 1/3 "pizza" 5/6 "wine" 1/3 "cheese" 1/2}
         (tweet/combined-term-frequencies [{:text "love pizza wine"}
                                           {:text "cheese pizza"}]))))

(deftest word-frequencies-by-user-handle-test
  (is (= {"john" {"user123" 1
                  "going" 1
                  "hiking" 2
                  "saturday" 1
                  "love" 1}
          "mary" {"good" 2
                  "manhattan" 1
                  "bourbon" 1
                  "rye" 1
                  "better" 1}}
         (tweet/word-frequencies-by-user-handle
          [{:user {:screen_name "mary"}
            :text "A good Manhattan."}
           {:user {:screen_name "john"}
            :text "@user123 Going hiking Saturday. Love #hiking."}
           {:user {:screen_name "mary"}
            :text "Good with #bourbon, better with #rye"}]))))
