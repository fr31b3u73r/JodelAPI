# JodelAPI
[![Implementation](https://img.shields.io/badge/api--version-4.45.2-brightgreen.svg)]() [![Java](https://img.shields.io/badge/java-8-brightgreen.svg)]() [![Donate](https://img.shields.io/badge/donate-PayPal-brightgreen.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=5654A67GA3GHA)

*This is an inofficial library to interact with the private API of the Jodel App. Not affiliated with The Jodel Venture GmbH. API can change at any time so this library might not work in the future.*

## Usage
Have a look in the examples folder and the source of the library.

It is mainly dependent on the [Python Jodel API of nborrmann](https://github.com/nborrmann/jodel_api). There are some classes which contain helpers and parses to simplify interacting with the API.

### Examples

#### Creating an account
To create a new account first add the library to your project. You can use the source or compile the source to a .jar file and add it to your project.

Add this line to the class you want to use the Jodel library:
```java
import com.fr31b3u73r.jodel.*;
```

To create a new account use the constructor of class ```JodelAccount``` with your location and set all other values to ```null```:
```java
JodelAccount ja = new JodelAccount("48.148434", "11.567867", "Munich", "DE", "Munich", false, null, null, null, null, null);
```

This is all you have to do, your account will then be set up. To retrieve your account data simply call ```getAccountData()``` which returns an object of type ```JodelAccountData``` with all needed information to use your account again later.

#### Verify account
To be able to use most functions of your generated account you need to verify it. This is done by an image captcha. You can retrieve image url of this captcha and key simply by using the following function:
```java
JodelRequestResponse captchaResponse = ja.getCaptchaData();
String captchaKey = captchaResponse.responseValues.get("captchaKey");
String captchaURL = captchaResponse.responseValues.get("captchaUrl");
```

To solve the captcha display the image and add the positions of images with a racoon to a list (starting from left to right beginning with 0).

Example:
```java
List<Integer> positions = new ArrayList<Integer>();
positions.add(2);
positions.add(4);
positions.add(8);
```

To verify the captcha simply call ```verifyCaptcha``` with key and the list of positions:
```java
JodelRequestResponse verifyCaptcha = ja.verifyCaptcha(captchaKey, positions);
```


#### Create a new Jodel
Creating a new Jodel with a verified account is simple. Just use ```createPost``` to do so:
```java
JodelRequestResponse createResponse = ja.createPost("Ich bin eine Münchner Ampel", null, JodelPostColor.RED, null);
```
The first parameter is the message, in the second parameter you can pass a base64 encoded image, the third parameter is the color (as defined in Class ```JodelPostColor```) and the last parameter defines the channel (use null if you don´ want to post to a chanel).


#### Retrieve Jodels
To retrieve Jodels from other users you can use one of the functions in ```JodelAccount```:
* getPostsRecent
* getPostsPopular
* getPostsDiscussed
* getPicturesRecent
* getPicturesPopular
* getPicturesDiscussed
* getMyPinnedPosts
* getMyRepliedPosts
* getMyVotedPosts
Have a look into source to see which parameters can be used calling those functions.

Here is an example:
```java
JodelRequestResponse getPopularPictures = ja.getPicturesPopular(0, 10, null);
```
It gets the 10 most popular pictures of your current location. The response is of Type ```JodelRequestResponse```. To parse this answer use the according parser as defined in "JodelParser":
```java
List<JodelPost> retrievedPosts = JodelParser.getParsedJodels(getPopularPictures.rawResponseMessage);
```

There are also methods retrieving single Jodel posts, your norifications, recommended channels etc.. Have a look into source to see how to use them.

#### Mixed functions
Here are some functions that might also be interesting for you:
##### Follow a channel
```java
JodelRequestResponse followChannel = ja.followChannel("selfies");
```
##### Unfollow a channel
```java
JodelRequestResponse unfollowChannel = ja.unfollowChannel("selfies");
```
##### Upvote a Jodel
```java
JodelRequestResponse upvoteJodel = ja.upvoteJodel("abc123");
```
##### Downvote a Jodel
```java
JodelRequestResponse downvoteJodel = ja.downvoteJodel("abc123");
```
##### Get your current karma
```java
JodelRequestResponse myKarma = ja.getKarma();
String karma = myKarma.responseValues.get("karma");
```
...and many more

Feel free to commit changes and additions like missing methods, tests, docs etc.!
If you encounter any problem don´t hesitate to open an issue - not all methods might be tested properly and bugs can occur at any time.