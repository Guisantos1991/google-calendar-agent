package com.guidev.googlecalendaragent.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_log")
public class MessageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_user_id", nullable = false)
    private ChannelUser channelUser;

    private String direction;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(columnDefinition = "jsonb")
    private String rawPayloadJson;

    private String detectedIntent;
    private LocalDateTime createdAt;
}