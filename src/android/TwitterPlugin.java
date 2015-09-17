package com.phonegap.plugins;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.View;

import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.Status;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;


/**
 * This class echoes a string called from JavaScript.
 */
public class TwitterPlugin extends CordovaPlugin {
    private static SharedPreferences mSharedPreferences;
    Context context;
    Twitter twitter;

    private static String TWITTER_PREFERENCES = "twitterPref";
    private static String TWITTER_OAUTH_FIELD = "oauth";
    private static String TWITTER_OAUTH_VERIFIER_FIELD = "verifier";


    private static String TWITTER_CONSUMER_KEY = "miEtYd53qANl9KwIyr9NrgNcs";
    private static String TWITTER_CONSUMER_SECRET = "THdPrNECOHhSPDOojGSRPZeVjfqUxOK72p6KIGhhiuNKtXJnbv";
    private static String TWITTER_OAUTH_ACCESSTOKEN = "289556287-rFOF62TojrZPI74U5i7oOfiyxCaIEcBENp6OIQux";
    private static String TWITTER_OAUTH_ACCESSTOKENSECRET= "sht2Gght1uYyoDyXLmrrbVHvl5gs8xeBZEv2Bhzc0Kzda";
    private static String TWITTER_CALLBACK_URL = "";

    private static AccessToken loadAccessToken(){
        String token = mSharedPreferences.getString(TWITTER_OAUTH_FIELD,null);
        String tokenSecret = mSharedPreferences.getString(TWITTER_OAUTH_VERIFIER_FIELD,null);
        if(null != token && null != tokenSecret){
            return new AccessToken(token, tokenSecret);
        }else{
            return null;
        }
    }

    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }
    

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        context = this.cordova.getActivity().getApplicationContext();

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(TWITTER_CONSUMER_KEY)
                .setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET)
                .setOAuthAccessToken(TWITTER_OAUTH_ACCESSTOKEN)
                .setOAuthAccessTokenSecret(TWITTER_OAUTH_ACCESSTOKENSECRET)
                .setJSONStoreEnabled(true);
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();

        mSharedPreferences = context.getSharedPreferences(TWITTER_PREFERENCES, 0);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("isTwitterAvailable")) {
            this.isTwitterAvailable(callbackContext);
            return true;
        }else if(action.equals("isTwitterSetup")){
            this.isTwitterSetup(callbackContext);
            return true;
        }else if(action.equals("composeTweet")){
            String text = args.getJSONObject(0).getString("text");
            this.composeTweet(text,callbackContext);
            return true;
        }else if(action.equals("sendTweet")){
            String text = args.getJSONObject(0).getString("text");
            this.sendTweet(text,callbackContext);
            return true;
        }else if(action.equals("getPublicTimeline")){
            this.getPublicTimeline(callbackContext);
            return true;
        }else if(action.equals("searchByHashtag")){
            String hashtag = args.getString(0);
            this.searchByHashtag(hashtag,callbackContext);
            return true;
        }else if(action.equals("getMentions")){
            this.getMentions(callbackContext);
            return true;
        }else if(action.equals("getTwitterUsername")){
            this.getTwitterUsername(callbackContext);
            return true;
        }else if(action.equals("getTwitterProfile")){
            this.getTwitterProfile(callbackContext);
            return true;
        }else if(action.equals("getTWRequest")){
            String url = args.getJSONArray(0).getString(0);
            String params = args.getJSONArray(0).getString(1);
            this.getTWRequest(callbackContext);
            return true;
        }else if(action.equals("reTweet")){
            long tweetId = Long.parseLong(args.getString(0));
            this.reTweet(tweetId,callbackContext);
            return true;
        }else if(action.equals("addFavorites")){
            long tweetId = Long.parseLong(args.getString(0));
            this.addFavorites(tweetId,callbackContext);
            return true;
        }else if(action.equals("rmFavorites")){
            long tweetId = Long.parseLong(args.getString(0));
            this.rmFavorites(tweetId,callbackContext);
            return true;
        }

        return false;
    }

    private void isTwitterAvailable(CallbackContext callbackContext) {
        callbackContext.success();
    }

    private void isTwitterSetup(CallbackContext callbackContext) {

        callbackContext.success();
    }

    private void composeTweet(String newStatus, CallbackContext callbackContext) {

        // Create intent using ACTION_VIEW and a normal Twitter url:
        String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s", urlEncode(newStatus));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Narrow down to official Twitter app, if available:
        List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                intent.setPackage(info.activityInfo.packageName);
            }
        }

        context.startActivity(intent);

    }

    private void sendTweet(String newStatus, CallbackContext callbackContext) {
        try {
            Status status = twitter.updateStatus(newStatus);
            JSONObject tweet = new JSONObject(TwitterObjectFactory.getRawJSON(status));
            callbackContext.success(tweet);
        } catch (TwitterException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        }
    }

    private void getPublicTimeline(CallbackContext callbackContext) {
        try {
            List<Status> statuses = twitter.getHomeTimeline();
            JSONArray tweets = new JSONArray(TwitterObjectFactory.getRawJSON(statuses));

            JSONObject tweet = null;
            for (int i = 0; i < tweets.length(); i++) {
                tweet = tweets.getJSONObject(i);
            }


            callbackContext.success(tweets);
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        } catch (TwitterException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        }
    }

    private void searchByHashtag(String hashtag,CallbackContext callbackContext) {

        try {
            Query query = new Query(hashtag);
            QueryResult result;
            List<Status> tweets = new ArrayList<Status>();
            do {
                result = twitter.search(query);
                tweets.addAll(result.getTweets());
            } while ((query = result.nextQuery()) != null);
            JSONArray jsonTweets = new JSONArray(TwitterObjectFactory.getRawJSON(tweets));
            callbackContext.success(jsonTweets);
        } catch (TwitterException te) {
            te.printStackTrace();
            callbackContext.error(te.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        }

    }

    private void getMentions(CallbackContext callbackContext) {
        try {
            List<Status> statuses = twitter.getMentionsTimeline();
            JSONArray tweets = new JSONArray(TwitterObjectFactory.getRawJSON(statuses));
            callbackContext.success(tweets);
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        } catch (TwitterException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        }
    }

    private void getTwitterUsername(CallbackContext callbackContext) {
        try {
            User user = twitter.showUser(twitter.getId());
            callbackContext.success(user.getScreenName());
        } catch (TwitterException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        }
    }

    private void getTwitterProfile(CallbackContext callbackContext) {
        try {
            User user = twitter.showUser(twitter.getId());
            JSONObject jsonUser = new JSONObject(TwitterObjectFactory.getRawJSON(user));
            callbackContext.success(jsonUser);
        } catch (TwitterException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        }
    }

    private void getTWRequest(CallbackContext callbackContext) {

        callbackContext.success();
    }

    private void reTweet(long tweetId,CallbackContext callbackContext) {
        try {
            Status status = twitter.retweetStatus(tweetId);
            JSONObject tweet = new JSONObject(TwitterObjectFactory.getRawJSON(status));
            callbackContext.success(tweet);
        } catch (TwitterException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        }
    }

    private void addFavorites(long tweetId, CallbackContext callbackContext) {
        try {
            Status status = twitter.createFavorite(tweetId);
            JSONObject tweet = new JSONObject(TwitterObjectFactory.getRawJSON(status));
            callbackContext.success(tweet);
        } catch (TwitterException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        }
    }

    private void rmFavorites(long tweetId,CallbackContext callbackContext) {
        try {
            Status status = twitter.destroyFavorite(tweetId);
            JSONObject tweet = new JSONObject(TwitterObjectFactory.getRawJSON(status));
            callbackContext.success(tweet);
        } catch (TwitterException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        }
    }
}
