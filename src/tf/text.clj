(ns tf.text
  (:require [schema.core :as s]
            [e85th.commons.util :as u]
            [clojure.string :as str]))

(def stop-words
  #{ "" "a" "an" "and" "are" "as" "at"
    "be" "but" "by" "can" "do" "for" "if" "in" "into" "is" "it"
    "no" "not" "of" "on" "or" "such"
    "that" "the" "their" "then" "there" "these" "they" "this" "to"
    "was" "why" "will" "with"})

(defn text->words
  "Takes an input string s and splits the string on non-text, non-decimal digits. See:
   http://stackoverflow.com/questions/1611979/remove-all-non-word-characters-from-a-string-in-java-leaving-accented-charact
   NB This is not great when there are URLs in the input text."
  [s]
  (str/split s #"[^\p{L}\p{Nd}]+"))

(def ^{:doc "Input text is lower cased then some stop words are removed.
             Response will be lower cased."}
  text-wo-stop-words
  (comp (partial remove stop-words) text->words str/lower-case))

(def ^{:doc "Lowercased word frequencies."} word-frequencies
  (comp frequencies text-wo-stop-words))

(s/defn term-frequencies :- {s/Str s/Num}
  "Calculates the term frequency for each word. NB no stemming is taking place.
   A term is a lower cased string token with some stop words removed."
  [s :- s/Str]
  (let [word->count (word-frequencies s)
        total-terms (count word->count)]
    (reduce (fn [m [k n]]
              (assoc m k (/ n total-terms)))
            {}
            word->count)))
