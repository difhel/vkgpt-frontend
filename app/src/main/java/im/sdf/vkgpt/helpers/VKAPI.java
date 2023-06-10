package im.sdf.vkgpt.helpers;

import im.sdf.vkgpt.models.AuthResponseSuccess;
import im.sdf.vkgpt.models.Conversation;
import im.sdf.vkgpt.models.Conversations;
import im.sdf.vkgpt.models.Groups;
import im.sdf.vkgpt.models.Messages;
import im.sdf.vkgpt.models.Users;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VKAPI {
    @GET("oauth/token")
    Call<AuthResponseSuccess> authenticate(
            @Query("libverify_supported") int libverifySupported,
            @Query("2fa_supported") int twoFaSupported,
            @Query("grant_type") String grantType,
            @Query("client_id") int clientId,
            @Query("client_secret") String clientSecret,
            @Query("username") String username,
            @Query("password") String password,
            @Query("scope") String scope,
            @Query("device_id") String deviceId,
            @Query("captcha_sid") String captchaSid,
            @Query("captcha_key") String captchaKey
    );

    @GET("method/users.get")
    Call<Users> getUser(
            @Query("user_ids") String userIds,
            @Query("fields") String fields,
            @Query("access_token") String accessToken,
            @Query("v") String v
    );

    @GET("method/groups.getById")
    Call<Groups> getGroup(
            @Query("group_ids") String groupIds,
            @Query("fields") String fields,
            @Query("access_token") String accessToken,
            @Query("v") String v
    );

//    @GET("method/messages.getConversations")
//    Call<Conversations> getConversations(
//            @Query("offset") int offset,
//            @Query("count") int count,
//            @Query("extended") int extended,
//            @Query("access_token") String accessToken,
//            @Query("v") String v
//    );

    @GET("method/execute")
    Call<Conversations> executeGetChats(
            @Query("code") String code,
            @Query("offset") int offset,
            @Query("count") int count,
            @Query("access_token") String accessToken,
            @Query("v") String v
    );

    @GET("method/execute")
    Call<Conversation> executeGetChat(
            @Query("code") String code,
            @Query("peer_id") int peerId,
            @Query("access_token") String accessToken,
            @Query("v") String v
    );

    @GET("method/execute")
    Call<Messages> executeGetMessages(
            @Query("code") String code,
            @Query("offset") int offset,
            @Query("count") int count,
            @Query("peer_id") int peerId,
            @Query("access_token") String accessToken,
            @Query("v") String v
    );
}
