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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChannelTest extends BaseInitializedDbTest {
    private final String LIST_CHANNELS_ENDPOINT = "/channel/list";

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private ChannelRepository channelRepository;

    @Test
    public void shouldCreateChannel() {
        AppUser user = appUserRepository.findAppUserByUsername(USER_1);
        Long oldChannelCount = channelRepository.count();
        String channelName = "xyz";
        ChannelCreateRequest request = new ChannelCreateRequest(
                channelName, false, false, new HashSet<>(){{
                    add(user.getId());
            }}
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
        AppUser user1 = appUserRepository.findAppUserByUsername(USER_1);
        AppUser user2 = appUserRepository.findAppUserByUsername(USER_2);

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
