package com.mo.query_service.dto.response;

import java.util.UUID;

public record GenreTrendResponse(
        UUID contentId,
        String genre,
        String title,
        Long totalWatchTimeMs
) {
}
