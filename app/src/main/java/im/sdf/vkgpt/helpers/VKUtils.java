package im.sdf.vkgpt.helpers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import im.sdf.vkgpt.ChatsListActivity;
import im.sdf.vkgpt.LoginActivity;
import im.sdf.vkgpt.R;
import im.sdf.vkgpt.models.Groups;
import im.sdf.vkgpt.models.User;
import im.sdf.vkgpt.models.Users;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VKUtils {
    private String accessToken;
    private VKAPI VKAPIClient;

    public VKUtils(String accessToken) {
        this.accessToken = accessToken;
        VKAPI VKAPIClient = RetrofitClient
                .getInstance()
                .getVKAPI();
        this.VKAPIClient = VKAPIClient;
    }

    public String getUserName(int userId) {
        final String[] result = {""};
        Call<Users> callGetMe = this.VKAPIClient.getUser(
                Integer.toString(userId),
                "",
                this.accessToken,
                "5.131"
        );
        callGetMe.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                Users users = response.body();
                if (response.isSuccessful() && users.isSuccessful() && users.response.size() == 1) {
                    User user = users.response.get(0);
                    result[0] = (user.firstName == null ? "" : user.firstName) + " " +
                             (user.lastName == null ? "" : user.lastName);
                }
                else if (!response.isSuccessful() || !users.isSuccessful()){
                    // VK rejected the request or the response scheme is incorrect
                    // nothing
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                // nothing
            }
        });
        return result[0];
    }

    public String getGroupName(int groupId) {
        final String[] result = {""};
        Call<Groups> callGetGroup = this.VKAPIClient.getGroup(
                Integer.toString(groupId),
                "",
                this.accessToken,
                "5.131"
        );

        callGetGroup.enqueue(new Callback<Groups>() {
            @Override
            public void onResponse(Call<Groups> call, Response<Groups> response) {
                Groups groups = response.body();
                if (response.isSuccessful() && groups.isSuccessful() && groups.response.size() == 1) {
                    Groups.Group group = groups.response.get(0);
                    result[0] = group.name == null ? "" : group.name;
                    Log.d("GetGroupName", result[0] + " status OK " + Integer.toString(groupId));
                }
                else if (!response.isSuccessful() || !groups.isSuccessful()){
                    // VK rejected the request or the response scheme is incorrect
                    // nothing
                }
            }

            @Override
            public void onFailure(Call<Groups> call, Throwable t) {
                // nothing
                Log.e("GetGroupName", "onFailure occured: ", t);
            }
        });
        Log.d("GetGroupName", "finnaly output " + result[0] + " 0 " + Integer.toString(groupId));
        return result[0];
    }

    public String getUserAvatar(int userId) {
        final String[] result = {""};
        Call<Users> callGetMe = this.VKAPIClient.getUser(
                Integer.toString(userId),
                "photo_50",
                this.accessToken,
                "5.200"
        );
        callGetMe.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                Users users = response.body();
                if (response.isSuccessful() && users.isSuccessful() && users.response.size() == 1) {
                    // all things are ok
                    result[0] = users.response.get(0).photo50;
                }
                else if (!response.isSuccessful() || !users.isSuccessful()){
                    // VK rejected the request or the response scheme is incorrect
                    // nothing
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                // nothing
            }
        });
        return result[0];
    }

}
