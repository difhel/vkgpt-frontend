package im.sdf.vkgpt;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class LoginTwoFaActivity extends AppCompatActivity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_two_fa);

        webView = findViewById(R.id.auth_webview);
        // Enable JS
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
                if (url.startsWith("https://oauth.vk.com/blank.html")) {
                    String[] arrUrl = url.split("https://oauth.vk.com/blank.html");
                    String[] arrPayload = arrUrl[1].split("&");
                    if (arrPayload[0].equals("#success=1")) {
                        String accessToken = arrPayload[1].split("access_token=")[1];
                        String uid = arrPayload[2].split("user_id=")[1];
                        SharedPreferences sharedPreferences = getSharedPreferences("VKGPT", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("access_token", accessToken);
                        editor.putInt("user_id", Integer.parseInt(uid));
                        // Save the changes
                        editor.apply();
                        finishAffinity();
                        startActivity(new Intent(getApplicationContext(), ChatsListActivity.class));
                    }
                    finish();

                    return true;
                }
                return false;
            }
        });

        Intent intent = getIntent();
        String url = intent.getStringExtra("vk2fa_url");
        webView.loadUrl(url);
    }
}