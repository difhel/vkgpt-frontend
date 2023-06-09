package im.sdf.vkgpt.models;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import im.sdf.vkgpt.R;
import im.sdf.vkgpt.ViewChat;
import im.sdf.vkgpt.helpers.CircleTransform;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListItemHolder> {
    List<Conversations.ResponseItem> chatList;

    public ChatsListAdapter(List<Conversations.ResponseItem> chatList) {
        this.chatList = chatList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setChatList(List<Conversations.ResponseItem> chatList) {
        this.chatList = chatList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatsListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_list_item, parent, false);
        return new ChatsListItemHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChatsListItemHolder holder, int position) {
        Conversations.ResponseItem chat = chatList.get(position);
        String name = chat.getName(); // name
        Pair<String, Pair<String, String>> _text = chat.getText(); // text
        String text = _text.second.second + _text.second.first;
        // <Type, <Text, Handle>>
        // How to get the text to show in preview: text.second.second + text.second.first
        // How to get the text to show in message: text.second.first
        int unreadCount = chat.getUnreadCount();
        String avatar = chat.getAvatar(); // avatar getter
        Log.wtf("CHATDRAWER", String.format("Chat name: %s", name));
        holder.chatName.setText(name);
        switch (_text.first) {
            case ("PLAIN"):
                holder.chatText.setText(text);
                break;
            case ("PHOTO"):
                holder.chatText.setText(text + holder.chatText.getContext().getString(R.string.photo));
                break;
            case ("STICKER"):
                holder.chatText.setText(text + holder.chatText.getContext().getString(R.string.sticker));
                break;
            case ("DOC"):
                holder.chatText.setText(text + holder.chatText.getContext().getString(R.string.doc));
                break;
            default:
                // all other types
                holder.chatText.setText(text + holder.chatText.getContext().getString(R.string.docs));
        }
        if (chat.lastMessage.attachments.size() > 0) {
            Log.wtf("RealAttachmentFile", chat.lastMessage.attachments.get(0).type);
        }

        if (unreadCount == 0) {
            holder.chatUnreadCount.setVisibility(View.GONE);
        } else {
            holder.chatUnreadCount.setText(String.valueOf(unreadCount));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.itemView.getContext(), "clicked on " + name, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(holder.itemView.getContext(), ViewChat.class);
                intent.putExtra("chat_name", name);
                intent.putExtra("peer_id", String.valueOf(chat.peerId));
                intent.putExtra("photo50", String.valueOf(chat.photo50));
                holder.itemView.getContext().startActivity(intent);
            }
        });
        Picasso.get().load(avatar).resizeDimen(R.dimen.chatslist_avatar_size, R.dimen.chatslist_avatar_size).transform(new CircleTransform()).into(holder.chatAvatar);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}

