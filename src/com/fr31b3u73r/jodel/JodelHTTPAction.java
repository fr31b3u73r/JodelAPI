package com.fr31b3u73r.jodel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JodelHTTPAction {
	JSONObject locationObject = new JSONObject();
	String accessToken = null;
	String deviceUID = null;
	String expirationDate = null;
    String distinctID = null;
    String refreshToken = null;
    String latitude = null;
    String longitude = null;
	
	protected JodelHTTPAction() {
	}
	
	protected void updateAccessValues(JSONObject locationObject, String accessToken, String distinctID, String refreshToken, String deviceUID, String expirationDate, String latitude, String longitude) {
		this.locationObject = locationObject;
		this.accessToken = accessToken;
		this.deviceUID = deviceUID;
		this.expirationDate = expirationDate;
		this.distinctID = distinctID;
		this.refreshToken = refreshToken;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	protected JodelHTTPResponse setLocation() {
		JSONObject payload = new JSONObject();
		payload.put("location", this.locationObject);
		return this.sendRequest("PUT", "/v2/users/location", payload);
	}

	protected JodelHTTPResponse setPin(String postID) {
		return this.sendRequest("PUT", "/v2/posts/" + postID + "/pin", null);
	}

	protected JodelHTTPResponse setNotificationReadPostID(String postID) {
		return this.sendRequest("PUT", "/v3/user/notifications/post/" + postID + "/read", null);
	}

	protected JodelHTTPResponse setNotificationReadNotificationID(String notificationID) {
		return this.sendRequest("PUT", "/v3/user/notifications/" + notificationID + "/read", null);
	}

	protected JodelHTTPResponse setUserProfile(String userType, String gender, int age) {
		JSONObject payload = new JSONObject();
		payload.put("user_type", userType);
		payload.put("gender", gender);
		payload.put("age", age);
		return this.sendRequest("PUT", "/v3/user/profile", payload);
	}

	protected JodelHTTPResponse removePin(String postID) {
		return this.sendRequest("PUT", "/v2/posts/" + postID + "/unpin", null);
	}

	protected JodelHTTPResponse removeJodel(String postID) {
		return this.sendRequest("DELETE", "/v2/posts/" + postID, null);
	}

	protected JodelHTTPResponse enableNotify(String postID) {
		return this.sendRequest("PUT", "/v2/posts/" + postID + "/notifications/enable", null);
	}

	protected JodelHTTPResponse disableNotify(String postID) {
		return this.sendRequest("PUT", "/v2/posts/" + postID + "/notifications/disable", null);
	}
	
	protected JodelHTTPResponse getUserConfig() {
		return this.sendRequest("GET", "/v3/user/config", null);
	}
	
	protected JodelHTTPResponse getNewAccessToken() {
		JSONObject payload = new JSONObject();
		payload.put("client_id", JodelAccount.CLIENT_ID);
		payload.put("distinct_id", this.distinctID);
		payload.put("refresh_token", this.refreshToken);
		return this.sendRequest("POST", "/v2/users/refreshToken", payload);
	}
	
	protected JodelHTTPResponse getNewTokens() {
		JSONObject payload = new JSONObject();
		payload.put("device_uid", this.deviceUID);
		payload.put("location", this.locationObject);
		payload.put("client_id", JodelAccount.CLIENT_ID);
		return this.sendRequest("POST", "/v2/users", payload);
	}
	
	protected JodelHTTPResponse getCaptcha() {
		return this.sendRequest("GET", "/v3/user/verification/imageCaptcha", null);
	}

	protected JodelHTTPResponse getNotifications() {
		return this.sendRequest("PUT", "/v3/user/notifications", null);
	}

	protected JodelHTTPResponse getNotificationsNew() {
		return this.sendRequest("GET", "/v3/user/notifications/new", null);
	}
	
	protected JodelHTTPResponse getJodels(String url, int skip, int limit, String after, String hashtag, String channel) {
		JSONObject parameters = new JSONObject();
		parameters.put("lat", this.latitude);
		parameters.put("lng", this.longitude);
		parameters.put("skip", skip);
		parameters.put("limit", limit);
		if (hashtag != null) {
			parameters.put("hashtag", hashtag);
		}
		if (channel != null) {
			parameters.put("channel", channel);
		}
		if (after != null) {
			parameters.put("after", after);
		}
		
		return this.sendRequest("GET", url, parameters, null);
	}
	
	protected JodelHTTPResponse getJodel(String postID) {
		return this.sendRequest("GET", "/v2/posts/" + postID, null);
	}

	protected JodelHTTPResponse getJodelV3(String postID, int skip) {
		JSONObject parameters = new JSONObject();
		parameters.put("details", "true");
		parameters.put("reply", skip);
		return this.sendRequest("GET", "/v3/posts/" + postID + "/details", parameters, null);
	}

	protected JodelHTTPResponse getJodelShareURL(String postID) {
        return this.sendRequest("POST", "/v3/posts/"+postID+"/share", null);
    }

	protected JodelHTTPResponse getRecommendedChannels() {
		return this.sendRequest("GET", "/v3/user/recommendedChannels", null);
	}

	protected JodelHTTPResponse getChannelMeta(String channel) {
		JSONObject parameters = new JSONObject();
		parameters.put("channel", channel);
		return this.sendRequest("GET", "/v3/user/channelMeta", parameters, null);
	}

	protected  JodelHTTPResponse getKarma() {
		return this.sendRequest("GET", "/v2/users/karma", null);
	}

	protected JodelHTTPResponse followChannel(String channel) {
		JSONObject parameters = new JSONObject();
		parameters.put("channel", channel);
		return this.sendRequest("PUT", "/v3/user/followChannel", parameters, null);
	}

	protected JodelHTTPResponse unfollowChannel(String channel) {
		JSONObject parameters = new JSONObject();
		parameters.put("channel", channel);
		return this.sendRequest("PUT", "/v3/user/unfollowChannel", parameters, null);
	}

	protected JodelHTTPResponse submitCaptcha(String captchaKey, List<Integer> answer) {
		JSONArray answerArr = new JSONArray();
		for(int answerPosition: answer)
		{
			answerArr.add(answerPosition);
		}
		JSONObject payload = new JSONObject();
		payload.put("key", captchaKey);
		payload.put("answer", answerArr);
		return this.sendRequest("POST", "/v3/user/verification/imageCaptcha", payload);
	}
	
	protected JodelHTTPResponse submitPost(String message, String base64Image, String color, int ancestor, String channel) {
		JSONObject payload = new JSONObject();
		if (message != null) {
			payload.put("message", message);
		}
		if (base64Image != null) {
			payload.put("image", base64Image);
		}
		if (color != null) {
			payload.put("color", color);
		} else {
			String postColor = JodelHelper.getRandomColor();
			payload.put("color", postColor);
		}
		if (ancestor != 0) {
			payload.put("ancestor", ancestor);
		}
		if (channel != null) {
			payload.put("channel", channel);
		}
		payload.put("location", this.locationObject);
		return this.sendRequest("POST", "/v3/posts/", payload);
	}

	protected JodelHTTPResponse performUpvote(String postID) {
		return this.sendRequest("PUT", "/v2/posts/"+postID+"/upvote/", null);
	}

	protected JodelHTTPResponse performDownvote(String postID) {
		return this.sendRequest("PUT", "/v2/posts/"+postID+"/downvote/", null);
	}

	protected JodelHTTPResponse performUpvoteSticky(String postID) {
		return this.sendRequest("PUT", "/v3/stickyposts/"+postID+"/up", null);
	}

	protected JodelHTTPResponse performDownvoteSticky(String postID) {
		return this.sendRequest("PUT", "/v3/stickyposts/"+postID+"/down", null);
	}

	protected JodelHTTPResponse performDismissSticky(String postID) {
		return this.sendRequest("PUT", "/v3/stickyposts/"+postID+"/dismiss", null);
	}

	protected JodelHTTPResponse performThank(String postID) {
		return this.sendRequest("POST", "/v3/posts/"+postID+"/giveThanks", null);
	}
	
	
	private JodelHTTPResponse sendRequest (String method, String endpoint, JSONObject params, JSONObject payload) {
		JodelHTTPResponse httpResponse =  new JodelHTTPResponse();
		String fullUrl = JodelAccount.API_URL + endpoint;
		if (params != null) {
			fullUrl += "?";
			for (Object key : params.keySet()) {
				String paramString = "";
		        //based on you key types
		        String keyStr = (String)key;
		        Object keyVal = params.get(keyStr);

		        paramString += keyStr + "=" + keyVal;
		        fullUrl += paramString + "&";
		    }
			fullUrl = fullUrl.substring(0, fullUrl.length()-1);
		}
		HttpURLConnection connection = null;
		
		try {
			URL url = new URL(fullUrl);
			connection = (HttpURLConnection) url.openConnection();
			if (!method.equals("GET")) {
				connection.setDoOutput(true);
			}
			connection.setRequestMethod(method);
			connection.setRequestProperty("User-Agent", "Jodel/" + JodelAccount.VERSION + " Dalvik/2.1.0 (Linux; U; Android 5.1.1; )");
			connection.setRequestProperty("Accept-Encoding", "gzip");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			if (this.accessToken != null) {
				connection.setRequestProperty("Authorization", "Bearer " + this.accessToken);
			}
			
			// perform signing
			Map<String, String> requestSignProperties = getRequestSignProperties(method, url, params, payload);
			String xAuthValue = requestSignProperties.get("X-Authorization");
			connection.setRequestProperty("X-Authorization", xAuthValue);
			String clientValue = requestSignProperties.get("X-Client-Type");
			connection.setRequestProperty("X-Client-Type", clientValue);
			String timestampValue = requestSignProperties.get("X-Timestamp");
			connection.setRequestProperty("X-Timestamp", timestampValue);
			String apiVersionValue = requestSignProperties.get("X-Api-Version");
			connection.setRequestProperty("X-Api-Version", apiVersionValue);
			
			
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.connect();
			
			if (!method.equals("GET")) {
				OutputStream os = connection.getOutputStream();
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
				if (payload != null) {
					pw.write(payload.toString().replace(":", ": ").replace(",", ", "));
				}
				pw.close();
			} else {
				
			}
			
			int responseCode = connection.getResponseCode();
			
			if (responseCode == 200) {
				BufferedReader rd  = null;
				StringBuilder sb = null;
				String line = null;
				rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			    sb = new StringBuilder();
		
			    while ((line = rd.readLine()) != null){
			    	sb.append(line + '\n');
			    }
			    String responseJsonString = sb.toString();
			    httpResponse.responseCode = responseCode;
			    httpResponse.responseMessage = responseJsonString;
			} else {
				httpResponse.responseCode = responseCode;
			}
		} catch (Exception e) {
		}
		return httpResponse;
	}
	
	private JodelHTTPResponse sendRequest (String method, String endpoint, JSONObject payload) {
		return this.sendRequest(method, endpoint, null, payload);
	}
	
	private Map<String, String> getRequestSignProperties (String method, URL url, JSONObject params, JSONObject payload) {
		Map<String, String> requestSignProperties = new HashMap<String, String>();
		
		String timestamp = Instant.now().toString().substring(0, 19) + "Z";
		
		List<String> req = new ArrayList<String>();
		req.add(method);
		req.add(url.getAuthority());
		req.add("443");
		req.add(url.getPath());
		if (this.accessToken != null) {
			req.add(this.accessToken);
		} else {
			req.add("");
		}
		req.add(timestamp);

		if (params != null) {
			List<String> paramsList = new ArrayList<String>();
			for (Object key : params.keySet()) {
				String paramString = "";
		        //based on you key types
		        String keyStr = (String)key;
		        Object keyVal = params.get(keyStr);

		        paramString += keyStr + "%" + keyVal;
		        paramsList.add(paramString);
		    }
			req.add(String.join("%", paramsList));
		} else {
			req.add("");
		}
		
		if (payload != null) {
			String payloadString = payload.toString().replace(":", ": ").replace(",", ", ");
			req.add(payloadString);
		} else {
			req.add("");
		}
		
		String hmacData = String.join("%", req);
		String signature = null;
		try {
			signature = JodelHelper.calculateHMAC(JodelAccount.SECRET, hmacData);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		requestSignProperties.put("X-Authorization", "HMAC " + signature);
		requestSignProperties.put("X-Client-Type","android_" + JodelAccount.VERSION);
		requestSignProperties.put("X-Timestamp", timestamp);
		requestSignProperties.put("X-Api-Version", "0.2");
		
		return requestSignProperties;
	}
}
