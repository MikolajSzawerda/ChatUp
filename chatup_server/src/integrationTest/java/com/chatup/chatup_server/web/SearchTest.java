package com.chatup.chatup_server.web;

import com.chatup.chatup_server.BaseInitializedDbTest;
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

    private final String SEARCH_ENDPOINT = "/search";

    @Test
    void shouldReturnMessagesOnlyForGivenChannel(){
        //Given
        String phrase = "test";
        long channelId = 1L;

        //When
        var response = getSearchRequest(getURISearchFrom(phrase, 0, channelId), createUserToken(USER_1));

        //Then
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        List<OutgoingMessage> responseSearch = List.of(Objects.requireNonNull(response.getBody()));
        assertEquals(0, responseSearch.stream().filter(m -> m.channelID() != channelId).count());
    }

    private URI getURISearchFrom(String phrase, int page, Long... channels){
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .port(PORT)
                .host("localhost")
                .path(SEARCH_ENDPOINT)
                .queryParam("page", page)
                .queryParam("channels", channels)
                .queryParam("phrase", phrase)
                .build().toUri();
    }

    protected ResponseEntity<OutgoingMessage[]> getSearchRequest(URI uri, String token){
        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(createAuthHeaders(token)), OutgoingMessage[].class);
    }
}
