package im.sdf.vkgpt.models;

import com.google.gson.annotations.SerializedName;

public class AuthResponseSuccess {
    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("expires_in")
    public int expiresIn;

    @SerializedName("user_id")
    public int userId;

    // Add any other fields from the response that you need to use
}
