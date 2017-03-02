# twitter-friends

Identify other Twitter users who share some level of similarity.

## Build
```shell
lein uberjar
```

## Run Tests
```shell
lein test :all
```

## Run Server
Any of the following will do after the build step above.

```shell
API_KEY=${key} API_SECRET=${secret} java -jar ./target/twitter-friends-0.1.0-standalone.jar
```

```shell
API_KEY=${key} API_SECRET=${secret} lein run
```

UI: http://localhost:9001

Swagger UI: http://localhost:9001/swagger

## Command Line

```shell
API_KEY=${key} API_SECRET=${secret} lein run --mode cli --handle xfthhxk
```

## Design

A user's followers and people she follows are likely very similar.  However,
the API has limits on data access so checking every follower is not possible.
Instead, the following approach was taken:
* Get the subject's timeline (most recent tweets)
* Figure out terms relevant to the subject in order of preference
  * Find most used hashtags
  * Look at his/her own description
  * Tabulate most frequent words in tweets
  * Fallback: is the search for #friends
* Submit most frequent terms to Twitter's search endpoint.
* Score results
  * Calculate word frequency by user
  * Calculate subject's term frequency to act as a weight
  * Sum the reuslt of multiplying corresponding word frequency and weight
  * Higher score implies greater potential similarity.
* Display matching users sorted by descending score (most relevant)


The preference for hashtags is because the user is indicating what is
important in the tweet and also because they seem to encode
a perspective/sentiment. For example, #DeleteUber will better match
users then just going off of text ie "uber".

I quickly noticed that some class of user's very rarely use hashtags. To handle
this case, I looked to how a user describes himself/herself.

As a fallback to lack of hashtags and no self description, the search is based
off of most frequent words in tweets (removing stop words). The stop words could
be larger, they are currently based off of Datomic's stop words.

Without any other input, search happens via the #friends hashtag.

## Implementation
* The UI is a single page app based on re-frame.
* The UI makes API calls to get the data and uses kioo templates to render the views.
* The server exposes APIs which uses compojure-api and so Swagger API docs are available.
* The application is also runnable from the command line.

This project is simple enough to do with just a server side rendered UI. However, I already
had a template project to work off of and generally I do prefer doing single page apps.
UIs are full of state and it's easier to not lose it at every request.


Choice in libraries:

* compojure-api: Great for building documented APIs with an explorable UI
* schema: Have not yet explored Spec in depth. Good for communicating/enforcing inputs/outputs.
* re-frame: Well designed, lots of experience with it.
* kioo: Prefer it to hiccup in most situations. Allows designers to just work with HTML and CSS.

## Constraints
* Twitter's API restricts non-paying consumers to a handful of
  calls per endpoint per 15 minute period.
* Twitter has an index of recent (at most 9 days) tweets available for search.


## Issues Encountered
* Not everyone uses hashtags.
* Some accounts are private.
* Some accounts have no tweets and no description.
* Search can return tweets by same user need to merge scoring.
* Results and scores change as new tweets come in.


## License

Copyright © 2017 Amar Mehta

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
