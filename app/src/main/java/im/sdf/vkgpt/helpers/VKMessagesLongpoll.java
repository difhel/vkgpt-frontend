package im.sdf.vkgpt.helpers;

import im.sdf.vkgpt.models.longpoll.GetLongpollServerResponse;
import im.sdf.vkgpt.models.longpoll.LongpollResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface VKMessagesLongpoll {
    @GET("method/messages.getLongPollServer")
    Call<GetLongpollServerResponse> getLongPollServer(
            @Query("access_token") String accessToken,
            @Query("v") String apiVersion,
            @Query("lp_version") String lpVersion
    );

    @GET
    Call<LongpollResponse> poll(
            @Url String url,
            @Query("act") String act,
            @Query("key") String key,
            @Query("ts") String ts,
            @Query("wait") int wait
    );
}