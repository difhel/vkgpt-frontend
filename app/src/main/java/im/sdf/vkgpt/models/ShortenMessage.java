package im.sdf.vkgpt.models;

//import android.util.Log;

import com.google.gson.annotations.SerializedName;


public class ShortenMessage {
    @SerializedName("from_name")
    public String fromName;

    @SerializedName("text")
    public String text;

    public ShortenMessage(String fromName, String text) {
        this.fromName = fromName;
        this.text = text;
    }

}


