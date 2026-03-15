package com.guidev.googlecalendaragent.persistence.repository;

import com.guidev.googlecalendaragent.persistence.entity.ChannelUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelUserRepository extends JpaRepository<ChannelUser, Long> {

    Optional<ChannelUser> findByChannelAndExternalUserId(String channel, String externalUserId);
    Optional<ChannelUser> findByChannelAndExternalChatId(String channel, String externalChatId);
    boolean existsByChannelAndExternalUserId(String channel, String externalUserId);
}
