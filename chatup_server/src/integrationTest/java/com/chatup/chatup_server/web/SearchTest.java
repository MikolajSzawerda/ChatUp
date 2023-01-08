package com.chatup.chatup_server.web;

import com.chatup.chatup_server.BaseInitializedDbTest;
import com.chatup.chatup_server.domain.UserInfo;
import com.chatup.chatup_server.service.messaging.OutgoingMessage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class SearchTest extends BaseInitializedDbTest {

    private final String MESSAGES_SEARCH_ENDPOINT = "/search/messages";
    private final String USERS_SEARCH_ENDPOINT = "/search/users";

    @Test
    void shouldReturnMessagesOnlyForGivenChannel(){
        //Given
        String phrase = "test";
        long channelId = 1L;

        //When
        var response = getSearchRequest(getMessageURISearchFrom(phrase, 0, channelId), createUserToken(USER_1), OutgoingMessage[].class);

        //Then
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        List<OutgoingMessage> responseSearch = List.of(Objects.requireNonNull(response.getBody()));
        assertEquals(0, responseSearch.stream().filter(m -> m.channelID() != channelId).count());
    }

    @Test
    void shouldReturnUsersByFuzzyUsername(){
        //Given
        String name = "joh mie";

        //When
        var response = getSearchRequest(getUserURISearchFrom(name, 0), createUserToken(USER_1), UserInfo[].class);

        //Then
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        List<UserInfo> responseSearch = List.of(Objects.requireNonNull(response.getBody()));
        assertFalse(responseSearch.isEmpty());
    }

    private URI getMessageURISearchFrom(String phrase, int page, Long... channels){
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .port(PORT)
                .host("localhost")
                .path(MESSAGES_SEARCH_ENDPOINT)
                .queryParam("page", page)
                .queryParam("channels", channels)
                .queryParam("phrase", phrase)
                .build().toUri();
    }

    private URI getUserURISearchFrom(String name, int page){
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .port(PORT)
                .host("localhost")
                .path(USERS_SEARCH_ENDPOINT)
                .queryParam("name", name)
                .queryParam("page", page)
                .build().toUri();
    }

    protected <T> ResponseEntity<T> getSearchRequest(URI uri, String token, Class<T> type){
        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(createAuthHeaders(token)), type);
    }
}
