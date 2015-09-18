CordovaTwitter
=======================

Build upon the work [etiennea](https://github.com/etiennea) with its version for IOS,
but also need to work for android and be compatible with ionic (Angular),
I decided to keep my own fork.

Dependencies
------------

In android use [twitter4j](http://twitter4j.org/en/index.html) Auto add this dependency


Installation
------------

Installation with cordova CLI

    cordova plugin add cordova-twitter

Installation with phonegap CLI

    phonegap plugin add cordova-twitter

Installation with Ionic CLI

	ionic plugin add cordova-twitter

Configuration
-------------

if you need Ionic / Angular factory for simplify your work you can add **angular/twitterFactory.js** in your project

For **Android** need modify this parameter in **angular/twitterFactory.js**

    var TWITTER_CONSUMER_KEY = "consumerKey";
    var TWITTER_CONSUMER_SECRET = "secretKey";
    var TWITTER_OAUTH_ACCESSTOKEN = "accesstoken";
    var TWITTER_OAUTH_ACCESSTOKENSECRET= "accessSecret";
    var TWITTER_CALLBACK_URL = "http://localhost/callback";

    This parameters you can config in [https://apps.twitter.com/](https://apps.twitter.com/)

If you want login with twitter, the best option is [Oauth plugin](http://ngcordova.com/docs/plugins/oauth/) and use the function **$twitter.loginOauth** provided by the factory



In **IOS** it's not necessary, IOS version use Twitter Account in iphone.

Tested in phonegap 4.x
----------------------



MIT license
