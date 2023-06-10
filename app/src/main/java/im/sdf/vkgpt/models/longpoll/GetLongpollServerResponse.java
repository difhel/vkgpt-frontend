package im.sdf.vkgpt.models.longpoll;

import com.google.gson.annotations.SerializedName;

import im.sdf.vkgpt.models.VKAPIError;

public class GetLongpollServerResponse {
    @SerializedName("response")
    public LPResponseWrapper response;

    @SerializedName("error")
    public VKAPIError error;

    public Boolean isSuccessful() {
        return error == null && response != null;
    }

    public String getErrorMessage() {
        return error != null ? String.format("%d %s", error.errorCode, error.errorDescription) : "Cannot get error";
    }

    public class LPResponseWrapper {
        @SerializedName("server")
        public String server;

        @SerializedName("key")
        public String key;

        @SerializedName("ts")
        public Integer ts;
    }
}