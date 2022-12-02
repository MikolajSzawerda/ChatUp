package com.chatup.chatup_server.client;

import com.chatup.chatup_server.repository.AppUserRepository;
import com.chatup.chatup_server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

@Lazy
@TestConfiguration
public class ClientConfig {


    @Bean
    public SocketClientFactory socketClientFactory(@Value("${local.server.port}") int port,
                                                   AppUserRepository appUserRepository,
                                                   AuthService authService){
        return new SocketClientFactory(port, appUserRepository, authService);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
