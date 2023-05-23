package im.sdf.vkgpt.helpers;


//import im.sdf.vkgpt.models.AnonymTokenResult;
import java.util.List;

import im.sdf.vkgpt.models.ShortenMessage;
import im.sdf.vkgpt.models.Suggestions;
import im.sdf.vkgpt.models.Users;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GPTAPI {
    @POST("predict")
    Call<Suggestions> predict (
            @Body List<ShortenMessage> messages
    );
}