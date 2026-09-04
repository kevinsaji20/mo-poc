package com.mo.query_service.dto.response;

import java.util.UUID;

public record TopContentResponse (
    UUID contentId,
    Long totalWatchTimeMs
) {
}
