package com.chatup.chatup_server.service.messaging;


import com.chatup.chatup_server.service.channels.ChannelInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(OutgoingMessage.class),
        @JsonSubTypes.Type(ChannelInfo.class) }
)
public interface Event {
}
