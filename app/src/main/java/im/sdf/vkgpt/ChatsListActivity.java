package im.sdf.vkgpt;

import static im.sdf.vkgpt.helpers.Constants.VKSCRIPT_GET_CONVERSATIONS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import im.sdf.vkgpt.databinding.ActivityChatsListBinding;
import im.sdf.vkgpt.helpers.CircleTransform;
import im.sdf.vkgpt.helpers.RetrofitClient;
import im.sdf.vkgpt.helpers.VKAPI;
import im.sdf.vkgpt.helpers.VKUtils;
import im.sdf.vkgpt.models.ChatsListAdapter;
import im.sdf.vkgpt.models.Conversations;
import im.sdf.vkgpt.models.Users;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsListActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityChatsListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // toolbar setting up
        View logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("VKGPT", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            finishAffinity();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });
        SharedPreferences sharedPreferences = getSharedPreferences("VKGPT", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String accessToken = sharedPreferences.getString("access_token", "");
        VKUtils vkUtils = new VKUtils();
        setAvatarToToolbar(userId, accessToken, vkUtils);


        // getting the conversations list
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Conversations.ResponseItem> chatList = new ArrayList<>(); // Retrieve chat list from API call
        ChatsListAdapter chatAdapter = new ChatsListAdapter(chatList);
        recyclerView.setAdapter(chatAdapter);
        Log.d("ChatsListActivity", "Set adapter - done");

        VKAPI VKAPIClient = RetrofitClient
                .getInstance()
                .getVKAPI();
        Call<Conversations> callGetConversations = VKAPIClient.executeGetChats(
                VKSCRIPT_GET_CONVERSATIONS,
                10,
                1,
                accessToken,
                "5.131"
        );
        callGetConversations.enqueue(new Callback<Conversations>() {
            @Override
            public void onResponse(Call<Conversations> call, Response<Conversations> response) {
                Conversations conversations = response.body();
                Log.d("ChatsListActivity", "Get conversations list response handled");
                if (response.isSuccessful() && conversations.isSuccessful()) {
                    // all things are ok
                    Log.d("ChatsListActivity", "Successfully got the conversations list");
                    List<Conversations.ResponseItem> chatList = conversations.response;
                    chatAdapter.setChatList(chatList);
                }
                else if (!response.isSuccessful() || !conversations.isSuccessful()){
                    // VK rejected the request or the response scheme is incorrect
                    Log.d("ChatsListActivity", "Rejected");
                    if (conversations.error != null) {
                        vkUtils.resolveVKAPIError(conversations.error, conversations.getErrorMessage(), getApplicationContext(), "ChatListActivity");
                    }
                }
            }

            @Override
            public void onFailure(Call<Conversations> call, Throwable t) {
                Log.d("ChatsListActivity", "Failure (getting conversations list)", t);
                Toast.makeText(ChatsListActivity.this, R.string.http_error, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void setAvatarToToolbar(int userId, String accessToken, VKUtils vkUtils) {
        VKAPI VKAPIClient = RetrofitClient
                .getInstance()
                .getVKAPI();
        Call<Users> callGetMe = VKAPIClient.getUser(
                Integer.toString(userId),
                "photo_50",
                accessToken,
                "5.131"
        );
        callGetMe.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                Users users = response.body();
                if (response.isSuccessful() && users.isSuccessful() && users.response.size() == 1) {
                    // all things are ok
                    MaterialToolbar toolbar = findViewById(R.id.topAppBar);
                    Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            toolbar.setLogo(new BitmapDrawable(getResources(), bitmap));
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            // Handle the failure to load the image
                            Log.wtf("ChatsListActivity", e.toString());
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            // Handle the start of the image loading process
                        }
                    };

                    Picasso.get().load(users.response.get(0).photo50).resizeDimen(R.dimen.topbar_avatar_size, R.dimen.topbar_avatar_size).transform(new CircleTransform()).into(target);
                }
                else if (!response.isSuccessful() || !users.isSuccessful()){
                    // VK rejected the request or the response scheme is incorrect
                    if (users.error != null) {
                        vkUtils.resolveVKAPIError(users.error, users.getErrorMessage(), getApplicationContext(), "ChatsListActivity");
                    }
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(ChatsListActivity.this, R.string.http_error, Toast.LENGTH_LONG).show();
            }
        });
    }
}