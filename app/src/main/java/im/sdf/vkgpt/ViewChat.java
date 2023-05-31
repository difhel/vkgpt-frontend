package im.sdf.vkgpt;

import static im.sdf.vkgpt.helpers.Constants.VKSCRIPT_GET_CONVERSATIONS;
import static im.sdf.vkgpt.helpers.Constants.VKSCRIPT_GET_MESSAGES;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import im.sdf.vkgpt.helpers.CircleTransform;
import im.sdf.vkgpt.helpers.GPTAPI;
import im.sdf.vkgpt.helpers.RetrofitClient;
import im.sdf.vkgpt.helpers.VKAPI;
import im.sdf.vkgpt.models.ChatsListAdapter;
import im.sdf.vkgpt.models.Conversations;
import im.sdf.vkgpt.models.Messages;
import im.sdf.vkgpt.models.MessagesListAdapter;
import im.sdf.vkgpt.models.ShortenMessage;
import im.sdf.vkgpt.models.Suggestions;
import im.sdf.vkgpt.models.SuggestionsAdapter;
import im.sdf.vkgpt.models.Users;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewChat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chat);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        String chatName = intent.getStringExtra("chat_name");
        String peerId = intent.getStringExtra("peer_id");
        String photo50 = intent.getStringExtra("photo50");
        toolbar.setTitle(chatName);
        setChatAvatarToToolbar(photo50, toolbar);

        List<String> suggestionsList = new ArrayList<>();
        RecyclerView recyclerViewSuggestions = findViewById(R.id.recycler_view_suggestions);
        recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(this));
        SuggestionsAdapter suggestionsAdapter = new SuggestionsAdapter(suggestionsList);
        recyclerViewSuggestions.setAdapter(suggestionsAdapter);


        // getting messages & udpating it
        SharedPreferences sharedPreferences = getSharedPreferences("VKGPT", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int userId = sharedPreferences.getInt("user_id", -1);
        String accessToken = sharedPreferences.getString("access_token", "");

        RecyclerView recyclerView = findViewById(R.id.recycler_view_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Messages.Message> messageList = new ArrayList<>();
        MessagesListAdapter messagesAdapter = new MessagesListAdapter(messageList);
        recyclerView.setAdapter(messagesAdapter);
        VKAPI VKAPIClient = RetrofitClient
                .getInstance()
                .getVKAPI();
        Call<Messages> callGetMessages = VKAPIClient.executeGetMessages(
                VKSCRIPT_GET_MESSAGES,
                0,
                1,
                Integer.parseInt(peerId),
                accessToken,
                "5.131"
        );
        callGetMessages.enqueue(new Callback<Messages>() {
            @Override
            public void onResponse(Call<Messages> call, Response<Messages> response) {
                Messages messages = response.body();
                Log.d("ChatsListActivity", "Get conv response handled");
                if (response.isSuccessful() && messages.isSuccessful()) {
                    // all things are ok
                    Log.d("ViewChatActivity", "Successfuly got messages");
                    List<Messages.Message> messageList = messages.response;
                    handleSuggestions(getShortenMessages(messageList), suggestionsAdapter);
                    Collections.reverse(messageList);
                    messagesAdapter.setChatList(messageList);
                    recyclerView.scrollToPosition(messageList.size() - 1); // we want to show the last message
                }
                else if (!response.isSuccessful() || !messages.isSuccessful()){
                    // VK rejected the request or the response scheme is incorrect
                    Log.d("ChatsListActivity", "Rejected");
                    if (messages.error != null) {
                        if (messages.error.errorCode == 5) {
                            // User auth is broken - https://dev.vk.com/reference/errors
                            editor.clear();
                            editor.apply();
                            finishAffinity();
                            Log.d("ChatListActivity", "Broken user: " + messages.getErrorMessage());
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(ViewChat.this, R.string.error, Toast.LENGTH_LONG).show();
                            Log.e("ChatListActivity", "API Error: " + messages.getErrorMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Messages> call, Throwable t) {
                Log.d("ViewChat", "Failure", t);

                Toast.makeText(ViewChat.this, R.string.http_error, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void setChatAvatarToToolbar(String photo50, MaterialToolbar toolbar) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // Do something with the Bitmap, such as setting it as the icon of the MaterialToolbar
                toolbar.setLogo(new BitmapDrawable(getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                // Handle the failure to load the image
                Log.wtf("ViewChat", e.toString());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // Handle the start of the image loading process
            }
        };

        Picasso.get().load(photo50).resizeDimen(R.dimen.topbar_avatar_size, R.dimen.topbar_avatar_size).transform(new CircleTransform()).into(target);
    }
    private void handleSuggestions(List<ShortenMessage> shortenMessages, SuggestionsAdapter suggestionsAdapter) {
        GPTAPI GPTAPIClient = RetrofitClient
                .getInstance()
                .getGPTAPI();
        Call<Suggestions> callGetSuggestions = GPTAPIClient.predict(
                shortenMessages
        );
        callGetSuggestions.enqueue(new Callback<Suggestions>() {
            @Override
            public void onResponse(Call<Suggestions> call, Response<Suggestions> response) {
                Suggestions suggestions = response.body();
                Log.d("ViewChat", "Got suggestions response");
                if (response.isSuccessful() && suggestions.isSuccessful()) {
                    // all things are ok
                    Log.d("ViewChatActivity", "Successfuly got suggestions, first: " + suggestions.suggestions.get(0));
                    List<String> suggestionsList = suggestions.suggestions;
                    suggestionsAdapter.setSuggestionsList(suggestionsList);
                }
                else if (!response.isSuccessful() || !suggestions.isSuccessful()){
                    // My API rejected the request or the response scheme is incorrect
                    Log.d("ChatsListActivity", "Rejected");
                    Toast.makeText(ViewChat.this, R.string.error, Toast.LENGTH_LONG).show();
                    try {
                        Log.e("ChatView", "Beginning of crash. Is response succesful: " + (response.isSuccessful() ? "true" : "false"));
                        Log.e("ChatView", "Have ok: " + " ok -> " + (suggestions == null ? "suggestions is null" : suggestions.isOk()));
                        Log.e("ChatView", "Have sug: " + " sug -> " + (suggestions == null ? "suggestions is null" : suggestions.safeSuggestions()));
                        Log.e("ChatView", response.errorBody().string());
                    }
                    catch (Exception e) {Log.wtf("WTF", e);}

                    Log.e("ChatListActivity", "GPT API Error: " + " b" + suggestions.exception);
                }
            }

            @Override
            public void onFailure(Call<Suggestions> call, Throwable t) {
                Log.d("ViewChat", "Failure to get suggestions", t);

                Toast.makeText(ViewChat.this, R.string.http_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private List<ShortenMessage> getShortenMessages(List<Messages.Message> messages) {
        List<ShortenMessage> result = new ArrayList<>();
        List<String> tmp = new ArrayList<>();
        Messages.Message msg;
        for (int i = 0; i < 3 && i < messages.size(); i++) {
            msg = messages.get(i);
            result.add(new ShortenMessage(msg.fromName, msg.text));
            tmp.add(msg.text);
        }
        Log.wtf("MessageShortener", tmp.toString());
        return result;
    }
}