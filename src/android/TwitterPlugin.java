package com.phonegap.plugins;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.Status;
import twitter4j.Query;

import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;


/**
 * This class echoes a string called from JavaScript.
 */
public class TwitterPlugin extends CordovaPlugin {
    private static SharedPreferences mSharedPreferences;
    Context context;
    Twitter twitter;

    private static String TWITTER_PREFERENCES = "twitterPref";

    private static String TWITTER_CONSUMER_KEY;
    private static String TWITTER_CONSUMER_SECRET;
    private static String TWITTER_OAUTH_ACCESSTOKEN;
    private static String TWITTER_OAUTH_ACCESSTOKENSECRET;

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

        mSharedPreferences = context.getSharedPreferences(TWITTER_PREFERENCES, 0);
        TWITTER_CONSUMER_KEY            = mSharedPreferences.getString("TWITTER_CONSUMER_KEY",null);
        TWITTER_CONSUMER_SECRET         = mSharedPreferences.getString("TWITTER_CONSUMER_SECRET",null);
        TWITTER_OAUTH_ACCESSTOKEN       = mSharedPreferences.getString("TWITTER_OAUTH_ACCESSTOKEN",null);
        TWITTER_OAUTH_ACCESSTOKENSECRET = mSharedPreferences.getString("TWITTER_OAUTH_ACCESSTOKENSECRET", null);

    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("isTwitterAvailable")) {
            this.isTwitterAvailable(callbackContext);
            return true;
        }else if(action.equals("isTwitterSetup")){

            TWITTER_CONSUMER_KEY            = args.getString(0).equals("null") ? null : args.getString(0);
            TWITTER_CONSUMER_SECRET         = args.getString(1).equals("null") ? null : args.getString(1);
            TWITTER_OAUTH_ACCESSTOKEN       = args.getString(2).equals("null") ? null : args.getString(2);
            TWITTER_OAUTH_ACCESSTOKENSECRET = args.getString(3).equals("null") ? null : args.getString(3);

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
            String url = args.getString(0);
            String params = args.getString(1);
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

        if(TWITTER_OAUTH_ACCESSTOKEN !=  null && TWITTER_OAUTH_ACCESSTOKENSECRET  != null){
            callbackContext.success(1);
        }else{
            callbackContext.success(0);
        }
    }

    private void isTwitterSetup(CallbackContext callbackContext) {

        ConfigurationBuilder cb = new ConfigurationBuilder();

        cb.setDebugEnabled(true)
            .setJSONStoreEnabled(true)
            .setOAuthConsumerKey(TWITTER_CONSUMER_KEY)
            .setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

        if(TWITTER_OAUTH_ACCESSTOKEN != null && TWITTER_OAUTH_ACCESSTOKENSECRET != null){

            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            mEditor.putString("TWITTER_CONSUMER_KEY",TWITTER_CONSUMER_KEY);
            mEditor.putString("TWITTER_CONSUMER_SECRET", TWITTER_CONSUMER_SECRET);
            mEditor.putString("TWITTER_OAUTH_ACCESSTOKEN", TWITTER_OAUTH_ACCESSTOKEN);
            mEditor.putString("TWITTER_OAUTH_ACCESSTOKENSECRET", TWITTER_OAUTH_ACCESSTOKENSECRET);
            mEditor.apply();

            cb.setOAuthAccessToken(TWITTER_OAUTH_ACCESSTOKEN)
                .setOAuthAccessTokenSecret(TWITTER_OAUTH_ACCESSTOKENSECRET);
        }

        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();

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
        callbackContext.success();

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
