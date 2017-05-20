package com.fr31b3u73r.jodel;

import java.util.List;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JodelAccount {

    JSONObject locationObject = new JSONObject();

    static final String API_URL = "https://api.go-tellm.com/api";
    static final String CLIENT_ID = "81e8a76e-1e02-4d17-9ba0-8a7020261b26";
    static final String SECRET = "DOTrPEbAXvJmqDKnvdLCPJlUyKenbDoAJWiAxxnT";
    static final String VERSION = "4.44.1";

    String accessToken = null;
    String deviceUID = null;
    String expirationDate = null;
    String distinctID = null;
    String refreshToken = null;
    String latitude = null;
    String longitude = null;
    JodelHTTPAction httpAction;

    public JodelAccount(String lat, String lng, String city, String country, String name, Boolean updateLocation,
                        String accessToken, String deviceUID, String refreshToken, String distinctID, String expirationDate) {

        this.httpAction = new JodelHTTPAction();

        this.latitude = lat;
        this.longitude = lng;

        this.locationObject.put("city", city);
        this.locationObject.put("loc_accuracy", 0.0);
        this.locationObject.put("country", country);
        this.locationObject.put("name", name);
        JSONObject locationCoordinates = new JSONObject();
        locationCoordinates.put("lat", Float.parseFloat(lat));
        locationCoordinates.put("lng", Float.parseFloat(lng));
        this.locationObject.put("loc_coordinates", locationCoordinates);

        if (accessToken != null && deviceUID != null && refreshToken != null && distinctID != null && expirationDate != null) {
            this.expirationDate = expirationDate;
            this.distinctID = distinctID;
            this.refreshToken = refreshToken;
            this.deviceUID = deviceUID;
            this.accessToken = accessToken;

            if (updateLocation == true) {
                this.updateHTTPParameter();
                this.httpAction.setLocation();
            }
        } else {
            this.refreshAllTokens();
        }

    }

    public JodelAccountData getAccountData() {
        JodelAccountData myJodelAccountData = new JodelAccountData();
        myJodelAccountData.accessToken = this.accessToken;
        myJodelAccountData.deviceUID = this.deviceUID;
        myJodelAccountData.distincID = this.distinctID;
        myJodelAccountData.expirationDate = this.expirationDate;
        myJodelAccountData.refreshToken = this.refreshToken;
        return myJodelAccountData;
    }

    // Creates a new account with random ID if self.device_uid is not set. Otherwise renews all tokens of the
    // account with ID = this.device_uid.
    public void refreshAllTokens() {
        if (this.deviceUID == null) {
            char[] validChars = "abcdef0123456789".toCharArray();
            StringBuilder sb = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 64; i++) {
                char c = validChars[random.nextInt(validChars.length)];
                sb.append(c);
            }
            this.deviceUID = sb.toString();
        }

        this.updateHTTPParameter();
        JodelHTTPResponse requestResponse = this.httpAction.getNewTokens();

        if (requestResponse.responseCode == 200) {
            String responseMessage = requestResponse.responseMessage;
            JSONParser parser = new JSONParser();
            try {
                JSONObject responseJson = (JSONObject) parser.parse(responseMessage);
                this.accessToken = responseJson.get("access_token").toString();
                this.expirationDate = responseJson.get("expiration_date").toString();
                this.refreshToken = responseJson.get("refresh_token").toString();
                this.distinctID = responseJson.get("distinct_id").toString();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // Refreshes the Access Token of the currently used account
    public void refreshAccessToken() {
        this.updateHTTPParameter();
        JodelHTTPResponse requestResponse = this.httpAction.getNewAccessToken();
        if (requestResponse.responseCode == 200) {
            String responseMessage = requestResponse.responseMessage;
            JSONParser parser = new JSONParser();
            try {
                JSONObject responseJson = (JSONObject) parser.parse(responseMessage);
                this.accessToken = responseJson.get("access_token").toString();
                this.expirationDate = responseJson.get("expiration_date").toString();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // Gets the captcha image url and key for not verified users
    public JodelRequestResponse getCaptchaData() {
        JodelRequestResponse requestResponse = new JodelRequestResponse();

        this.updateHTTPParameter();
        JodelHTTPResponse requestUserResponse = this.httpAction.getUserConfig();
        if (requestUserResponse.responseCode == 200) {
            String responseUserMessage = requestUserResponse.responseMessage;
            JSONParser parser = new JSONParser();
            try {
                JSONObject responseJson = (JSONObject) parser.parse(responseUserMessage);
                boolean verrifiedStatus = (Boolean) responseJson.get("verified");
                if (verrifiedStatus == false) {
                    this.updateHTTPParameter();
                    JodelHTTPResponse requestCaptchaResponse = this.httpAction.getCaptcha();
                    requestResponse.httpResponseCode = requestCaptchaResponse.responseCode;
                    if (requestCaptchaResponse.responseCode == 200) {
                        String responseCaptchaMessage = requestCaptchaResponse.responseMessage;
                        requestResponse.rawResponseMessage = responseCaptchaMessage;
                        JSONParser parserCaptcha = new JSONParser();
                        try {
                            JSONObject responseCaptchaJson = (JSONObject) parserCaptcha.parse(responseCaptchaMessage);
                            String captchaUrl = responseCaptchaJson.get("image_url").toString();
                            String captchaKey = responseCaptchaJson.get("key").toString();
                            requestResponse.responseValues.put("captchaUrl", captchaUrl);
                            requestResponse.responseValues.put("captchaKey", captchaKey);

                        } catch (ParseException e) {
                            requestResponse.rawErrorMessage = e.getMessage();
                            e.printStackTrace();
                            requestResponse.error = true;
                            requestResponse.errorMessage = "Could not parse response JSON!";
                        }
                    }
                }
            } catch (ParseException e) {
                requestResponse.rawErrorMessage = e.getMessage();
                e.printStackTrace();
                requestResponse.error = true;
                requestResponse.errorMessage = "Could not parse response JSON!";
            }
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // Solves the captcha so that a user can verify his account
    public JodelRequestResponse verifyCaptcha(String key, List<Integer> positions) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse submitCaptchaVerification = this.httpAction.submitCaptcha(key, positions);
        requestResponse.httpResponseCode = submitCaptchaVerification.responseCode;
        if (submitCaptchaVerification.responseCode == 200) {
            String responseCaptchaMessage = submitCaptchaVerification.responseMessage;
            requestResponse.rawResponseMessage = responseCaptchaMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // Creates a new Jodel
    public JodelRequestResponse createPost(String message, String base64Image, String color, String channel, int ancestor) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        if (message == null && base64Image == null) {
            requestResponse.error = true;
            requestResponse.errorMessage = "Message or Image is mandatory!";
            return requestResponse;
        }
        this.updateHTTPParameter();
        JodelHTTPResponse submitPost = this.httpAction.submitPost(message, base64Image, color, ancestor, channel);
        requestResponse.httpResponseCode = submitPost.responseCode;
        if (submitPost.responseCode == 200) {
            String responseCaptchaMessage = submitPost.responseMessage;
            requestResponse.rawResponseMessage = responseCaptchaMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // Creates a new Jodel with no ancestor (i.e. no reply)
    public JodelRequestResponse createPost(String message, String base64Image, String color, String channel) {
        return this.createPost(message, base64Image, color, channel, 0);
    }

    // Gets Jodels matching given criteria
    public JodelRequestResponse getPosts(String postTypes, int skip, int limit, String after, boolean mine, String hashtag, String channel, boolean pictures) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();

        String category = "location";
        String apiVersion = "v2";
        String picturesPosts = "posts";
        if (mine == true) {
            category = "mine";
        } else if (hashtag != null) {
            category = "hashtag";
            apiVersion = "v3";
        } else if (channel != null) {
            category = "channel";
            apiVersion = "v3";
        }
        if (pictures == true) {
            apiVersion = "v3";
            picturesPosts = "pictures";
        }
        if (postTypes == null) {
            postTypes = "";
        }
        String url = "/" + apiVersion + "/" + picturesPosts + "/" + category + "/" + postTypes;

        this.updateHTTPParameter();
        JodelHTTPResponse getJodels = this.httpAction.getJodels(url, skip, limit, after, hashtag, channel);
        requestResponse.httpResponseCode = getJodels.responseCode;
        if (getJodels.responseCode == 200) {
            String responseJodelsMessage = getJodels.responseMessage;
            requestResponse.rawResponseMessage = responseJodelsMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // Gets recent Jodels matching given criteria
    public JodelRequestResponse getPostsRecent(int skip, int limit, String after, boolean mine, String hashtag, String channel) {
        return this.getPosts("", skip, limit, after, mine, hashtag, channel, false);
    }

    // Gets popular Jodels matching given criteria
    public JodelRequestResponse getPostsPopular(int skip, int limit, String after, boolean mine, String hashtag, String channel) {
        return this.getPosts("popular", skip, limit, after, mine, hashtag, channel, false);
    }

    // Gets most discussed Jodels matching given criteria
    public JodelRequestResponse getPostsDiscussed(int skip, int limit, String after, boolean mine, String hashtag, String channel) {
        return this.getPosts("discussed", skip, limit, after, mine, hashtag, channel, false);
    }

    // Gets recent picture Jodels matching given criteria
    public JodelRequestResponse getPicturesRecent(int skip, int limit, String after) {
        return this.getPosts("", skip, limit, after, false, null, null, true);
    }

    // Gets popular picture Jodels matching given criteria
    public JodelRequestResponse getPicturesPopular(int skip, int limit, String after) {
        return this.getPosts("popular", skip, limit, after, false, null, null, true);
    }

    // Gets most discussed picture Jodels matching given criteria
    public JodelRequestResponse getPicturesDiscussed(int skip, int limit, String after) {
        return this.getPosts("discussed", skip, limit, after, false, null, null, true);
    }

    // Gets your pinned Jodels matching given criteria
    public JodelRequestResponse getMyPinnedPosts(int skip, int limit, String after) {
        return this.getPosts("pinned", skip, limit, after, true, null, null, false);
    }

    // Gets Jodels you replied to matching given criteria
    public JodelRequestResponse getMyRepliedPosts(int skip, int limit, String after) {
        return this.getPosts("replies", skip, limit, after, true, null, null, false);
    }

    // Gets Jodels you voted to matching given criteria
    public JodelRequestResponse getMyVotedPosts(int skip, int limit, String after) {
        return this.getPosts("votes", skip, limit, after, true, null, null, false);
    }

    // Gets a single Jodel with all replies by post ID
    public JodelRequestResponse getPostDetails(String postID) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse getJodel = this.httpAction.getJodel(postID);
        requestResponse.httpResponseCode = getJodel.responseCode;
        if (getJodel.responseCode == 200) {
            String responseJodelsMessage = getJodel.responseMessage;
            requestResponse.rawResponseMessage = responseJodelsMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // Gets a single Jodel in new V3 of endpoint with all replies by post ID
    public JodelRequestResponse getPostDetailsV3(String postID, int skip) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse getJodel = this.httpAction.getJodelV3(postID, skip);
        requestResponse.httpResponseCode = getJodel.responseCode;
        if (getJodel.responseCode == 200) {
            String responseJodelsMessage = getJodel.responseMessage;
            requestResponse.rawResponseMessage = responseJodelsMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // upvotes a post
    public JodelRequestResponse upvoteJodel(String postID) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse performUpvote = this.httpAction.performUpvote(postID);
        requestResponse.httpResponseCode = performUpvote.responseCode;
        if (performUpvote.responseCode == 200) {
            String responseJodelsMessage = performUpvote.responseMessage;
            requestResponse.rawResponseMessage = responseJodelsMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // downvotes a post
    public JodelRequestResponse downvoteJodel(String postID) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse performDownvote = this.httpAction.performDownvote(postID);
        requestResponse.httpResponseCode = performDownvote.responseCode;
        if (performDownvote.responseCode == 200) {
            String responseJodelsMessage = performDownvote.responseMessage;
            requestResponse.rawResponseMessage = responseJodelsMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // thanks a post
    public JodelRequestResponse thankJodel(String postID) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse performThank = this.httpAction.performThank(postID);
        requestResponse.httpResponseCode = performThank.responseCode;
        if (performThank.responseCode == 200) {
            String responseJodelsMessage = performThank.responseMessage;
            requestResponse.rawResponseMessage = responseJodelsMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // get share url
    public JodelRequestResponse getJodelShareLink(String postID) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse getShareLink = this.httpAction.getJodelShareURL(postID);
        requestResponse.httpResponseCode = getShareLink.responseCode;
        if (getShareLink.responseCode == 200) {
            String responseJodelsMessage = getShareLink.responseMessage;
            requestResponse.rawResponseMessage = responseJodelsMessage;
            JSONParser parser = new JSONParser();
            try {
                JSONObject responseJson = (JSONObject) parser.parse(responseJodelsMessage);
                String url = (String) responseJson.get("url");
                requestResponse.responseValues.put("shareLink", url);
            } catch (Exception e) {
                requestResponse.rawErrorMessage = e.getMessage();
                e.printStackTrace();
                requestResponse.error = true;
                requestResponse.errorMessage = "Could not parse response JSON!";
            }
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // pin a Jodel
    public JodelRequestResponse pinJodel(String postID) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse setPin = this.httpAction.setPin(postID);
        requestResponse.httpResponseCode = setPin.responseCode;
        if (requestResponse.httpResponseCode == 200) {
            requestResponse.rawResponseMessage = setPin.responseMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // unpin a Jodel
    public JodelRequestResponse unpinJodel(String postID) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse removePin = this.httpAction.removePin(postID);
        requestResponse.httpResponseCode = removePin.responseCode;
        if (requestResponse.httpResponseCode == 200) {
            requestResponse.rawResponseMessage = removePin.responseMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // enable notifications of a Jodel
    public JodelRequestResponse enableNotification(String postID) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse notifyJodel = this.httpAction.enableNotify(postID);
        requestResponse.httpResponseCode = notifyJodel.responseCode;
        if (requestResponse.httpResponseCode == 200) {
            requestResponse.rawResponseMessage = notifyJodel.responseMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // disable notifications of a Jodel
    public JodelRequestResponse disableNotification(String postID) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse noNotifyJodel = this.httpAction.disableNotify(postID);
        requestResponse.httpResponseCode = noNotifyJodel.responseCode;
        if (requestResponse.httpResponseCode == 200) {
            requestResponse.rawResponseMessage = noNotifyJodel.responseMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // deletes a (own) Jodel
    public JodelRequestResponse deleteJodel(String postID) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse removeJodel = this.httpAction.removeJodel(postID);
        requestResponse.httpResponseCode = removeJodel.responseCode;
        if (requestResponse.httpResponseCode == 204) {
            requestResponse.rawResponseMessage = removeJodel.responseMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // upvotes a sticky Jodel
    public JodelRequestResponse upvoteStickyJodel(String postID) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse upvoteSticky = this.httpAction.performUpvoteSticky(postID);
        requestResponse.httpResponseCode = upvoteSticky.responseCode;
        if (requestResponse.httpResponseCode == 200) {
            requestResponse.rawResponseMessage = upvoteSticky.responseMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // downvotes a sticky Jodel
    public JodelRequestResponse downvoteStickyJodel(String postID) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse downvoteSticky = this.httpAction.performDownvoteSticky(postID);
        requestResponse.httpResponseCode = downvoteSticky.responseCode;
        if (requestResponse.httpResponseCode == 200) {
            requestResponse.rawResponseMessage = downvoteSticky.responseMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // dismisses a sticky Jodel
    public JodelRequestResponse dismissStickyJodel(String postID) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse dismissSticky = this.httpAction.performDismissSticky(postID);
        requestResponse.httpResponseCode = dismissSticky.responseCode;
        if (requestResponse.httpResponseCode == 200) {
            requestResponse.rawResponseMessage = dismissSticky.responseMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // gets Notifications
    public JodelRequestResponse getNotifications() {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse getNotify = this.httpAction.getNotifications();
        requestResponse.httpResponseCode = getNotify.responseCode;
        if (requestResponse.httpResponseCode == 200) {
            requestResponse.rawResponseMessage = getNotify.responseMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // gets new Notifications
    public JodelRequestResponse getNotificationsNew() {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse getNotify = this.httpAction.getNotificationsNew();
        requestResponse.httpResponseCode = getNotify.responseCode;
        if (requestResponse.httpResponseCode == 200) {
            requestResponse.rawResponseMessage = getNotify.responseMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // sets a notification "read" by postID or notificationID
    public JodelRequestResponse setNotificationRead(String postID, String notificationID) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse setNotificationRead = null;
        if (postID != null) {
            setNotificationRead = this.httpAction.setNotificationReadPostID(postID);
        } else if (notificationID != null) {
            setNotificationRead = this.httpAction.setNotificationReadNotificationID(notificationID);
        } else {
            requestResponse.error = true;
        }
        requestResponse.httpResponseCode = setNotificationRead.responseCode;
        if (requestResponse.httpResponseCode != 204) {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // gets the recommended channels
    public JodelRequestResponse getRecommendedChannels() {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse recommendedChannels = this.httpAction.getRecommendedChannels();
        requestResponse.httpResponseCode = recommendedChannels.responseCode;
        if (requestResponse.httpResponseCode == 200) {
            requestResponse.rawResponseMessage = recommendedChannels.responseMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // gets metadata of a channel
    public JodelRequestResponse getChannelMeta(String channel) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse channelMeta = this.httpAction.getChannelMeta(channel);
        requestResponse.httpResponseCode = channelMeta.responseCode;
        if (requestResponse.httpResponseCode == 200) {
            requestResponse.rawResponseMessage = channelMeta.responseMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // follows a channel
    public JodelRequestResponse followChannel(String channel) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse follow = this.httpAction.followChannel(channel);
        requestResponse.httpResponseCode = follow.responseCode;
        if (requestResponse.httpResponseCode == 204) {
            requestResponse.rawResponseMessage = follow.responseMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // unfollows a channel
    public JodelRequestResponse unfollowChannel(String channel) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse unfollow = this.httpAction.unfollowChannel(channel);
        requestResponse.httpResponseCode = unfollow.responseCode;
        if (requestResponse.httpResponseCode == 204) {
            requestResponse.rawResponseMessage = unfollow.responseMessage;
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    // sets the user profile
    public JodelRequestResponse setUserProfile(String userType, String gender, int age) {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        if (userType != null && gender != null) {
            this.updateHTTPParameter();
            JodelHTTPResponse setUserProfile = this.httpAction.setUserProfile(userType, gender, age);
            requestResponse.httpResponseCode = setUserProfile.responseCode;
            if (requestResponse.httpResponseCode != 204) {
                requestResponse.error = true;
            }
        } else {
            requestResponse.error = true;
            requestResponse.errorMessage = "Please specify all necessary parameters!";
        }
        return requestResponse;
    }

    // gets your karma
    public JodelRequestResponse getKarma() {
        JodelRequestResponse requestResponse = new JodelRequestResponse();
        this.updateHTTPParameter();
        JodelHTTPResponse karmaResponse = this.httpAction.getKarma();
        requestResponse.httpResponseCode = karmaResponse.responseCode;
        if (requestResponse.httpResponseCode == 200) {
            String responseKarma = karmaResponse.responseMessage;
            requestResponse.rawResponseMessage = responseKarma;
            JSONParser parserCaptcha = new JSONParser();
            try {
                JSONObject responseCaptchaJson = (JSONObject) parserCaptcha.parse(responseKarma);
                String karma = responseCaptchaJson.get("karma").toString();
                requestResponse.responseValues.put("karma", karma);

            } catch (ParseException e) {
                requestResponse.rawErrorMessage = e.getMessage();
                e.printStackTrace();
                requestResponse.error = true;
                requestResponse.errorMessage = "Could not parse response JSON!";
            }
        } else {
            requestResponse.error = true;
        }
        return requestResponse;
    }

    private void updateHTTPParameter() {
        this.httpAction.updateAccessValues(this.locationObject, this.accessToken, this.distinctID, this.refreshToken, this.deviceUID, this.expirationDate, this.latitude, this.longitude);
    }
}
