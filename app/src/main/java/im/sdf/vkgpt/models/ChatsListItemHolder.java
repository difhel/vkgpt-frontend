package im.sdf.vkgpt.models;

import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import im.sdf.vkgpt.R;

public class ChatsListItemHolder extends RecyclerView.ViewHolder {
    TextView chatName;
    TextView chatText;

    ImageView chatAvatar;

    TextView chatUnreadCount;

    public ChatsListItemHolder(@NonNull View itemView) {
        super(itemView);
        chatName = itemView.findViewById(R.id.chat_name);
        chatText = itemView.findViewById(R.id.chat_text);
        chatAvatar = itemView.findViewById(R.id.chat_avatar);
        chatUnreadCount = itemView.findViewById(R.id.unread_counter);

    }
}