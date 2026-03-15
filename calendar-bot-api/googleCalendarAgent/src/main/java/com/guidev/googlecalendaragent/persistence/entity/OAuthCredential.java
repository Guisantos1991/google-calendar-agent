package com.guidev.googlecalendaragent.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "oauth_credential")
public class OAuthCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_user_id", nullable = false)
    private ChannelUser channelUser;

    private String provider;
    private String accountEmail;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    @Column(columnDefinition = "TEXT")
    private String accessToken;

    private LocalDateTime tokenExpiry;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
