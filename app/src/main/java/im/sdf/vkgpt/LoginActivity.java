package im.sdf.vkgpt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import im.sdf.vkgpt.helpers.Constants;
import im.sdf.vkgpt.helpers.RetrofitClient;
import im.sdf.vkgpt.helpers.VKAPI;
import im.sdf.vkgpt.models.AuthResponseSuccess;
import im.sdf.vkgpt.models.VKAPIResponseError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser("", "");
            }
        });
    }

    private void loginUser(String captchaSid, String captchaKey) {
        final String userName = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (userName.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        } else if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        VKAPI VKAPIClient = RetrofitClient
                .getInstance()
                .getVKAPI();
        Call<AuthResponseSuccess> callAuth = VKAPIClient.authenticate(
                1,
                1,
                "password",
                Constants.CLIENT_ID,
                Constants.CLIENT_SECRET,
                userName,
                password,
                "all,offline",
                "VKGPTAndroid(1.0)",
                captchaSid,
                captchaKey
        );

        callAuth.enqueue(new Callback<AuthResponseSuccess>() {
            @Override
            public void onResponse(Call<AuthResponseSuccess> call, Response<AuthResponseSuccess> response) {
                AuthResponseSuccess authResponseSuccess = response.body();
                if (response.isSuccessful()) {
                    if (authResponseSuccess != null && authResponseSuccess.accessToken != null) {
                        // success, brrrrr!
                        Log.d("LoginActivity", "Success authentication");
                        // Toast.makeText(LoginActivity.this, authResponseSuccess.expiresIn + " Access token: " + authResponseSuccess.accessToken, Toast.LENGTH_LONG).show();
                        // Saving access token & user id to shared preferences
                        SharedPreferences sharedPreferences = getSharedPreferences("VKGPT", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("access_token", authResponseSuccess.accessToken);
                        editor.putInt("user_id", authResponseSuccess.userId);
                        // Save the changes
                        editor.apply();
                        finishAffinity();
                        startActivity(new Intent(getApplicationContext(), ChatsListActivity.class));
                    } else {
                        Log.wtf("LoginActivity", "VK returned a success response but it does not contain access token");
                        Toast.makeText(LoginActivity.this, R.string.http_error, Toast.LENGTH_LONG).show();
                        return;
                    }
                } else {
                    // request failed
                    try {
                        String errorJSON = response.errorBody().string();
                        VKAPIResponseError authResponseError = new Gson().fromJson(errorJSON, VKAPIResponseError.class);
                        if (authResponseError.error != null && authResponseError.error.equals("need_captcha")) {
                            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(LoginActivity.this);
                            View customAlertDialogView = LayoutInflater.from(LoginActivity.this)
                                    .inflate(R.layout.captcha_box, null, false);
                            ImageView captchaImage = customAlertDialogView.findViewById(R.id.captcha_box_image);
                            Picasso.get().load(authResponseError.captchaImg).resizeDimen(R.dimen.captcha_height, R.dimen.captcha_width).into(captchaImage);
                            TextInputEditText captchaEditText = customAlertDialogView.findViewById(R.id.captcha_text);

                            // Building the Alert dialog using materialAlertDialogBuilder instance
                            materialAlertDialogBuilder.setView(customAlertDialogView)
                                    .setTitle(getString(R.string.captcha_caption))
                                    .setPositiveButton(getString(R.string.continue_hint), (dialog, t) -> {
                                        String captchaKey = captchaEditText.getText().toString();
                                        loginUser(authResponseError.captchaSid, captchaKey);
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton(getString(R.string.cancel_hint), (dialog, t) -> {
                                        dialog.cancel();
                                    })
                                    .show();
                        }
                        if (authResponseError.error != null && authResponseError.error.equals("need_validation")) {
                            Intent intent = new Intent(getApplicationContext(), LoginTwoFaActivity.class);
                            intent.putExtra("vk2fa_url", authResponseError.redirect_uri);
                            startActivity(intent);
                        }
                        if (authResponseError.error != null) {
                            Log.e("LoginActivity", "Authentication failed: " + authResponseError.error + " " + authResponseError.errorDescription());
                            Toast.makeText(LoginActivity.this, getString(R.string.login_failed, authResponseError.error, authResponseError.errorDescription()), Toast.LENGTH_SHORT).show();
                        } else {
                            throw new java.io.IOException();
                        }
                    } catch (java.io.IOException e) {
                        Log.wtf("LoginActivity", "VK returned a error response but it does not contain error info. Probably it is a problem on VK side.");
                        Toast.makeText(LoginActivity.this, R.string.http_error, Toast.LENGTH_LONG).show();
                    }
                    return;
                }
            }

            @Override
            public void onFailure(Call<AuthResponseSuccess> call, Throwable t) {
                Log.wtf("LoginActivity", t);
                Toast.makeText(LoginActivity.this, R.string.http_error, Toast.LENGTH_LONG).show();
                return;
            }
        });
    }
}