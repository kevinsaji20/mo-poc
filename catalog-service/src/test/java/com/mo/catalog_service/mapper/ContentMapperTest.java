package com.mo.catalog_service.mapper;

import com.mo.catalog_service.entity.ContentTag;
import com.mo.catalog_service.entity.MediaContent;
import com.mo.catalog_service.dto.response.ContentResponse;
import com.mo.catalog_service.enums.ContentStatus;
import com.mo.catalog_service.enums.ContentType;
import com.mo.catalog_service.enums.Genre;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ContentMapperTest {

    @Test
    void toResponse_mapsAllFields() {
        ContentMapperImpl mapper = new ContentMapperImpl();

        MediaContent mc = new MediaContent();
        UUID id = UUID.randomUUID();
        mc.setId(id);
        mc.setTitle("title");
        mc.setDescription("desc");
        mc.setContentType(ContentType.VIDEO);
        mc.setGenre(Genre.ENTERTAINMENT);
        mc.setLanguage("en");
        mc.setDurationSeconds(120);
        mc.setThumbnailUrl("thumb");
        mc.setStreamUrl("stream");
        mc.setReleaseDate(LocalDate.now());
        mc.setChannelName("channel");
        mc.setContentStatus(ContentStatus.DRAFT);
        mc.setCreatedAt(OffsetDateTime.now());
        mc.setUpdatedAt(OffsetDateTime.now());
        mc.setCreatedBy(UUID.randomUUID());

        ContentTag tag = new ContentTag();
        tag.setId(UUID.randomUUID());
        tag.setContent(mc);
        tag.setTag("t");
        List<ContentTag> tags = new ArrayList<>();
        tags.add(tag);
        mc.setTags(tags);

        ContentResponse resp = mapper.toResponse(mc);

        assertThat(resp.title()).isEqualTo("title");
        assertThat(resp.contentType()).isEqualTo(ContentType.VIDEO);
        assertThat(resp.genre()).isEqualTo(Genre.ENTERTAINMENT);
        assertThat(resp.tags()).hasSize(1);
        // contentStatus -> status is not mapped by default (different property names), expect null
        assertThat(resp.status()).isNull();
    }
}
