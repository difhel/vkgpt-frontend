package im.sdf.vkgpt.models;

//import android.util.Log;

import android.util.Pair;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Messages {
    @SerializedName("response")
    public List<Message> response;

    @SerializedName("error")
    public VKAPIError error;

    public Boolean isSuccessful() {
        return error == null && response != null;
    }

    public String getErrorMessage() {
        return error != null ? String.format("%d %s", error.errorCode, error.errorDescription) : "Cannot get error";
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

        @SerializedName("photo50")
        public String photo50;

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

        public String getAvatar() {
            return photo50 != null ? photo50 : "";
        }
    }

}

