package com.chatup.chatup_client.model;


public class IncomingEvent {
    private String eventType;
    private Event event;

    public IncomingEvent(String eventType, Event event) {
        this.eventType = eventType;
        this.event = event;
    }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
}
