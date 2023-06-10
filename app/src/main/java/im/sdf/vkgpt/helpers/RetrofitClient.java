package im.sdf.vkgpt.helpers;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL_VK_ME_API = Constants.BASE_URL_VK_ME_API;
    private static final String BASE_URL_GPT_API = Constants.BASE_URL_GPT_API;

    private static RetrofitClient instance;

    private final Retrofit retrofitVK;
    private final Retrofit retrofitGPT;

    private final Retrofit retrofitLongpoll;

    private RetrofitClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        retrofitVK = new Retrofit.Builder()
                .baseUrl(BASE_URL_VK_ME_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        retrofitGPT = new Retrofit.Builder()
                .baseUrl(BASE_URL_GPT_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        retrofitLongpoll = new Retrofit.Builder()
                .baseUrl(BASE_URL_VK_ME_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.connectTimeout(100, TimeUnit.SECONDS)
                        .readTimeout(100, TimeUnit.SECONDS).build())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public VKAPI getVKAPI() {
        return retrofitVK.create(VKAPI.class);
    }

    public GPTAPI getGPTAPI() {
        return retrofitGPT.create(GPTAPI.class);
    }

    public VKMessagesLongpoll getLongpoll() {
        return retrofitLongpoll.create(VKMessagesLongpoll.class);
    }
}
