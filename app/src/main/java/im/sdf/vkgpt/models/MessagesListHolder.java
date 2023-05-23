package im.sdf.vkgpt.models;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import im.sdf.vkgpt.R;

public class MessagesListHolder extends RecyclerView.ViewHolder {
    TextView userName;
    TextView messageText;

    ImageView userAvatar;

    public MessagesListHolder(@NonNull View itemView) {
        super(itemView);
        userName = itemView.findViewById(R.id.user_name);
        messageText = itemView.findViewById(R.id.message_text);
        userAvatar = itemView.findViewById(R.id.user_avatar);
    }
}