package im.sdf.vkgpt.models;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import im.sdf.vkgpt.R;

public class SuggestionsHolder extends RecyclerView.ViewHolder {
    Button button;

    public SuggestionsHolder(@NonNull View itemView) {
        super(itemView);
        button = itemView.findViewById(R.id.suggestion_button);
    }
}