(ns tf.tf-test
  (:require  [clojure.test :refer :all]
             [tf.core-test :as test]
             [taoensso.timbre :as log]
             [tf.core :as tf]
             [e85th.commons.util :as u]))

(use-fixtures :once test/with-system)

(def endpoint "/api/v1/friends")

(def test-data [{:twitter.user/handle "vfeldman55"
                 :twitter.user/photo-url "https://abs.twimg.com/sticky/default_profile_images/default_profile_6_normal.png"
                 :twitter.user/friend-score 13.2}
                {:twitter.user/handle "denisooi"
                 :twitter.user/photo-url "https://pbs.twimg.com/profile_images/790552962477219840/PhgPTVGx_normal.jpg"
                 :twitter.user/friend-score 39.2}])

(deftest ^:integration friends-test
  (with-redefs [tf/find-friends (constantly test-data)]
    (let [[status response] (test/api-call :get endpoint {:handle "foo"})]
      (log/infof "status: %s, response %s" status response)
      (is (= 200 status)))))
