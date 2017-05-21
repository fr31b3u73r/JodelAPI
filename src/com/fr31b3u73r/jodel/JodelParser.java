package com.fr31b3u73r.jodel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JodelParser {
    /**
     * Parses a list of Jodels
     * @param jodelsJSON String containing a JSON with jodels (from rawResponse)
     * @return A list of objects from type JodelPost
     */
    public static List<JodelPost> getParsedJodels(String jodelsJSON) {
        List<JodelPost> result = new ArrayList<JodelPost>();
        JSONParser parserJodels = new JSONParser();
        try {
            JSONObject responsePostJson = (JSONObject) parserJodels.parse(jodelsJSON);
            JSONArray jodelsArray = (JSONArray) responsePostJson.get("posts");

            Iterator i = jodelsArray.iterator();

            while (i.hasNext()) {
                JSONObject jodel = (JSONObject) i.next();
                JodelPost jodelPost = new JodelPost();
                jodelPost.message = (String) jodel.get("message");
                jodelPost.createdAt = (String) jodel.get("created_at");
                jodelPost.updatedAt = (String) jodel.get("updated_at");
                jodelPost.pinCount = (long) jodel.get("pin_count");
                jodelPost.color = (String) jodel.get("color");
                jodelPost.gotThanks = (boolean) jodel.get("got_thanks");
                jodelPost.thanksCount = (long) jodel.get("thanks_count");
                try {
                    jodelPost.imageApproved = (boolean) jodel.get("image_approved");
                    jodelPost.imageURL = (String) jodel.get("image_url");
                    jodelPost.thumbnailURL = (String) jodel.get("thumbnail_url");
                } catch (Exception e) {
                }
                try {
                    jodelPost.fromHome = (boolean) jodel.get("from_home");
                } catch (Exception e) {
                }
                try {
                    jodelPost.childCount = (long) jodel.get("child_count");
                } catch (Exception e) {
                }
                jodelPost.replier = (long) jodel.get("replier");
                jodelPost.postID = (String) jodel.get("post_id");
                jodelPost.discoveredBy = (long) jodel.get("discovered_by");
                jodelPost.voteCount = (long) jodel.get("vote_count");
                jodelPost.shareCount = (long) jodel.get("share_count");
                jodelPost.userHandle = (String) jodel.get("user_handle");
                jodelPost.postOwn = (String) jodel.get("post_own");
                jodelPost.distance = (long) jodel.get("distance");
                result.add(jodelPost);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Parses a single Jodel (with replies)
     * @param jodelJSON String containing a JSON with a single jodel (from rawResponse)
     * @return An object of type JodelPost containing parsed Jodel with replies
     */
    public static JodelPost getParsedJodel(String jodelJSON) {
        JSONParser parserJodels = new JSONParser();
        JodelPost jodelPost = new JodelPost();
        try {
            JSONObject responsePostJson = (JSONObject) parserJodels.parse(jodelJSON);

            jodelPost.message = (String) responsePostJson.get("message");
            jodelPost.createdAt = (String) responsePostJson.get("created_at");
            jodelPost.updatedAt = (String) responsePostJson.get("updated_at");
            jodelPost.pinCount = (long) responsePostJson.get("pin_count");
            jodelPost.color = (String) responsePostJson.get("color");
            jodelPost.gotThanks = (boolean) responsePostJson.get("got_thanks");
            jodelPost.thanksCount = (long) responsePostJson.get("thanks_count");
            try {
                jodelPost.imageApproved = (boolean) responsePostJson.get("image_approved");
                jodelPost.imageURL = (String) responsePostJson.get("image_url");
                jodelPost.thumbnailURL = (String) responsePostJson.get("thumbnail_url");
            } catch (Exception e) {
            }
            try {
                jodelPost.fromHome = (boolean) responsePostJson.get("from_home");
            } catch (Exception e) {
            }
            try {
                jodelPost.childCount = (long) responsePostJson.get("child_count");
            } catch (Exception e) {
            }
            jodelPost.replier = (long) responsePostJson.get("replier");
            jodelPost.postID = (String) responsePostJson.get("post_id");
            jodelPost.discoveredBy = (long) responsePostJson.get("discovered_by");
            jodelPost.voteCount = (long) responsePostJson.get("vote_count");
            jodelPost.shareCount = (long) responsePostJson.get("share_count");
            jodelPost.userHandle = (String) responsePostJson.get("user_handle");
            jodelPost.postOwn = (String) responsePostJson.get("post_own");
            jodelPost.distance = (long) responsePostJson.get("distance");

            JSONArray jodelsRepliesArray = (JSONArray) responsePostJson.get("children");

            Iterator i = jodelsRepliesArray.iterator();

            while (i.hasNext()) {
                JSONObject jodelReply = (JSONObject) i.next();
                JodelPostReply jodelPostReply = new JodelPostReply();
                jodelPostReply.message = (String) jodelReply.get("message");
                jodelPostReply.createdAt = (String) jodelReply.get("created_at");
                jodelPostReply.updatedAt = (String) jodelReply.get("updated_at");
                jodelPostReply.color = (String) jodelReply.get("color");
                jodelPostReply.thanksCount = (long) jodelReply.get("thanks_count");
                jodelPostReply.postID = (String) jodelReply.get("post_id");
                try {
                    jodelPostReply.imageApproved = (boolean) jodelReply.get("image_approved");
                    jodelPostReply.imageURL = (String) jodelReply.get("image_url");
                    jodelPostReply.thumbnailURL = (String) jodelReply.get("thumbnail_url");
                } catch (Exception e) {
                }
                jodelPostReply.discoveredBy = (long) jodelReply.get("discovered_by");
                jodelPostReply.voteCount = (long) jodelReply.get("vote_count");
                jodelPostReply.userHandle = (String) jodelReply.get("user_handle");
                jodelPostReply.postOwn = (String) jodelReply.get("post_own");
                jodelPostReply.distance = (long) jodelReply.get("distance");
                jodelPost.replies.add(jodelPostReply);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jodelPost;
    }

    /**
     * Parses a single Jodel (with replies) in V3 of API
     * @param jodelJSON String containing a JSON with a single jodel (from rawResponse)
     * @return An object of type JodelPost containing parsed Jodel with replies
     */
    public static JodelPost getParsedJodelV3(String jodelJSON) {
        JSONParser parserJodels = new JSONParser();
        JodelPost jodelPost = new JodelPost();
        try {
            JSONObject responsePostJson = (JSONObject) parserJodels.parse(jodelJSON);
            JSONObject postContentJson = (JSONObject) responsePostJson.get("details");

            jodelPost.message = (String) postContentJson.get("message");
            jodelPost.createdAt = (String) postContentJson.get("created_at");
            jodelPost.updatedAt = (String) postContentJson.get("updated_at");
            jodelPost.pinCount = (long) postContentJson.get("pin_count");
            jodelPost.color = (String) postContentJson.get("color");
            jodelPost.gotThanks = (boolean) postContentJson.get("got_thanks");
            try {
                jodelPost.imageApproved = (boolean) postContentJson.get("image_approved");
                jodelPost.imageURL = (String) postContentJson.get("image_url");
                jodelPost.thumbnailURL = (String) postContentJson.get("thumbnail_url");
            } catch (Exception e) {
            }
            try {
                jodelPost.fromHome = (boolean) postContentJson.get("from_home");
            } catch (Exception e) {
            }
            try {
                jodelPost.childCount = (long) postContentJson.get("child_count");
            } catch (Exception e) {
            }
            jodelPost.replier = (long) postContentJson.get("replier");
            jodelPost.postID = (String) postContentJson.get("post_id");
            jodelPost.discoveredBy = (long) postContentJson.get("discovered_by");
            jodelPost.voteCount = (long) postContentJson.get("vote_count");
            jodelPost.shareCount = (long) postContentJson.get("share_count");
            jodelPost.userHandle = (String) postContentJson.get("user_handle");
            jodelPost.postOwn = (String) postContentJson.get("post_own");
            jodelPost.distance = (long) postContentJson.get("distance");

            JSONArray jodelsRepliesArray = (JSONArray) responsePostJson.get("replies");

            Iterator i = jodelsRepliesArray.iterator();

            while (i.hasNext()) {
                JSONObject jodelReply = (JSONObject) i.next();
                JodelPostReply jodelPostReply = new JodelPostReply();
                jodelPostReply.message = (String) jodelReply.get("message");
                jodelPostReply.createdAt = (String) jodelReply.get("created_at");
                jodelPostReply.updatedAt = (String) jodelReply.get("updated_at");
                jodelPostReply.pinCount = (long) jodelReply.get("pin_count");
                jodelPostReply.color = (String) jodelReply.get("color");
                jodelPostReply.gotThanks = (boolean) jodelReply.get("got_thanks");
                jodelPostReply.thanksCount = (long) jodelReply.get("thanks_count");
                jodelPostReply.childCount = (long) jodelReply.get("child_count");
                jodelPostReply.replier = (long) jodelReply.get("replier");
                jodelPostReply.postID = (String) jodelReply.get("post_id");
                try {
                    jodelPostReply.imageApproved = (boolean) jodelReply.get("image_approved");
                    jodelPostReply.imageURL = (String) jodelReply.get("image_url");
                    jodelPostReply.thumbnailURL = (String) jodelReply.get("thumbnail_url");
                } catch (Exception e) {
                }
                try {
                    jodelPostReply.fromHome = (boolean) jodelReply.get("from_home");
                } catch (Exception e) {
                }
                jodelPostReply.discoveredBy = (long) jodelReply.get("discovered_by");
                jodelPostReply.voteCount = (long) jodelReply.get("vote_count");
                jodelPostReply.userHandle = (String) jodelReply.get("user_handle");
                jodelPostReply.postOwn = (String) jodelReply.get("post_own");
                jodelPostReply.distance = (long) jodelReply.get("distance");
                jodelPost.replies.add(jodelPostReply);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jodelPost;
    }

    /**
     * Parses a list of Jodel-notifications
     * @param notificationsJSON String containing a JSON with notifications
     * @return A list of objects from type JodelNotification
     */
    public static List<JodelNotification> getParsedNotifications(String notificationsJSON) {
        List<JodelNotification> jodelNotifications = new ArrayList<JodelNotification>();
        JSONParser parser = new JSONParser();
        try {
            JSONObject responseJson = (JSONObject) parser.parse(notificationsJSON);
            JSONArray jodelsNotificationsArray = (JSONArray) responseJson.get("notifications");

            Iterator i = jodelsNotificationsArray.iterator();
            while (i.hasNext()) {
                JSONObject notification = (JSONObject) i.next();
                JodelNotification jodelNotification = new JodelNotification();
                jodelNotification.postID = (String) notification.get("post_id");
                jodelNotification.type = (String) notification.get("type");
                jodelNotification.userID = (String) notification.get("user_id");
                jodelNotification.message = (String) notification.get("message");
                if (jodelNotification.type.equals("vote_post")) {
                    jodelNotification.voteCount = (long) notification.get("vote_count");
                }
                ;
                jodelNotification.scroll = (String) notification.get("scroll");
                jodelNotification.lastInteraction = (String) notification.get("last_interaction");
                jodelNotification.read = (boolean) notification.get("read");
                jodelNotification.seen = (boolean) notification.get("seen");
                jodelNotification.color = (String) notification.get("color");
                jodelNotification.notificationID = (String) notification.get("notification_id");

                jodelNotifications.add(jodelNotification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jodelNotifications;
    }
}
