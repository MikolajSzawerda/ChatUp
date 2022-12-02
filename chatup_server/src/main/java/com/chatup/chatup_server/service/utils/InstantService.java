package com.chatup.chatup_server.service.utils;


import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class InstantService {

    public Instant getNow(){
        return Instant.now();
    }
}
