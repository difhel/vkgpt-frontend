package im.sdf.vkgpt.models;

import com.google.gson.annotations.SerializedName;

public class VKAPIResponseError {
    @SerializedName("error")
    public String error;

    @SerializedName("error_description")
    public String errorDescription;

    public String errorDescription() {
        return errorDescription == null ? "" : errorDescription;
    }

    // Add any other fields from the response that you need to use
}
