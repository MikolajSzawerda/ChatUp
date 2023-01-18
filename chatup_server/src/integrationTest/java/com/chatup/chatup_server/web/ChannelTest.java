package com.chatup.chatup_server.web;

import com.chatup.chatup_server.BaseInitializedDbTest;
import com.chatup.chatup_server.client.SocketClient;
import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.Channel;
import com.chatup.chatup_server.repository.AppUserRepository;
import com.chatup.chatup_server.repository.ChannelRepository;
import com.chatup.chatup_server.service.channels.ChannelCreateRequest;
import com.chatup.chatup_server.service.channels.ChannelInfo;
import com.chatup.chatup_server.service.channels.ChannelService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    private final String LIST_CHANNELS_ENDPOINT = "/channel/list";

    SocketClient client1;
    SocketClient client2;
    AppUser user1;
    AppUser user2;


    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private ChannelRepository channelRepository;

    @BeforeEach
    void initClient() {
        user1 = appUserRepository.findAppUserByUsername(USER_1);
        user2 = appUserRepository.findAppUserByUsername(USER_2);
    }

    @Test
    public void shouldCreateChannel() {
        Long oldChannelCount = channelRepository.count();
        String channelName = "xyz";
        ChannelCreateRequest request = new ChannelCreateRequest(
                channelName, false, false, new HashSet<>()
        );

        ResponseEntity<ChannelInfo> response = getCreateChannelRequest(createUserToken(USER_1), request);
        Channel channel = channelRepository
                .findAll()
                .stream()
                .filter(c->c.getName().equals(channelName))
                .findFirst().get();

        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        assertEquals(oldChannelCount+1, channelRepository.count());
        assertEquals(channel.getId(), Objects.requireNonNull(response.getBody()).id());
        assertEquals(channel.getName(), "xyz");
        assertEquals(channel.getDirectMessage(), false);
        assertEquals(channel.getPrivate(), false);
    }

    @Test
    public void shouldCreateChannelNameForDms() {

        ChannelCreateRequest request = new ChannelCreateRequest(
                null, true, true, new HashSet<>(){{
                add(user1.getId());
                add(user2.getId());
            }}
        );

        Long id = getCreateChannelRequest(createUserToken(USER_1), request).getBody().id();

        ResponseEntity<ChannelInfo[]> response = getListChannelsRequest(createUserToken(USER_2));
        List<ChannelInfo> channelsList = List.of(Objects.requireNonNull(response.getBody()));

        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        String newName = channelsList.stream().filter(c->c.id().equals(id)).findFirst().get().name();
        assertEquals(user1.getFirstName() + " " + user1.getLastName(), newName);

    }

    @Test
    void shouldBroadcastMessageAboutChannelCreation(){
        AppUser user1 = appUserRepository.findAppUserByUsername(USER_1);
        AppUser user2 = appUserRepository.findAppUserByUsername(USER_2);
        client1 = socketClientFactory.getClient(USER_1);
        client2 = socketClientFactory.getClient(USER_2);
        ChannelCreateRequest request = new ChannelCreateRequest(
                null, true, true, new HashSet<>(){{
            add(user1.getId());
            add(user2.getId());
        }}
        );
        getCreateChannelRequest(createUserToken(USER_1), request).getBody().id();

        timedAssertEquals(1, client1.getEvents()::size);
        timedAssertEquals(1, client2.getEvents()::size);
        client1.close();
        client2.close();
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
