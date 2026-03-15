package com.guidev.googlecalendaragent.persistence.repository;

import com.guidev.googlecalendaragent.persistence.entity.ConversationState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationStateRepository extends JpaRepository<ConversationState, Long> {

    Optional<ConversationState> findByChannelUserId(Long channelUserId);

    void deleteByChannelUserId(Long channelUserId);
}