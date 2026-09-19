package com.mo.query_service.dto.response;

import java.util.UUID;

public record MostCompletedResponse(
        UUID contentId,
        Long completeCount
) {
}
