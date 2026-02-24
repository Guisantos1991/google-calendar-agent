package com.guidev.googlecalendaragent.dto.messageDTO;

public record Message(
        long message_id,
        Chat chat,
        From from,
        String text
) {
}
