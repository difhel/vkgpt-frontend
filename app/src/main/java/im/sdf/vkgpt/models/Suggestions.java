package im.sdf.vkgpt.models;

//import android.util.Log;

import android.util.Pair;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Suggestions {
    @SerializedName("ok")
    public Boolean ok;

    @SerializedName("exception")
    public String exception;

    @SerializedName("suggestions")
    public List<String> suggestions;

    public Boolean isSuccessful() {
        return ok.equals(Boolean.TRUE) && suggestions != null;
    }

    public String isOk() {
        if (ok == null) {
            return "null";
        }
        if (Boolean.TRUE.equals(ok)) {
            return "true";
        }
        return "false";
    }

    public String safeSuggestions() {
        if (suggestions == null) {
            return "null";
        }
        if (suggestions.size() > 0) {
            return suggestions.get(0);
        }
        return "empty";
    }

    public String getErrorMessage() {
        return exception != null ? exception : "Cannot get error";
    }

}

