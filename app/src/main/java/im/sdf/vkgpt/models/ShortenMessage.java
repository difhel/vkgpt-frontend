package im.sdf.vkgpt.models;

//import android.util.Log;

import android.util.Pair;

import com.google.gson.annotations.SerializedName;

import java.util.List;


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


