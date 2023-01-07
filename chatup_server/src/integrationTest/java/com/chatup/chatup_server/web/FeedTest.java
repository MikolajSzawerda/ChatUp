package com.chatup.chatup_server.web;

import com.chatup.chatup_server.BaseInitializedDbTest;
import com.chatup.chatup_server.domain.Channel;
import com.chatup.chatup_server.domain.Message;
import com.chatup.chatup_server.repository.MessageRepository;
import com.chatup.chatup_server.service.messaging.OutgoingMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FeedTest extends BaseInitializedDbTest {
    private final String LAST_FEED_ENDPOINT = "/last-feed/{channelID}";
    private final String FEED_FROM_ENDPOINT = "/feed/{channelID}";


    @Autowired
    private MessageRepository messageRepository;

    @Value("${app.feed.pageSize}")
    private long PAGE_SIZE;

    @Test
    public void shouldGetLastFeed(){
        //Given
        Long channelID = 1L;
        List<Message> lastFeed = getLastFeedForChannel(channelID);
        URI uri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .port(PORT)
                .host("localhost")
                .path(LAST_FEED_ENDPOINT)
                .build(channelID);

        //When
        var response = getFeedRequest(uri, createUserToken(USER_1));

        //Then
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        List<OutgoingMessage> responseFeed = List.of(Objects.requireNonNull(response.getBody()));
        assertTrue(checkFeedEquality(lastFeed, responseFeed));
    }

    @Test
    public void shouldReturnFollowingMessages(){
        //Given
        Long channelID = 1L;
        List<Message> feed = messageRepository
                .findAll()
                .stream().filter(m->m.getChannel().getId().equals(channelID))
                .sorted(Comparator.comparingLong(Message::getID).reversed()).toList();
        Set<Long> feedIds = feed.stream().map(Message::getID).collect(Collectors.toSet());
        URI uri = getURIFeedFrom(channelID, feed.get(0).getID(), 0);

        //When
        var response = getFeedRequest(uri, createUserToken(USER_1));

        //Then
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        List<OutgoingMessage> responseFeed = List.of(Objects.requireNonNull(response.getBody()));
        feedIds.removeAll(responseFeed.stream().map(OutgoingMessage::messageID).collect(Collectors.toSet()));
        assertEquals(responseFeed.size(), PAGE_SIZE);

        //When
        uri = getURIFeedFrom(channelID, feed.get(0).getID(), 1);
        response = getFeedRequest(uri, createUserToken(USER_1));

        //Then
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        responseFeed = List.of(Objects.requireNonNull(response.getBody()));
        feedIds.removeAll(responseFeed.stream().map(OutgoingMessage::messageID).collect(Collectors.toSet()));
        assertEquals(feed.size()-PAGE_SIZE-1, responseFeed.size());
        assertTrue(feedIds.contains(feed.get(0).getID()));
    }

    private URI getURIFeedFrom(Long channelID, Long messageID, int page){
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .port(PORT)
                .host("localhost")
                .path(FEED_FROM_ENDPOINT)
                .queryParam("page", page)
                .queryParam("fromMessageID", messageID)
                .build(channelID);
    }

    private List<Message> getLastFeedForChannel(Long channelID){
        return messageRepository
                .findAll()
                .stream()
                .filter(m->m.getChannel().getId().equals(channelID))
                .sorted(Comparator.comparingLong(Message::getID).reversed())
                .limit(PAGE_SIZE)
                .collect(Collectors.toList());
    }

    private boolean checkFeedEquality(List<Message> feed, List<OutgoingMessage> response){
        Set<Long> feedIds = feed
                .stream()
                .map(Message::getID)
                .collect(Collectors.toSet());
        Set<Long> responseIds = response
                .stream()
                .map(OutgoingMessage::messageID)
                .collect(Collectors.toSet());
        return feedIds.size() == responseIds.size()
                && feedIds.containsAll(responseIds)
                && responseIds.containsAll(feedIds);
    }

    protected ResponseEntity<OutgoingMessage[]> getFeedRequest(URI uri, String token){
        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(createAuthHeaders(token)), OutgoingMessage[].class);
    }
}
