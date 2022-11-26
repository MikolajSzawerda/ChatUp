package com.chatup.chatup_server.client;

import java.util.List;

public interface SocketClient {
    void sendMessage(String topic, String msg);
    List<String> getMessages();
    void subscribe(String topic);
    void close();
}
