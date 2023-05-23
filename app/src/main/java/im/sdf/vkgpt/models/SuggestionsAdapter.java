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

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsHolder> {
    List<String> suggestionsList;
    String accessToken;

    public SuggestionsAdapter(List<String> suggestionsList) {
        this.suggestionsList = suggestionsList;
        this.accessToken = accessToken;
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
        holder.button.setText("Test replace with suggestion");
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.itemView.getContext(), "clicked on " + suggestion, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestionsList.size();
    }
}

