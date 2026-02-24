package com.guidev.googlecalendaragent.dto.telegramDTO;

import com.guidev.googlecalendaragent.dto.messageDTO.Message;

public record TelegramUpdate (

        Long update_id,
        Message message

) {
}
