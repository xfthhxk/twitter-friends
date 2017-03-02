(ns tf.test-data
  (:require [clojure.edn :as edn]
             [clojure.java.io :as io]))

(def friend-response
  [{:twitter.user/handle "vfeldman55"
    :twitter.user/photo-url "https://abs.twimg.com/sticky/default_profile_images/default_profile_6_normal.png"
    :twitter.user/friend-score 13.2
    :twitter.user/twitter-page "https://twitter.com/vfeldman55"}
   {:twitter.user/handle "denisooi"
    :twitter.user/photo-url "https://pbs.twimg.com/profile_images/790552962477219840/PhgPTVGx_normal.jpg"
    :twitter.user/friend-score 39.2
    :twitter.user/twitter-page "https://twitter.com/denisooi"}])

(def ^:private read-data-file
  (comp edn/read-string slurp io/resource (partial str "sample-responses/")))

(def user-timeline-response
  (read-data-file "xfthhxk-tweets.edn"))

(def search-results-response
  (read-data-file "clojure-rust-search.edn"))

(def sample-tweet
  {:in_reply_to_screen_name nil,
   :is_quote_status false,
   :coordinates nil,
   :in_reply_to_status_id_str nil,
   :place nil,
   :possibly_sensitive false,
   :geo nil,
   :in_reply_to_status_id nil,
   :entities
   {:hashtags [{:text "NSA", :indices [23 27]}],
    :symbols [],
    :user_mentions
    [{:screen_name "EFF",
      :name "EFF",
      :id 4816,
      :id_str "4816",
      :indices [17 21]}],
    :urls
    [{:url "https://t.co/GvdIhrltXh",
      :expanded_url "https://eff.org/donate",
      :display_url "eff.org/donate",
      :indices [33 56]}]},
   :source
   "<a href=\"https://dev.twitter.com/docs/tfw\" rel=\"nofollow\">Twitter for Websites</a>",
   :lang "en",
   :in_reply_to_user_id_str nil,
   :id 357311998746312705,
   :contributors nil,
   :truncated false,
   :retweeted false,
   :in_reply_to_user_id nil,
   :id_str "357311998746312705",
   :favorited false,
   :user
   {:description "Programmer and Cocktail Afficonado",
    :profile_link_color "009999",
    :profile_sidebar_border_color "EEEEEE",
    :is_translation_enabled false,
    :profile_image_url
    "http://pbs.twimg.com/profile_images/3651430078/3c8b73078f89dd1254c8e2518932cf10_normal.jpeg",
    :profile_use_background_image true,
    :default_profile false,
    :profile_background_image_url
    "http://abs.twimg.com/images/themes/theme14/bg.gif",
    :is_translator false,
    :profile_text_color "333333",
    :name "AM",
    :profile_background_image_url_https
    "https://abs.twimg.com/images/themes/theme14/bg.gif",
    :favourites_count 0,
    :screen_name "xfthhxk",
    :entities {:description {:urls []}},
    :listed_count 0,
    :profile_image_url_https
    "https://pbs.twimg.com/profile_images/3651430078/3c8b73078f89dd1254c8e2518932cf10_normal.jpeg",
    :statuses_count 11,
    :has_extended_profile false,
    :contributors_enabled false,
    :following nil,
    :lang "en",
    :utc_offset nil,
    :notifications nil,
    :default_profile_image false,
    :profile_background_color "131516",
    :id 465827909,
    :follow_request_sent nil,
    :url nil,
    :translator_type "none",
    :time_zone nil,
    :profile_sidebar_fill_color "EFEFEF",
    :protected false,
    :profile_background_tile true,
    :id_str "465827909",
    :geo_enabled true,
    :location "",
    :followers_count 11,
    :friends_count 35,
    :verified false,
    :created_at "Mon Jan 16 19:43:42 +0000 2012"},
   :retweet_count 0,
   :favorite_count 0,
   :created_at "Wed Jul 17 01:33:13 +0000 2013",
   :text "Why I donated to @EFF: #NSA duh! https://t.co/GvdIhrltXh"})


(def ref-tweets
  [{:entities {:hashtags [{:text "NSA"}]}
    :text "Such wow #NSA."}

   {:entities {:hashtags [{:text "pizza"}]}
    :text "Boy do I love #pizza!"}

   {:entities {:hashtags [{:text "pizza"}
                          {:text "gelato"}]}
    :text "#Gelato and #pizza for dinner"}
   {:text "Love hiking."}])

(def found-tweets
  [{:entities {:hashtags [{:text "NSA"}]}
    :user {:screen_name "mary"}
    :text "#NSA snooping."}

   {:entities {:hashtags [{:text "pizza"}]}
    :user {:screen_name "mary"}
    :text "Good #pizza!"}

   {:entities {:hashtags [{:text "pizza"}
                          {:text "gelato"}]}
    :user {:screen_name "john"}
    :text "Love #pizza and #gelato."}

   {:user {:screen_name "hiker42"}
    :text "Italian gelato!"}

   {:user {:screen_name "hiker42"}
    :text "hiking fun"}])
