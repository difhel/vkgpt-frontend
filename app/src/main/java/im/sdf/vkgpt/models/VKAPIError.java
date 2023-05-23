package im.sdf.vkgpt.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VKAPIError {
    @SerializedName("error_code")
    public int errorCode;

    @SerializedName("error_msg")
    public String errorDescription;

    @SerializedName("request_params")
    public List<VKAPIErrorParams> requestParams;

}

class VKAPIErrorParams {
    @SerializedName("key")
    public String key;

    @SerializedName("value")
    public String value;
}