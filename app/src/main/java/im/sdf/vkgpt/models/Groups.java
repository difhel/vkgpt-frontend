package im.sdf.vkgpt.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Groups {
    @SerializedName("response")
    public List<Group> response;

    @SerializedName("error")
    public VKAPIError error;

    public Boolean isSuccessful() {
        return error == null && response != null;
    }

    public String getErrorMessage() {
        return error != null ? String.format("%d %s", error.errorCode, error.errorDescription) : "Cannot get error";
    }

    public class Group {
        @SerializedName("id")
        public int id;

        @SerializedName("name")
        public String name;

        @SerializedName("photo_50")
        public String photo50;
    }
}

