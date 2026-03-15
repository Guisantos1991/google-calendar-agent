package com.guidev.googlecalendaragent.service;

import com.guidev.googlecalendaragent.persistence.entity.ChannelUser;
import com.guidev.googlecalendaragent.persistence.repository.ChannelUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelUserService {

    private final ChannelUserRepository channelUserRepository;

    public ChannelUser findOrCreate(String channel, String externalUserId, String externalChatId, String displayName) {
        return channelUserRepository
                .findByChannelAndExternalUserId(channel, externalUserId)
                .orElseGet(() -> create(channel, externalUserId, externalChatId, displayName));
    }

    public ChannelUser create(String channel, String externalUserId, String externalChatId, String displayName) {
        ChannelUser user = new ChannelUser();
        user.setChannel(channel);
        user.setExternalUserId(externalUserId);
        user.setExternalChatId(externalChatId);
        user.setDisplayName(displayName);
        return channelUserRepository.save(user);
    }
}
