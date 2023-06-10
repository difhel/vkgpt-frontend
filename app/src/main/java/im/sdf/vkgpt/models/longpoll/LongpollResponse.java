package im.sdf.vkgpt.models.longpoll;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LongpollResponse {
    @SerializedName("ts")
    public Integer ts;

    @SerializedName("updates")
    public List<List<Object>> updates;

    @SerializedName("failed")
    public Integer failed;

    public Boolean isSuccessful() {
        return updates != null && failed == null;
    }

    public String getErrorMessage() {
        return failed != null ? String.valueOf(failed) : "Cannot get error";
    }
}