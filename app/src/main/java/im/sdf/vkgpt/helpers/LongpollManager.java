package im.sdf.vkgpt.helpers;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.sdf.vkgpt.models.longpoll.GetLongpollServerResponse;
import im.sdf.vkgpt.models.longpoll.LongpollListener;
import im.sdf.vkgpt.models.longpoll.LongpollResponse;
import retrofit2.Call;
import retrofit2.Response;

public class LongpollManager {
    private static LongpollManager instance;
    private static String accessToken;
    private List<LongpollListener> listeners = new ArrayList<>();

    private LongpollManager() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        startPolling(accessToken);
                    } catch (Exception e) {
                        Log.wtf("LP2", e);
                    }
                }
            }
        }).start();
    }

    public static synchronized LongpollManager getInstance(String accessToken) {
        LongpollManager.accessToken = accessToken;
        if (instance == null) {
            instance = new LongpollManager();
        }
        return instance;
    }

    public void addListener(LongpollListener listener) {
        listeners.add(listener);
    }

    public void removeListener(LongpollListener listener) {
        listeners.remove(listener);
    }

    private void notifyNewMessage(Integer peerId) {
        for (LongpollListener listener : listeners) {
            listener.onNewMessage(peerId);
        }
    }

    public void startPolling(String accessToken) throws IOException {
        Log.wtf("LP", "Polling started");
        // getting longpoll server
        VKMessagesLongpoll LongpollClient = RetrofitClient
                .getInstance()
                .getLongpoll();
        Call<GetLongpollServerResponse> callGetServer = LongpollClient.getLongPollServer(
                accessToken,
                "5.131",
                "3"
        );
        Response<GetLongpollServerResponse> response = callGetServer.execute();
        GetLongpollServerResponse longPollResponse = response.body();
        Log.wtf("LP", "Polling started 2 + errorGetServer " + longPollResponse.getErrorMessage());

        if (longPollResponse != null && longPollResponse.response != null) {
            String longpollServerUrl = longPollResponse.response.server;
            Log.d("LP", "~~ Got LP server: " + longpollServerUrl);
            int ts = longPollResponse.response.ts;
            while (true) {
                // getting events
                Call<LongpollResponse> eventsCall = LongpollClient.poll(
                        "https://" + longpollServerUrl,
                        "a_check",
                        longPollResponse.response.key,
                        String.valueOf(ts),
                        25
                );

                Response<LongpollResponse> eventsResponse = eventsCall.execute();
                LongpollResponse events = eventsResponse.body();
                Log.i("LP", "eventsResponse.isSuccessful() -> " + (eventsResponse.isSuccessful() ? "true" : "false"));
                if (eventsResponse.isSuccessful() && events != null && events.isSuccessful()) {
                    for (List<Object> update : events.updates) {
                        // checking events
                        Integer eventCode = ((Double) update.get(0)).intValue();
                        // TODO: handle events like message edited/deleted, now it handles only new message event
                        if (eventCode.equals(4)) {
                            // new message
                            Integer peerId = ((Double) update.get(3)).intValue();
                            notifyNewMessage(peerId);
                            Log.i("LPNewMessageEvent", "New message: " + update.toString());
                        }
                        if (eventCode.equals(2)) {
                            // TODO: message deleted
                            Log.i("LPEditedMessageEvent", "Edited message: " + update.toString());
                        }
                    }
                    ts = events.ts;
                } else {
                    Log.e("LP", "failed -> " + (events.failed != null ? String.valueOf(events.failed) : "null"));
                }
            }
        }

    }
}
