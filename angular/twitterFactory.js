angular.module('twitter', [])
	.factory('$twitter', ['$q','$cordovaOauth', function ($q,$cordovaOauth) {
		var TWITTER_CONSUMER_KEY = "consumerKey";
    var TWITTER_CONSUMER_SECRET = "secretKey";
    var TWITTER_OAUTH_ACCESSTOKEN = "accesstoken";
    var TWITTER_OAUTH_ACCESSTOKENSECRET= "accessSecret";
    var TWITTER_CALLBACK_URL = "http://localhost/callback";
	  return {
	    available: function(success,error){
	      Twitter.isTwitterAvailable(function(available){
	        Twitter.isTwitterSetup(function(){
	          if(available){
	            success(true);
	          }else{
	            success(false);
	          }
	        },function(_e){
						error(_e);
					});
	      });
	    },
	    loginOauth: function(success,error){
	      $cordovaOauth.twitter(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET)
	        .then(function(result) {
	          Twitter.isTwitterSetup(function(response){
	            success(true);
	          },function(_e){
	            error(_e);
	          },TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, response.oauth_token, response.oauth_token_secret);
	        }, function(_e) {
	          error(_e);
	        });
	    },
			login: function(){
				Twitter.isTwitterSetup(function(response){
					success(true);
				},function(_e){
					error(_e);
				},TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, TWITTER_OAUTH_ACCESSTOKEN, TWITTER_OAUTH_ACCESSTOKENSECRET);
			},
	    composeTweet: function(success,error,tweetText,options){
	      Twitter.composeTweet(success,error,tweetText,options);
	    },
	    sendTweet: function(success,error,tweetText,replyID){
	      options = replyID ? {in_reply_to_status_id:replyID} : undefined;
	      Twitter.sendTweet(success,error,tweetText,options);
	    },
	    getPublicTimeLine: function(success,error){
	      Twitter.getPublicTimeline(success,error);
	    },
	    searchByHashtag: function(success,error,hashtag){
	      Twitter.searchByHashtag(success,error,hashtag);
	    },
	    getMentions: function(success,error){
	      Twitter.getMentions(success,error);
	    },
	    getUsername: function(success,error){
	      Twitter.getTwitterUsername(success,error);
	    },
	    getProfile: function(success,error){
	      Twitter.getTwitterProfile(success,error);
	    },
	    getRequest: function(url, params, success, failure, options){
	      Twitter.getTWRequest(url, params, success, failure, options);
	    },
	    reTweet:function(success,error,id){
	      Twitter.reTweet(success,error,id);
	    },
	    addFavorites:function(success,error,id){
	      Twitter.addFavorites(success,error,id);
	    },
	    rmFavorites:function(success,error,id){
	      Twitter.rmFavorites(success,error,id);
	    }
	  };
	}])
