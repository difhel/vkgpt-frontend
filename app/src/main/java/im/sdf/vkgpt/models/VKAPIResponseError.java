package im.sdf.vkgpt.models;

import com.google.gson.annotations.SerializedName;

public class VKAPIResponseError {
    @SerializedName("error")
    public String error;

    @SerializedName("error_description")
    public String errorDescription;

    @SerializedName("captcha_img")
    public String captchaImg;

    @SerializedName("captcha_sid")
    public String captchaSid;

    public String errorDescription() {
        return errorDescription == null ? "" : errorDescription;
    }

    // Add any other fields from the response that you need to use
}
