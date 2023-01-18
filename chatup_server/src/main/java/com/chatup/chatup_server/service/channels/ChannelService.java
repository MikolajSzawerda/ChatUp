package com.chatup.chatup_server.service.channels;

import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.Channel;
import com.chatup.chatup_server.repository.AppUserRepository;
import com.chatup.chatup_server.repository.ChannelRepository;
import jakarta.persistence.EntityNotFoundException;
import com.chatup.chatup_server.service.messaging.BrokerService;
import com.chatup.chatup_server.service.messaging.OutgoingEvent;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ChannelService {
    private static final String CHANNEL_CREATION_ENDPOINT = "/exchange/";
    private final String CHANNEL_CREATION_FLAG;
    private final ChannelRepository channelRepository;
    private final AppUserRepository appUserRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final BrokerService brokerService;

    public ChannelService(ChannelRepository channelRepository,
                          AppUserRepository appUserRepository,
                          SimpMessagingTemplate simpMessagingTemplate,
                          BrokerService brokerService,
                          @Value("${app.events.channel-creation}") String channel_flag
                          ) {
        this.channelRepository = channelRepository;
        this.appUserRepository = appUserRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.brokerService = brokerService;
        this.CHANNEL_CREATION_FLAG = channel_flag;
    }

    public Channel createChannel(ChannelCreateRequest channelRequest){
        if (channelRequest.is_direct_message()) {
            if (channelRequest.user_ids().size() != 2)
                throw new IllegalArgumentException("Direct message must contain exactly 2 users.");

            if (!channelRequest.is_private())
                throw new IllegalArgumentException("Direct message channel must be private.");
        }

        List<Long> userIds = new ArrayList<>(channelRequest.user_ids());
        Set<AppUser> channelUsers = new HashSet<>();

        if (channelRequest.is_private()) {
            for (Long userId : userIds) {
                Optional<AppUser> optionalUser = appUserRepository.findById(userId);
                if (optionalUser.isEmpty())
                        throw new EntityNotFoundException("Invalid userId: " + userId);

                channelUsers.add(optionalUser.get());
            }
        } else {
            if (userIds.size() != 0)
                throw new IllegalArgumentException("Public channels shouldn't have specified members.");

            channelUsers = new HashSet<>(appUserRepository.findAll());
        }

        if (channelRequest.is_direct_message()) {
            Optional<Channel> prevChannel = getDirectMessageForUsers(userIds.get(0), userIds.get(1));
            if (prevChannel.isPresent()) {
                return prevChannel.get();
            }
        }
        Channel channel = channelRepository.save(
                new Channel(channelRequest.name(), channelRequest.is_private(),
                        channelRequest.is_direct_message(), channelUsers, Collections.emptyList())
        );
        brokerService.addChannel(channel);
        notifyAboutChannelCreation(channelUsers, channel);
        return channel;
    }

    void notifyAboutChannelCreation(Set<AppUser> users, Channel channel) {
        for (var user : users) {
            simpMessagingTemplate.convertAndSend(createBroadcastTopicName(user),
                    new OutgoingEvent(CHANNEL_CREATION_FLAG,
                            ChannelInfo.from(user.getId(), channel)));
        }
    }


    public static String createBroadcastTopicName(AppUser user) {
        return CHANNEL_CREATION_ENDPOINT + user.getUsername();
    }

    public List<ChannelInfo> listChannelsInfo(AppUser currentUser) {
        List<Channel> channels = new ArrayList<>(currentUser.getChannels());

        List<ChannelInfo> channelsInfo = new ArrayList<>();
        for (Channel channel : channels) {
            channelsInfo.add(ChannelInfo.from(currentUser.getId(), channel));
        }

        return channelsInfo;
    }

    private Optional<Channel> getDirectMessageForUsers(Long userId1, Long userId2) {
        Set<Channel> user1Dms = channelRepository.findByUsersIdAndIsDirectMessage(userId1, true);
        Set<Channel> user2Dms = channelRepository.findByUsersIdAndIsDirectMessage(userId2, true);

        user1Dms.retainAll(user2Dms);
        List<Channel> foundDms = new ArrayList<>(user1Dms);
        if (foundDms.size() > 1)
            throw new IllegalStateException("Found more than 1 DM for 2 users");

        return (user1Dms.size() > 0) ? Optional.of(foundDms.get(0)) : Optional.empty();
    }
}
