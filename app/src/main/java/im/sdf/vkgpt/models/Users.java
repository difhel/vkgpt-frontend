package im.sdf.vkgpt.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Users {
    @SerializedName("response")
    public List<User> response;

    @SerializedName("error")
    public VKAPIError error;

    public Boolean isSuccessful() {
        return error == null && response != null;
    }

    public String getErrorMessage() {
        return error != null ? String.format("%d %s", error.errorCode, error.errorDescription) : "Cannot get error";
    }
}

