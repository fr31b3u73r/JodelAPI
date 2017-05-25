import java.util.ArrayList;
import java.util.List;

import com.fr31b3u73r.jodel.*;

public class ExampleImplementation {
	public static void main(String[] args) {
		// create a new Jodel account
        JodelAccount ja = new JodelAccount("48.148434", "11.567867", "Munich", "DE", "Munich");

		// you can get your account data at any time as an object of type JodelAccountData
		// this information can be stored and used in the constructor of JodelAccount
		JodelAccountData myAccountData = ja.getAccountData();
		
		// retrieve the captcha to verify it
		JodelRequestResponse captchaResponse = ja.getCaptchaData();
		String captchaKey = captchaResponse.responseValues.get("captchaKey");
		String captchaURL = captchaResponse.responseValues.get("captchaUrl");
		
		// then open URL in Browser and adjust the list of positions (starting with 0 from left to right)
		List<Integer> positions = new ArrayList<Integer>();
		positions.add(2);
		positions.add(4);
		positions.add(8);
	
		// verify the captcha
		JodelRequestResponse verifyCaptcha = ja.verifyCaptcha(captchaKey, positions);
		
		// to post a new Jodel use the createPost() method
		// to set a color you can use the class JodelPostColor; if null is set here a random color is chosen
		// to submit an image you have to base64 encode it an submit it as the second parameter
		JodelRequestResponse createResponse = ja.createPost("Ich bin eine Münchner Ampel", null, JodelPostColor.RED, null);
		
		// to retrieve a list of Jodels use a function matching your criteria (see JodelAccount class)
		JodelRequestResponse getPopularPictures = ja.getPicturesPopular(0, 10, null);
		// to get the data select the jodelPosts parameter of JodelRequestResponse which is a list of type JodelPost
		List<JodelPost> retrievedPosts = JodelParser.getParsedJodels(getPopularPictures.rawResponseMessage);

		// gets recommended channels
		JodelRequestResponse recommendedChannels = ja.getRecommendedChannels();

		// follows / unfollows a channel
		JodelRequestResponse followChannel = ja.followChannel("selfies");
		JodelRequestResponse unfollowChannel = ja.unfollowChannel("selfies");

		// sets user details
		JodelRequestResponse setUser = ja.setUserProfile(JodelUsertype.STUDENT, "f", 21);

		// gets your current karma
		JodelRequestResponse myKarma = ja.getKarma();
		String karma = myKarma.responseValues.get("karma");
	}

}
