package im.sdf.vkgpt.models;

import com.google.gson.annotations.SerializedName;

// https://dev.vk.com/method/users.get

public class User {
    @SerializedName("id")
    public int id;

    @SerializedName("first_name")
    public String firstName;

    @SerializedName("last_name")
    public String lastName;

    @SerializedName("can_access_closed")
    public boolean canAccessClosed;

    @SerializedName("is_closed")
    public boolean isClosed;

    @SerializedName("verified")
    public boolean verified;

    @SerializedName("screen_name")
    public String screenName;

    @SerializedName("last_seen")
    public String lastSeen;

    @SerializedName("photo_50")
    public String photo50;

    @SerializedName("photo_100")
    public String photo100;
}