package com.fr31b3u73r.jodel;

import java.util.ArrayList;
import java.util.List;

public class JodelPost {
    public String message = null;
    public String createdAt = null;
    public String updatedAt = null;
    public long pinCount = 0;
    public String color = null;
    public boolean gotThanks = false;
    public long thanksCount = 0;
    public boolean imageApproved = false;
    public long childCount = 0;
    public long replier = 0;
    public String postID = null;
    public long discoveredBy = 0;
    public long voteCount = 0;
    public long shareCount = 0;
    public String userHandle = null;
    public String postOwn = null;
    public long distance = 0;
    public String imageURL = null;
    public String thumbnailURL = null;
    public boolean fromHome = false;
    public List<JodelPostReply> replies = new ArrayList<JodelPostReply>();
}
