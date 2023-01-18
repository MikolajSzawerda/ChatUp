package com.chatup.chatup_client.model;

import com.chatup.chatup_client.model.channels.Channel;
import com.chatup.chatup_client.model.messaging.Message;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(Message.class),
        @JsonSubTypes.Type(Channel.class) }
)
public interface Event {
}
