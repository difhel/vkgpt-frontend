package im.sdf.vkgpt.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import im.sdf.vkgpt.LoginActivity;
import im.sdf.vkgpt.R;
import im.sdf.vkgpt.ViewChat;
import im.sdf.vkgpt.models.VKAPIError;

public class VKUtils {
    public void resolveVKAPIError(VKAPIError error, String errorMessage, Context context, String logcatTag) {
        if (error.errorCode == 5) {
            // User auth is broken - https://dev.vk.com/reference/errors
            SharedPreferences sharedPreferences = context.getSharedPreferences("VKGPT", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            ((Activity) context).finishAffinity();
            Log.d(logcatTag, "Broken user: " + errorMessage);
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
        else {
            Toast.makeText(context, R.string.error, Toast.LENGTH_LONG).show();
            Log.e(logcatTag, "API Error: " + errorMessage);
        }
    }
}