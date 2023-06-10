package im.sdf.vkgpt.models.longpoll;

public interface LongpollListener {
    void onNewMessage(Integer peerId);
}
