package im.sdf.vkgpt.models;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import im.sdf.vkgpt.R;
import im.sdf.vkgpt.ViewChat;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsHolder> {
    List<String> suggestionsList;

    public SuggestionsAdapter(List<String> suggestionsList) {
        this.suggestionsList = suggestionsList;
    }

    public void setSuggestionsList(List<String> suggestionsList) {
        this.suggestionsList = suggestionsList;
        Log.d("SAdapter", "got data, notifying dataset changed... " + suggestionsList.get(0));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SuggestionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion, parent, false);
        return new SuggestionsHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SuggestionsHolder holder, int position) {
        String suggestion = suggestionsList.get(position);
        holder.button.setText(suggestion);
        holder.button.setOnClickListener(view -> {
            ((ViewChat) holder.itemView.getContext()).sendMessage(suggestion);
        });
    }

    @Override
    public int getItemCount() {
        return suggestionsList.size();
    }
}

