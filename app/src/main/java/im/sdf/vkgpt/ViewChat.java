package im.sdf.vkgpt;

import static im.sdf.vkgpt.helpers.Constants.VKSCRIPT_GET_MESSAGES;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import im.sdf.vkgpt.helpers.CircleTransform;
import im.sdf.vkgpt.helpers.GPTAPI;
import im.sdf.vkgpt.helpers.LongpollManager;
import im.sdf.vkgpt.helpers.RetrofitClient;
import im.sdf.vkgpt.helpers.VKAPI;
import im.sdf.vkgpt.helpers.VKUtils;
import im.sdf.vkgpt.models.Messages;
import im.sdf.vkgpt.models.MessagesListAdapter;
import im.sdf.vkgpt.models.SendMessageResponse;
import im.sdf.vkgpt.models.ShortenMessage;
import im.sdf.vkgpt.models.Suggestions;
import im.sdf.vkgpt.models.SuggestionsAdapter;
import im.sdf.vkgpt.models.longpoll.LongpollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewChat extends AppCompatActivity implements LongpollListener {
    private List<Messages.Message> messageList = new ArrayList<>();
    private MessagesListAdapter messagesAdapter = new MessagesListAdapter(messageList);
    private List<String> suggestionsList = new ArrayList<>();
    private SuggestionsAdapter suggestionsAdapter = new SuggestionsAdapter(suggestionsList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chat);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // getting params intent started with
        Intent intent = getIntent();
        String chatName = intent.getStringExtra("chat_name");
        String peerId = intent.getStringExtra("peer_id");
        String photo50 = intent.getStringExtra("photo50");

        // toolbar setting up
        toolbar.setTitle(chatName);
        setChatAvatarToToolbar(photo50, toolbar);


        // suggestions stuff
        RecyclerView recyclerViewSuggestions = findViewById(R.id.recycler_view_suggestions);
        recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSuggestions.setAdapter(suggestionsAdapter);


        // VK API misc
        SharedPreferences sharedPreferences = getSharedPreferences("VKGPT", Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("access_token", "");
        VKUtils vkUtils = new VKUtils();


        // messages list
        RecyclerView recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messagesAdapter);
        VKAPI VKAPIClient = RetrofitClient
                .getInstance()
                .getVKAPI();
        handleMessages(
                VKAPIClient,
                peerId,
                accessToken,
                suggestionsAdapter,
                messagesAdapter,
                vkUtils
        );
        findViewById(R.id.sendButton).setOnClickListener(v -> {
            EditText msg = findViewById(R.id.messageTextField);
            String message = msg.getText().toString();
            sendMessage(message);
            msg.getText().clear();
        });
    }

    public void sendMessage(String message) {
        Intent intent = getIntent();
        Integer peerId = Integer.parseInt(intent.getStringExtra("peer_id"));
        SharedPreferences sharedPreferences = getSharedPreferences("VKGPT", Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("access_token", "");
        VKUtils vkUtils = new VKUtils();
        VKAPI VKAPIClient = RetrofitClient
                .getInstance()
                .getVKAPI();
        Call<SendMessageResponse> callSendMessage = VKAPIClient.sendMessage(
                peerId,
                message,
                0,
                accessToken,
                "5.131"
        );
        callSendMessage.enqueue(new Callback<SendMessageResponse>() {
            @Override
            public void onResponse(Call<SendMessageResponse> call, Response<SendMessageResponse> response) {
                SendMessageResponse messages = response.body();
                Log.d("ViewChat", "Conversation history response handled");
                if (response.isSuccessful() && messages.isSuccessful()) {
                    // all things are ok
                    Log.d("ViewChat", "Successfully sent message");
                } else if (!response.isSuccessful() || !messages.isSuccessful()) {
                    // VK rejected the request or the response scheme is incorrect
                    Log.e("ViewChat", "Failed to send message");
                    if (messages.error != null) {
                        vkUtils.resolveVKAPIError(messages.error, messages.getErrorMessage(), getApplicationContext(), "ViewChat");
                    }
                }
            }

            @Override
            public void onFailure(Call<SendMessageResponse> call, Throwable t) {
                Log.d("ViewChat", "Failure (sending message)", t);
                Toast.makeText(getApplicationContext(), R.string.http_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("VKGPT", Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("access_token", "");
        LongpollManager.getInstance(accessToken).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("VKGPT", Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("access_token", "");
        LongpollManager.getInstance(accessToken).removeListener(this);
    }

    @Override
    public void onNewMessage(Integer peerId) {
        Intent intent = getIntent();
        Integer peerIdOnActivity = Integer.parseInt(intent.getStringExtra("peer_id"));
        if (!peerId.equals(peerIdOnActivity)) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("VKGPT", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("access_token", "");
//                int userId = sharedPreferences.getInt("user_id", -1);
                VKAPI VKAPIClient = RetrofitClient
                        .getInstance()
                        .getVKAPI();
                handleMessages(
                        VKAPIClient,
                        String.valueOf(peerId),
                        accessToken,
                        suggestionsAdapter,
                        messagesAdapter,
                        new VKUtils()
                );
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

    private void handleMessages(
            VKAPI VKAPIClient,
            String peerId,
            String accessToken,
            SuggestionsAdapter suggestionsAdapter,
            MessagesListAdapter messagesAdapter,
            VKUtils vkUtils
    ) {
        if (false) {
            // testing!
            List<Messages.Message> messageList = new ArrayList<>();
            messageList.add(new Messages().new Message("Марк Фомин", "https://sun6-22.userapi.com/s/v1/ig2/rNvPk9J2RIA6ZUmeHmxzB_dxPGIIXTgVTmNgQRr2QbX36Gy9qp_cMlSeVBU0uv8lO__RElFLcJ18OH4EQQRM5cah.jpg?size=50x50&quality=95&crop=0,0,800,800&ava=1", "Ух, только закончил с работой!", new ArrayList<>()));
            messageList.add(new Messages().new Message("Марк Фомин", "https://sun6-22.userapi.com/s/v1/ig2/rNvPk9J2RIA6ZUmeHmxzB_dxPGIIXTgVTmNgQRr2QbX36Gy9qp_cMlSeVBU0uv8lO__RElFLcJ18OH4EQQRM5cah.jpg?size=50x50&quality=95&crop=0,0,800,800&ava=1", "Ух, только закончил с работой!", new ArrayList<>()));
            messageList.add(new Messages().new Message("Марк Фомин", "https://sun6-22.userapi.com/s/v1/ig2/rNvPk9J2RIA6ZUmeHmxzB_dxPGIIXTgVTmNgQRr2QbX36Gy9qp_cMlSeVBU0uv8lO__RElFLcJ18OH4EQQRM5cah.jpg?size=50x50&quality=95&crop=0,0,800,800&ava=1", "Ух, только закончил с работой!", new ArrayList<>()));
            messageList.add(new Messages().new Message("Марк Фомин", "https://sun6-22.userapi.com/s/v1/ig2/rNvPk9J2RIA6ZUmeHmxzB_dxPGIIXTgVTmNgQRr2QbX36Gy9qp_cMlSeVBU0uv8lO__RElFLcJ18OH4EQQRM5cah.jpg?size=50x50&quality=95&crop=0,0,800,800&ava=1", "Ух, только закончил с работой!", new ArrayList<>()));
            messageList.add(new Messages().new Message("Марк Фомин", "https://sun6-22.userapi.com/s/v1/ig2/rNvPk9J2RIA6ZUmeHmxzB_dxPGIIXTgVTmNgQRr2QbX36Gy9qp_cMlSeVBU0uv8lO__RElFLcJ18OH4EQQRM5cah.jpg?size=50x50&quality=95&crop=0,0,800,800&ava=1", "Ух, только закончил с работой!", new ArrayList<>()));
            messageList.add(new Messages().new Message("Марк Фомин", "https://sun6-22.userapi.com/s/v1/ig2/rNvPk9J2RIA6ZUmeHmxzB_dxPGIIXTgVTmNgQRr2QbX36Gy9qp_cMlSeVBU0uv8lO__RElFLcJ18OH4EQQRM5cah.jpg?size=50x50&quality=95&crop=0,0,800,800&ava=1", "Ух, только закончил с работой!", new ArrayList<>()));
            messageList.add(new Messages().new Message("Марк Фомин", "https://sun6-22.userapi.com/s/v1/ig2/rNvPk9J2RIA6ZUmeHmxzB_dxPGIIXTgVTmNgQRr2QbX36Gy9qp_cMlSeVBU0uv8lO__RElFLcJ18OH4EQQRM5cah.jpg?size=50x50&quality=95&crop=0,0,800,800&ava=1", "Ух, только закончил с работой!", new ArrayList<>()));
            messageList.add(new Messages().new Message("Mark", "https://sun6-22.userapi.com/s/v1/ig2/rNvPk9J2RIA6ZUmeHmxzB_dxPGIIXTgVTmNgQRr2QbX36Gy9qp_cMlSeVBU0uv8lO__RElFLcJ18OH4EQQRM5cah.jpg?size=50x50&quality=95&crop=0,0,800,800&ava=1", "Hello, wassup?", new ArrayList<>()));
            messageList.add(new Messages().new Message("Ilya", "https://sun6-20.userapi.com/s/v1/ig2/vwamuU4GFdvG9r1MkyBjCfdmMpiKeZhSOb4nxSJF5MtMdr-SZoAXRBRjKkkvsVUKTe3RqlD8W0-LmynS6pkyBNzk.jpg?size=50x50&quality=95&crop=0,21,2027,2027&ava=1", "Hi, I'm great! How are you?", new ArrayList<>()));
            messagesAdapter.setChatList(messageList);
//            recyclerViewMessages.scrollToPosition(messageList.size() - 1);
            handleSuggestions(getShortenMessages(messageList), suggestionsAdapter);
            return;
        }
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
                Log.d("ViewChat", "Conversation history response handled");
                if (response.isSuccessful() && messages.isSuccessful()) {
                    // all things are ok
                    Log.d("ViewChat", "Successfully got the conversation history");
                    List<Messages.Message> messageList = messages.response;
                    handleSuggestions(getShortenMessages(messageList), suggestionsAdapter);
                    Collections.reverse(messageList);
                    messagesAdapter.setChatList(messageList);
                    ((RecyclerView) findViewById(R.id.recycler_view_messages)).scrollToPosition(messageList.size() - 1);
//                    recyclerViewMessages.scrollToPosition(messageList.size() - 1); // we want to show the last message
                } else if (!response.isSuccessful() || !messages.isSuccessful()) {
                    // VK rejected the request or the response scheme is incorrect
                    Log.e("ViewChat", "Failed to get the conversation history");
                    if (messages.error != null) {
                        vkUtils.resolveVKAPIError(messages.error, messages.getErrorMessage(), getApplicationContext(), "ViewChat");
                    }
                }
            }

            @Override
            public void onFailure(Call<Messages> call, Throwable t) {
                Log.d("ViewChat", "Failure (getting conversation messages)", t);
                Toast.makeText(ViewChat.this, R.string.http_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleSuggestions(List<ShortenMessage> shortenMessages, SuggestionsAdapter suggestionsAdapter) {
        if (false) {
            // testing!
            List<String> tmpSuggestionsList = new ArrayList<>();
            tmpSuggestionsList.add("Great!");
            tmpSuggestionsList.add("Perfect!");
            tmpSuggestionsList.add("Everything is down");
            suggestionsAdapter.setSuggestionsList(tmpSuggestionsList);
            return;
        }
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
                Log.d("ViewChat", "Get suggestions response handled");
                if (response.isSuccessful() && suggestions.isSuccessful()) {
                    // all things are ok
                    Log.d("ViewChatActivity", "Successfully got the suggestions list: " + suggestions.suggestions.toString());
                    List<String> suggestionsList = suggestions.suggestions;
                    suggestionsAdapter.setSuggestionsList(suggestionsList);
                } else if (!response.isSuccessful() || !suggestions.isSuccessful()) {
                    // My API rejected the request or the response scheme is incorrect
                    Log.e("ChatsListActivity", "Failed to get suggestions list");
                    Log.e("ChatListActivity", "GPT API Error: " + suggestions.exception);
                    Toast.makeText(ViewChat.this, R.string.error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Suggestions> call, Throwable t) {
                Log.d("ViewChat", "Failure (getting suggestions)", t);
                Toast.makeText(ViewChat.this, R.string.http_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private List<ShortenMessage> getShortenMessages(List<Messages.Message> messages) {
        List<ShortenMessage> result = new ArrayList<>();
        Messages.Message msg;
        for (int i = 0; i < 3 && i < messages.size(); i++) {
            msg = messages.get(i);
            result.add(new ShortenMessage(msg.fromName, msg.text));
        }
        return result;
    }


}