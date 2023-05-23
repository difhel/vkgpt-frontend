package im.sdf.vkgpt.models;

//import android.util.Log;

import android.util.Pair;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

import im.sdf.vkgpt.helpers.VKUtils;

public class Conversations {
    @SerializedName("response")
    public List<ResponseItem> response;

    @SerializedName("error")
    public VKAPIError error;

    public Boolean isSuccessful() {
        return error == null && response != null;
    }

    public String getErrorMessage() {
        return error != null ? String.format("%d %s", error.errorCode, error.errorDescription) : "Cannot get error";
    }
    public class ResponseItem {
        @SerializedName("peer_id")
        public Integer peerId;

        @SerializedName("name")
        public String name;

        @SerializedName("photo50")
        public String photo50;

        @SerializedName("unread_count")
        public Integer unreadCount;

        @SerializedName("last_message")
        public Message lastMessage;

        public String getName(){
            return name != null ? name : "";
        }

        public Pair<String, Pair<String, String>> getText(){
            return lastMessage != null ? lastMessage.getText() : new Pair<>("CHAT_STARTED", new Pair<>("", ""));
        }
        public String getAvatar() {
            return photo50 != null ? photo50 : "https://vk.com/images/camera_50.png";
        }
        public int getUnreadCount() {
            return unreadCount != null ? unreadCount : 0;
        }
    }

    public class Message {
        @SerializedName("date")
        public Integer date;

        @SerializedName("from_id")
        public Integer fromId;

        @SerializedName("from_name")
        public String fromName;

        @SerializedName("peer_id")
        public Integer peerId;

        @SerializedName("id")
        public Integer id;

        @SerializedName("text")
        public String text;

        @SerializedName("reply_message")
        public Message replyMessage;

        @SerializedName("attachments")
        public List<Attachment> attachments;

        public String getFromName() {
            return fromName != null && !fromName.equals("") ? fromName : "UNSUPPORTEDJAVA";
        }


        public Pair<String, Pair<String, String>> getText() {
            // returns pair of <AttachmentType, <Text, Handle>>
            // types of attachments: PLAIN, PHOTO, DOC, OTHER
            if (text != null && !text.equals("")) {
                return new Pair<>("PLAIN", new Pair<>(text, getFromName() + ": "));
            }
            else if (attachments.size() > 0) {
                Pair<String, Pair<String, String>> result = new Pair<>("", new Pair<>("", ""));
                if (attachments.size() > 1) {
                    return new Pair<>("OTHER", new Pair<>("", getFromName() + ": "));
                }
                Attachment attachment = attachments.get(0);
                // TODO: write a custom logic to handle each type of the attachments
                switch (attachment.type) {
                    case ("sticker"):
                        result = new Pair<>("STICKER", new Pair<>("", getFromName() + ": "));
                        break;
                    case ("photo"):
                        result = new Pair<>("PHOTO", new Pair<>("", getFromName() + ": "));
                        break;
                    default:
                        result = new Pair<>("DOC", new Pair<>("", getFromName() + ": "));
                        break;
                }
                return result;
            }
            return new Pair<>("PLAIN", new Pair<>("", getFromName() + ": "));
        }
    }

}

