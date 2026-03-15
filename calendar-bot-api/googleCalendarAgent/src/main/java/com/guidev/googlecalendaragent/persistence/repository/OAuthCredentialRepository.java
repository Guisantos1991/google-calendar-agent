package com.guidev.googlecalendaragent.persistence.repository;

import com.guidev.googlecalendaragent.persistence.entity.OAuthCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthCredentialRepository extends JpaRepository<OAuthCredential, Long> {

    Optional<OAuthCredential> findByChannelUserIdAndProvider(Long channelUserId, String provider);

    boolean existsByChannelUserIdAndProvider(Long channelUserId, String provider);

    void deleteByChannelUserIdAndProvider(Long channelUserId, String provider);
}