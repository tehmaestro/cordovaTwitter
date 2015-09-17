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

    cordova plugin add cordovaTwitter
  
Installation with phonegap CLI

    phonegap plugin add cordovaTwitter

Installation with Ionic CLI

	ionic plugin add cordovaTwitter

Configuration
-------------

if you need Ionic / Angular factory for simplify your work you can add angular/twitterFactory.js in your project

For **Android** need modify this parameter after add plugin

    private static String TWITTER_CONSUMER_KEY = "consumerKey";
    private static String TWITTER_CONSUMER_SECRET = "secretKey";
    private static String TWITTER_OAUTH_ACCESSTOKEN = "accesstoken";
    private static String TWITTER_OAUTH_ACCESSTOKENSECRET= "accessSecret";
    private static String TWITTER_CALLBACK_URL = "http://localhost.com";


Tested in phonegap 4.x
----------------------



MIT license
