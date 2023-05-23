package im.sdf.vkgpt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import im.sdf.vkgpt.databinding.ActivityMainBinding;
import im.sdf.vkgpt.LoginActivity;
import im.sdf.vkgpt.helpers.Constants;
import im.sdf.vkgpt.helpers.RetrofitClient;
import im.sdf.vkgpt.helpers.VKAPI;
import im.sdf.vkgpt.models.AuthResponseSuccess;
import im.sdf.vkgpt.models.Users;
import im.sdf.vkgpt.models.VKAPIResponseError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        Boolean isError = false;
        SharedPreferences sharedPreferences = getSharedPreferences("VKGPT", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String accessToken = sharedPreferences.getString("access_token", "");
        if (userId == -1 || accessToken.isEmpty()) {
            // first app usage
//            Toast.makeText(MainActivity.this, "First usage " + String.valueOf(userId) + accessToken, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            return;
        }
        VKAPI VKAPIClient = RetrofitClient
                .getInstance()
                .getVKAPI();
        Call<Users> callGetMe = VKAPIClient.getUser(
                Integer.toString(userId),
                "",
                accessToken,
                "5.131"
        );
        callGetMe.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                Users users = response.body();
                if (response.isSuccessful() && users.isSuccessful() && users.response.size() == 1) {
                    // all things are ok
                    finishAffinity();
                    Toast.makeText(MainActivity.this, getString(R.string.welcome_back, users.response.get(0).firstName), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), ChatsListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
                else if (!response.isSuccessful() || !users.isSuccessful()){
                    // VK rejected the request or the response scheme is incorrect
                    finishAffinity();
                    Log.e("MainActivity", "Broken user: " + users.getErrorMessage());
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
                else {
                    // idk when it can be executed
                    showError(getString(R.string.wtf, String.format("User id $1%d, response_is_ok $2%s, users_get_errmsg $3%s", userId, response.isSuccessful() ? "1" : "0", users.getErrorMessage())));
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                showError(R.string.http_error);
            }
        });
    }
    private void showError(int errorMessage) {
        setContentView(binding.getRoot());
        View errorBlock = findViewById(R.id.splashscreen_error);
        TextView errorTextView = (TextView) findViewById(R.id.main_activity_error_subtext);
        View buttonRestartApp = findViewById(R.id.restart_button);
        errorTextView.setText(errorMessage);
        errorBlock.setVisibility(View.VISIBLE);
        buttonRestartApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void showError(String errorMessage) {
        setContentView(binding.getRoot());
        View errorBlock = findViewById(R.id.splashscreen_error);
        TextView errorTextView = (TextView) findViewById(R.id.main_activity_error_subtext);
        View buttonRestartApp = findViewById(R.id.restart_button);
        errorTextView.setText(errorMessage);
        errorBlock.setVisibility(View.VISIBLE);
        buttonRestartApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}