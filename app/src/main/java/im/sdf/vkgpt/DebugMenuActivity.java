package im.sdf.vkgpt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.io.PrintWriter;
import java.io.StringWriter;

import im.sdf.vkgpt.helpers.RetrofitClient;
import im.sdf.vkgpt.helpers.VKAPI;
import im.sdf.vkgpt.models.Users;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DebugMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_menu);
        SharedPreferences sharedPreferences = getSharedPreferences("VKGPT", Context.MODE_PRIVATE);
        String userId = String.valueOf(sharedPreferences.getInt("user_id", -1));
        String accessToken = sharedPreferences.getString("access_token", "");
        TextView mTestAPITextView = (TextView) findViewById(R.id.stringNameDebugMenu);
        TextView mUserId = (TextView) findViewById(R.id.userIdDebugMenu);
        mUserId.setText(userId);
        TextInputEditText mEditText = (TextInputEditText) findViewById(R.id.editTextDebugMenu);
        mEditText.setText(accessToken);
        Button buttonApply = (Button) findViewById(R.id.buttonSaveDebugMenu);
        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("access_token", mEditText.getText().toString().trim());
                editor.putInt("user_id", 1);
                // Save the changes
                editor.apply();
            }
        });
        Button buttonTestAPI = (Button) findViewById(R.id.buttonGetMeDebugMenu);
        buttonTestAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKAPI VKAPIClient = RetrofitClient
                        .getInstance()
                        .getVKAPI();
                Call<Users> callGetMe = VKAPIClient.getUser(
                        "1",
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
                            mTestAPITextView.setText(users.response.get(0).firstName);
                        }
                        else if (!response.isSuccessful() || !users.isSuccessful()){
                            // VK rejected the request or the response scheme is incorrect
                            mTestAPITextView.setText("VK rejected the request or the response scheme is incorrect");
                        }
                        else {
                            // idk when it can be executed
                            mTestAPITextView.setText("Something weird");
                        }
                    }

                    @Override
                    public void onFailure(Call<Users> call, Throwable t) {
                        StringWriter sw = new StringWriter();
                        t.printStackTrace(new PrintWriter(sw));
                        String exceptionAsString = sw.toString();
                        mTestAPITextView.setText("On failure triaged:  " + exceptionAsString);
                    }
                });
            }
        });
    }
}