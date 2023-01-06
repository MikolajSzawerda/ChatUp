package com.chatup.chatup_server.service.channels;

import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.Channel;
import com.chatup.chatup_server.domain.exceptions.InvalidRequestException;
import com.chatup.chatup_server.repository.AppUserRepository;
import com.chatup.chatup_server.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final AppUserRepository appUserRepository;

    @Autowired
    public ChannelService(ChannelRepository channelRepository, AppUserRepository appUserRepository) {
        this.channelRepository = channelRepository;
        this.appUserRepository = appUserRepository;
    }

    public Channel createChannel(ChannelCreateRequest channelRequest) throws InvalidRequestException {
        if(channelRequest.is_direct_message()) {
            if(channelRequest.user_ids().size() != 2)
                throw new InvalidRequestException("Direct message must contain exactly 2 users.");

            if(!channelRequest.is_private())
                throw new InvalidRequestException("Direct message channel must be private.");
        }

        List<Long> userIds = new ArrayList<>(channelRequest.user_ids());
        Set<AppUser> channelUsers = new HashSet<>();

        for(Long userId : userIds) {
            Optional<AppUser> optionalUser = appUserRepository.findById(userId);
            if(optionalUser.isEmpty())
                throw new InvalidRequestException("Provided invalid userId.");

            channelUsers.add(optionalUser.get());
        }

        if(channelRequest.is_direct_message()) {
            Optional<Channel> prevChannel = getDirectMessageForUsers(userIds.get(0), userIds.get(1));
            if(prevChannel.isPresent())
                return prevChannel.get();
        }

        return channelRepository.save(
                new Channel(channelRequest.name(), channelRequest.is_private(),
                            channelRequest.is_direct_message(), channelUsers)
        );
    }

    public List<ChannelInfo> listChannelsInfo(AppUser currentUser) {
        List<Channel> channels = new ArrayList<>(currentUser.getChannels());

        List<ChannelInfo> channelsInfo = new ArrayList<>();
        for(Channel channel : channels) {
            channelsInfo.add(ChannelInfo.from(currentUser.getId(), channel));
        }
        
        return channelsInfo;
    }

    private Optional<Channel> getDirectMessageForUsers(Long userId1, Long userId2) {
        Set<Channel> user1Dms = channelRepository.findByUsersIdAndIsDirectMessage(userId1, true);
        Set<Channel> user2Dms = channelRepository.findByUsersIdAndIsDirectMessage(userId2, true);

        user1Dms.retainAll(user2Dms);
        List<Channel> foundDms = new ArrayList<>(user1Dms);
        if(foundDms.size() > 1)
            throw new IllegalStateException("Found more than 1 DM for 2 users");

        return (user1Dms.size() > 0)? Optional.of(foundDms.get(0)) : Optional.empty();
    }
}
