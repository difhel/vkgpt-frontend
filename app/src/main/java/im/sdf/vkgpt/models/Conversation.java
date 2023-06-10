package im.sdf.vkgpt.models;


import android.util.Pair;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Conversation {
    @SerializedName("response")
    public Conversations.ResponseItem response;

    @SerializedName("error")
    public VKAPIError error;

    public Boolean isSuccessful() {
        return error == null && response != null;
    }

    public String getErrorMessage() {
        return error != null ? String.format("%d %s", error.errorCode, error.errorDescription) : "Cannot get error";
    }
}

