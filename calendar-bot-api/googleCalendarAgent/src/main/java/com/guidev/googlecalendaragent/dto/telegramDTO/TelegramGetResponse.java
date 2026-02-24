package com.guidev.googlecalendaragent.dto.telegramDTO;

import java.util.List;

public record TelegramGetResponse(
        boolean ok,
        List<TelegramUpdate> result
) {
}
