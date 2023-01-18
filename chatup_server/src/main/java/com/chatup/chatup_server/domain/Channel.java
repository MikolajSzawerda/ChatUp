package com.chatup.chatup_server.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;

import java.util.List;
import java.util.Set;


@Entity
@Table(
        name = "channels"
)
public class Channel {
    @Id
    @SequenceGenerator(
            name = "channel_sequence",
            sequenceName = "channel_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "channel_sequence"
    )
    @Column(name = "channel_id")
    @GenericField
    private Long id;

    @Column
    private String name;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    private Boolean isPrivate;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    private Boolean isDirectMessage;

    @ManyToMany(
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "channels_users",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<AppUser> users;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "channel")
    private List<Message> messages;

    public Channel() {}

    public Channel(Long id, String name, Boolean isPrivate, Boolean isDirectMessage, Set<AppUser> users, List<Message> messages) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
        this.isDirectMessage = isDirectMessage;
        this.users = users;
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Channel(String name, Boolean isPrivate, Boolean isDirectMessage, Set<AppUser> users, List<Message> messages) {
        this.name = name;
        this.isPrivate = isPrivate;
        this.isDirectMessage = isDirectMessage;
        this.users = users;
        this.messages = messages;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getPrivate() { return isPrivate; }
    public void setPrivate(Boolean aPrivate) { isPrivate = aPrivate; }

    public Boolean getDirectMessage() { return isDirectMessage; }
    public void setDirectMessage(Boolean directMessage) { isDirectMessage = directMessage; }

    public Set<AppUser> getUsers() { return users; }
    public void setUsers(Set<AppUser> users) { this.users = users; }
}
