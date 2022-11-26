package com.chatup.chatup_server.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@Lazy
@TestConfiguration
public class ClientConfig {

    @Bean
    public SocketClientFactory socketClientFactory(@Value("${local.server.port}") int port){
        return new SocketClientFactory(port);
    }
}
