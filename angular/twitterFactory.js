angular.module('twitter', [])
	.factory('$twitter', ['$q', function ($q) {
		return {
			avalible: function(success,error){
				Twitter.isTwitterAvailable(function(response){
					if(response){
						Twitter.isTwitterSetup(function(response){
							if(response){
								success();
							}else{
								error();
							}
						});
					}else{
						error();
					}
				});
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
