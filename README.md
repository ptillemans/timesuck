# TimeSuck

An app which sucks all signs of where your time went from the apps we use daily.

## Google Calendar

This is a port from the code on [Java
Quickstart](https://developers.google.com/calendar/quickstart/java) page on the
google cloud docs site after wasting a ton of time trying to get clojure samples
to work.

Clojure really worked to get smooth Java interop and we can better use it, and
since all examples are 5 years old and the libs were not developed further it
seems that the need for them has dissipated.

Before the first time you run the app you need to create a *client_id.json* file
to set the client credentials to allow the app to use the gcal API. This file
can be downloaded from the Google Cloud Console form the *Credentials* part of
the *API* section. There is a sort of wizard to help with which credentials to
create. Specify an installed app, e.g. for Windows, and download the credentials
to the root folder of the project.

Once it is in place, it will the first time (and each time the token is expired)
open a browser window to let you authorize the app at Google to allow access to
the calendar. It listens on a temporary port and saves the token in the tokens
folder.

## Clojurescript support

I used the [blogpost of Christian
Johansen](https://cjohansen.no/tools-deps-figwheel-main-devcards-emacs/) to get
my setup in order and working with emacs and firefox.

I could not get to work with chrome on my Ubuntu box so changed the default
browser to firefox.

It also supports  devcards of which I heard, but never tried.

To start a REPL in the shell:

    $ clj -A:dev -m figwheel.main

from emacs:
