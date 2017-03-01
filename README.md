# twitter-friends

"Twitter Friends"

You will be developing a working Web site that allows your users to see a
twitter user's "Twitter Friends". The "Twitter Friends" are defined here as
two persons whose tweets are similar in content in some way. You are not
asked to find the closest friends of a person, just a set of reasonably
close friends that you can justify given the technical and resource
constraints you are working with, which we expect you to be able to
articulate.

On your site, when a user enters a person's twitter handle, your user
expects to see the set of this person's friends, along with their twitter
handles, profile pictures and the measures of closeness.  Feel free to
improve the user experience in whatever ways you feel necessary given the
time you have.

Your submission includes a link to the working Web site, a link (or
attachment) to the source code, and a link (or attachment) to a document
where you outline and justify your choices in design and implementation.
You are free to use whatever tools, resources and libraries that you have a
right to use.

## Usage

A user's followers and people she follows are likely very similar.  However,
the API has limits on data access so checking every follower is not possible.
Get data using search.

TODO
* Score results and order by them

FIXME

* People who don't use hashtags, need to look at user description
* If no description look at most frequent words and search for that.
* Handle exceptions for when the handle does not exist.
* Duplicate users returned by twitter search, so aggregate them into score.

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
