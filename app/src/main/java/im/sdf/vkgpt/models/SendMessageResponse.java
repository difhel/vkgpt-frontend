package im.sdf.vkgpt.models;

import com.google.gson.annotations.SerializedName;

public class SendMessageResponse {
    @SerializedName("response")
    public Integer response;

    @SerializedName("error")
    public VKAPIError error;

    public String getErrorMessage() {
        return error != null ? String.format("%d %s", error.errorCode, error.errorDescription) : "Cannot get error";
    }

    public Boolean isSuccessful() {
        return response != null && error == null;
    }
}
