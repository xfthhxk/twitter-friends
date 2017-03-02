(ns tf.core-test
  (:require [e85th.test.http :as http]
            [e85th.commons.util :as u]
            [twitter.api.restful :as twitter]
            [tf.core :as tf]
            [tf.routes :as routes]
            [tf.test-data :as test-data]
            [tf.test-system :as test-system]
            [com.stuartsierra.component :as component]
            [schema.core :as s]
            [clojure.test :refer :all]
            [taoensso.timbre :as log]
            [clojure.string :as str]))

(defonce system nil)

(defn init!
  "Call this first before using any other functions."
  []
  (u/set-utc-tz)
  (s/set-fn-validation! true)
  (alter-var-root #'system (constantly (component/start (test-system/make {}))))
  (http/init! {:routes (routes/make-handler (:app system))}))


(def api-call (http/make-transit-api-caller))

(defn with-system
  "Runs tests using the test system."
  [f]
  (when (not system)
    (println "Starting test system")
    (init!))
  (f))


(use-fixtures :once with-system)

(def endpoint "/api/v1/friends")

(deftest get-user-tweets-test
  (with-redefs [twitter/statuses-user-timeline (constantly test-data/user-timeline-response)]
    (let [tweets (tf/get-user-tweets system "xfthhxk")]
      (is (= tweets (:body test-data/user-timeline-response))))))

(deftest get-tweet-search-results
  (with-redefs [twitter/search-tweets (constantly test-data/search-results-response)]
    (let [tweets (tf/get-tweet-search-results system "#clojure OR #rust")]
      (is (= tweets (get-in test-data/search-results-response [:body :statuses]))))))

(deftest hashtags-search-clause-test
  (is (= "#clojure" (tf/hashtags-search-clause ["clojure"])))
  (is (= "#clojure OR #rust" (tf/hashtags-search-clause ["clojure" "rust"])))
  (is (nil? (tf/hashtags-search-clause []))))

(deftest build-search-clause-test
  (testing "no tweets (degenerate case)"
    (is (= tf/default-search-term (tf/build-search-clause []))))

  (testing "user description"
    (is (= "programmer OR cocktails"
           (tf/build-search-clause [{:user {:description "Programmer and Cocktails"}}
                                    {:text "Robots are taking over!"}]))))

  (testing "one tag, prefer tag over description and plain text tweet."
    (is (= "#NSA" (tf/build-search-clause [{:user {:description "Programmer and Cocktails"}
                                            :entities {:hashtags [{:text "NSA"}]}}
                                           {:text "Love hiking."}]))))

  (testing "more than one tag"
    (is (= "#NSA OR #pizza" (tf/build-search-clause [{:entities {:hashtags [{:text "NSA"}]}}
                                                     {:entities {:hashtags [{:text "pizza"}]}}
                                                     {:text "Love #hiking."}]))))
  (testing "no tags, no description only text"
    (let [clause (tf/build-search-clause [{:text "Going fishing."}
                                          {:text "Love hiking."}])]
      (is (= #{"going" "fishing" "love" "hiking"}
             (set (str/split clause #" OR ")))))))

(deftest score-word-frequencies-test
  (testing "no matching words"
    (is (zero? (tf/score-word-frequencies {"pizza" 2/3 "love" 1/3}
                                          {"ice" 3 "cream" 2}))))

  (testing "one matches"
    (is (= 3 (tf/score-word-frequencies {"pizza" 2/3 "rainbow" 1/3}
                                        {"rainbow" 9 "cream" 2}))))
  (testing "multiple matches"
    (is (= 13/3 (tf/score-word-frequencies {"pizza" 2/3 "rainbow" 1/3}
                                           {"pizza" 2 "rainbow" 9 "cream" 3})))))

(deftest score-search-results-test
  (is (= {"mary" 13/12
          "john" 5/3
          "hiker42" 5/6}
         (tf/score-search-results test-data/ref-tweets test-data/found-tweets))))

(deftest ^:integration find-friends-test
  ;; Using redefs because testing twitter rate limits preclude heavy testing
  ;; directly against the API
  (with-redefs [tf/find-friends (constantly test-data/friend-response)]
    (let [[status response] (api-call :get endpoint {:handle "foo"})]
      (is (= 200 status)))))

(comment
  (init!) )
