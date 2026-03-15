package com.guidev.googlecalendaragent.persistence.repository;

import com.guidev.googlecalendaragent.persistence.entity.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {

    List<MessageLog> findTop20ByChannelUserIdOrderByCreatedAtDesc(Long channelUserId);

    List<MessageLog> findByChannelUserIdOrderByCreatedAtDesc(Long channelUserId);
}