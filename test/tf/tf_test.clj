(ns tf.tf-test
  (:require  [clojure.test :refer :all]
             [tf.core-test :as test]
             [tf.test-data :as test-data]
             [taoensso.timbre :as log]
             [twitter.api.restful :as twitter]
             [tf.core :as tf]
             [e85th.commons.util :as u]))

(use-fixtures :once test/with-system)

(def endpoint "/api/v1/friends")

(deftest get-user-tweets-test
  (with-redefs [twitter/statuses-user-timeline (constantly test-data/user-timeline-response)]
    (let [tweets (tf/get-user-tweets test/system "xfthhxk")]
      (is (= tweets (:body test-data/user-timeline-response))))))

(deftest get-tweet-search-results
  (with-redefs [twitter/search-tweets (constantly test-data/search-results-response)]
    (let [tweets (tf/get-tweet-search-results test/system "#clojure OR #rust")]
      (is (= tweets (get-in test-data/search-results-response [:body :statuses]))))))

(deftest hashtags-search-clause-test
  (is (= "#clojure" (tf/hashtags-search-clause ["clojure"])))
  (is (= "#clojure OR #rust" (tf/hashtags-search-clause ["clojure" "rust"])))
  (is (nil? (tf/hashtags-search-clause []))))

(deftest find-friends-api-call-test
  ;; Using redefs because testing twitter rate limits preclude heavy testing
  ;; directly against the API
  (with-redefs [tf/find-friends (constantly test-data/friend-response)]
    (let [[status response] (test/api-call :get endpoint {:handle "foo"})]
      (is (= 200 status)))))

(deftest ^:integration find-friends-test
  ;; Using redefs because testing twitter rate limits preclude heavy testing
  ;; directly against the API
  (with-redefs [tf/find-friends (constantly test-data/friend-response)]
    (let [[status response] (test/api-call :get endpoint {:handle "foo"})]
      (is (= 200 status)))))
