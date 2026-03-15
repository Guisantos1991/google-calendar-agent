package com.guidev.googlecalendaragent.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_state")
public class ConversationState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "channel_user_id", nullable = false)
    private ChannelUser channelUser;

    private String activeIntent;

    @Column(columnDefinition = "jsonb")
    private String stateJson;

    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}