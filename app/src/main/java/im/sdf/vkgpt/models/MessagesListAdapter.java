package im.sdf.vkgpt.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import im.sdf.vkgpt.R;
import im.sdf.vkgpt.ViewChat;
import im.sdf.vkgpt.helpers.CircleTransform;

public class MessagesListAdapter extends RecyclerView.Adapter<MessagesListHolder> {
    List<Messages.Message> messageList;
    String accessToken;

    public MessagesListAdapter(List<Messages.Message> messageList) {
        this.messageList = messageList;
        this.accessToken = accessToken;
    }

    public void setChatList(List<Messages.Message> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessagesListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);
        return new MessagesListHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessagesListHolder holder, int position) {
        Integer userId = getUserId(holder.userName.getContext());
        Messages.Message message = messageList.get(position);
        String name = message.getFromName(); // name
        Pair<String, Pair<String, String>> _text = message.getText(); // text
        String text = _text.second.first;
        // <Type, <Text, Handle>>
        // How to get the text to show in preview: text.second.second + text.second.first
        // How to get the text to show in message: text.second.first
        String avatar = message.getAvatar(); // avatar getter
        Log.wtf("CHATDRAWER", String.format("Chat name: %s", name));
        holder.userName.setText(name);
        switch (_text.first) {
            case ("PLAIN"):
                holder.messageText.setText(text);
                break;
            case ("PHOTO"):
                holder.messageText.setText(text + holder.messageText.getContext().getString(R.string.photo));
                break;
            case ("STICKER"):
                holder.messageText.setText(text + holder.messageText.getContext().getString(R.string.sticker));
                break;
            case ("DOC"):
                holder.messageText.setText(text + holder.messageText.getContext().getString(R.string.doc));
                break;
            default:
                // all other types
                holder.messageText.setText(text + holder.messageText.getContext().getString(R.string.docs));
        }
//        Log.wtf("Attachment pair", String.format("Chat name: %s %s %s", name, text.first, text.second));
        if (message.attachments.size() > 0){
            Log.wtf("RealAttachmentFile", message.attachments.get(0).type);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.itemView.getContext(), "clicked on " + message.id, Toast.LENGTH_SHORT).show();
            }
        });
        if (message.fromId.equals(userId)){
            Log.wtf("EQUALS", message.fromId + " " + userId);
            holder.itemView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        Log.wtf("ACCESS", message.fromId + " " + userId);
        if (avatar != null && !avatar.isEmpty()) {
            Picasso.get().load(avatar).resizeDimen(R.dimen.message_avatar_size, R.dimen.message_avatar_size).transform(new CircleTransform()).into(holder.userAvatar);
        }
        else {
            Log.wtf("BrokenImageMsgListGen", String.format("Broken image for %s", message.fromId));
        }
//        Log.d("ChatAT", accessToken);
//        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public Integer getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("VKGPT", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("user_id", -1);
    }
}

