package com.chatup.chatup_server.web;

import com.chatup.chatup_server.BaseInitializedDbTest;
import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.Channel;
import com.chatup.chatup_server.repository.AppUserRepository;
import com.chatup.chatup_server.repository.ChannelRepository;
import com.chatup.chatup_server.service.channels.ChannelCreateRequest;
import com.chatup.chatup_server.service.channels.ChannelInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChannelTest extends BaseInitializedDbTest {
    private final String CREATE_CHANNEL_ENDPOINT = "/channel/create";
    private final String LIST_CHANNELS_ENDPOINT = "/channel/list";

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private ChannelRepository channelRepository;

    @Test
    public void shouldCreateChannel() {
        AppUser user = appUserRepository.findAppUserByUsername(USER_1);

        ChannelCreateRequest request = new ChannelCreateRequest(
                "xyz", false, false, new HashSet<>(){{
                    add(user.getId());
            }}
        );

        ResponseEntity<ChannelInfo> response = getCreateChannelRequest(createUserToken(USER_1), request);
        List<Channel> channels = channelRepository.findAll();

        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        assertEquals(channels.size(), 1);
        assertEquals(channels.get(0).getId(), Objects.requireNonNull(response.getBody()).id());
        assertEquals(channels.get(0).getName(), "xyz");
        assertEquals(channels.get(0).getDirectMessage(), false);
        assertEquals(channels.get(0).getPrivate(), false);
    }

    @Test
    public void shouldCreateChannelNameForDms() {
        AppUser user1 = appUserRepository.findAppUserByUsername(USER_1);
        AppUser user2 = appUserRepository.findAppUserByUsername(USER_2);

        ChannelCreateRequest request = new ChannelCreateRequest(
                null, true, true, new HashSet<>(){{
                add(user1.getId());
                add(user2.getId());
            }}
        );

        getCreateChannelRequest(createUserToken(USER_1), request);

        ResponseEntity<ChannelInfo[]> response = getListChannelsRequest(createUserToken(USER_2));
        List<ChannelInfo> channelsList = List.of(Objects.requireNonNull(response.getBody()));

        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        assertEquals(channelsList.size(), 1);
        assertEquals(channelsList.get(0).name(), USER_1);
    }

    protected ResponseEntity<ChannelInfo> getCreateChannelRequest(String token, ChannelCreateRequest body){
        URI uri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .port(PORT)
                .host("localhost")
                .path(CREATE_CHANNEL_ENDPOINT)
                .build().toUri();

        HttpEntity<ChannelCreateRequest> httpBody = new HttpEntity<>(body, createAuthHeaders(token));
        return restTemplate.exchange(uri, HttpMethod.POST, httpBody, ChannelInfo.class);
    }

    protected ResponseEntity<ChannelInfo[]> getListChannelsRequest(String token){
        URI uri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .port(PORT)
                .host("localhost")
                .path(LIST_CHANNELS_ENDPOINT)
                .build().toUri();

        return restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(createAuthHeaders(token)), ChannelInfo[].class
        );
    }
}
