Example Captricity API Android Client
============

**This code is not maintained**

This is a prototype of an Android client that uses the Captricity API.  This
code illustrates some common patterns when working with the Captricity API, but
we do not keep this code up to date.  Things *will* break as the API evolves,
but we're happy to review pull requests with fixes.

## Code tour

The following are some useful pieces of the code:

### Authorization and Authentication

After you have created an API application, you need to implement a workflow
that will allow your application's users to authorize your application to use
the Captricity API on their behalf.

The [App Authorization with the Captricity API Quickstart](https://shreddr.captricity.com/developer/quickstart/app-authorization/)
contains a high-level overview of a standard authorization workflow.

You need two key pieces of information once you [create your application on the developer dashboard](https://shreddr.captricity.com/developer/):

* The application ID (a positive integer, listed as "App ID" on the developer dashboard)
* The application secret key (a 32 character hex string, listed as "Secret key" on the developer).

The high-level idea is that your App should craft an authorization URL (signed
with your application's secret key) that will direct the user to a Captricity
page where they can authorize your app.  Once they authorize your app, we will
redirect them to a URL specified in the authorization URL (often times back to
your app).  The return URL will contain with it an API token that you can use
to interact with the API as the user.

* [Here we craft the authorization URL once the user presses the login button](https://github.com/Captricity/DroidCap/blob/master/src/com/example/whiskeydroid/DummyLoginActivity.java#L154).  [Documentation is here](https://shreddr.captricity.com/developer/overview/#authentication).  [Quickstart is here](https://shreddr.captricity.com/developer/quickstart/app-authorization/).
* [Here we direct the user to the Captricity site using an Intent](https://github.com/Captricity/DroidCap/blob/master/src/com/example/whiskeydroid/DummyLoginActivity.java#L166).
* [Here register the captricity:// scheme so we will be able to handle the callback](https://github.com/Captricity/DroidCap/blob/master/AndroidManifest.xml#L34).
* [Here we handle the callback.  This is where you harvest the user's API token](https://github.com/Captricity/DroidCap/blob/master/src/com/example/whiskeydroid/CapLogin.java#L17).


### Using the API

See the Related Work section below for details on the particular pattern we are
using to interact with the Captricity API.  Here is [the module that interacts with the API](https://github.com/Captricity/DroidCap/blob/master/src/com/example/whiskeydroid/QueryCaptricityAPI.java).  Note that this app still uses the beta API.  We recommend that you [interact with the latest API version](the://shreddr.captricity.com/developer/api-reference/).



## Development Environment

This is an Eclipse project.  See

http://developer.android.com/sdk/installing.html

for details of setting up the dev environment.


## Related Work

* Watch ["Developing an Android RESTful Client App"](http://www.youtube.com/watch?v=JkU3VM1Vyp0) for some general tips on developing an android app to interact with a web API.


## License

This is licensed under the MIT license. See LICENSE.txt.
