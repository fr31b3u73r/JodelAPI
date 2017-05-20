package com.fr31b3u73r.jodel;

import java.util.HashMap;
import java.util.Map;

public class JodelRequestResponse {
    public boolean error = false;
    public int httpResponseCode = 0;
    public String rawResponseMessage = "";
    public Map<String, String> responseValues = new HashMap<String, String>();
    public String errorMessage = "";
    public String rawErrorMessage = "";
}
