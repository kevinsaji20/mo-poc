package com.mo.catalog_service.service;

import com.mo.catalog_service.dto.request.ContentQueryParamRequest;
import com.mo.catalog_service.dto.request.CreateContentRequest;
import com.mo.catalog_service.dto.request.UpdateContentRequest;
import com.mo.catalog_service.dto.response.ContentResponse;
import com.mo.catalog_service.entity.ContentTag;
import com.mo.catalog_service.entity.MediaContent;
import com.mo.catalog_service.enums.ContentStatus;
import com.mo.catalog_service.enums.ContentType;
import com.mo.catalog_service.enums.Genre;
import com.mo.catalog_service.exception.ContentNotFoundException;
import com.mo.catalog_service.kafka.producer.ContentEventProducer;
import com.mo.catalog_service.mapper.ContentMapper;
import com.mo.catalog_service.repository.MediaContentRepository;
import com.mo.common.kafka.events.ContentArchivedEvent;
import com.mo.common.kafka.events.ContentPublishedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @Mock
    MediaContentRepository mediaContentRepository;

    @Mock
    ContentMapper contentMapper;

    @Mock
    ContentEventProducer producer;

    @InjectMocks
    com.mo.catalog_service.service.ContentService contentService;

    @Captor
    ArgumentCaptor<ContentPublishedEvent> publishedCaptor;

    @Captor
    ArgumentCaptor<ContentArchivedEvent> archivedCaptor;

    @BeforeEach
    void setupSecurityContext() {
        Authentication auth = mock(Authentication.class);
        org.mockito.Mockito.lenient().when(auth.getName()).thenReturn(UUID.randomUUID().toString());
        SecurityContext ctx = new SecurityContextImpl(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void getContent_mapsRepositoryResults() {
        MediaContent mc = new MediaContent();
        mc.setId(UUID.randomUUID());
        when(mediaContentRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(List.of(mc));

        ContentResponse resp = new ContentResponse(mc.getId(), mc.getTitle(), mc.getDescription(), null, null, null, null, null, null, List.of(), null, null, null, null, null, null);
        when(contentMapper.toResponse(mc)).thenReturn(resp);

        List<ContentResponse> results = contentService.getContent(new ContentQueryParamRequest(null, null, null));

        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(resp);
        verify(mediaContentRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class));
        verify(contentMapper).toResponse(mc);
    }

    @Test
    void createContent_setsFieldsAndSaves() {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());

        CreateContentRequest req = new CreateContentRequest("title", "desc", ContentType.VIDEO, Genre.ENTERTAINMENT, "en", 120, "thumb", "stream", List.of("tag1"), java.time.LocalDate.now(), "channel");

        MediaContent saved = new MediaContent();
        saved.setId(UUID.randomUUID());
        when(mediaContentRepository.save(any(MediaContent.class))).thenAnswer(inv -> {
            MediaContent arg = inv.getArgument(0);
            arg.setId(saved.getId());
            return arg;
        });

        ContentResponse resp = new ContentResponse(saved.getId(), "title", "desc", null, null, null, null, null, null, List.of(), null, null, null, null, null, null);
        when(contentMapper.toResponse(any(MediaContent.class))).thenReturn(resp);

        ContentResponse result = contentService.createContent(req);

        assertThat(result).isEqualTo(resp);
        verify(mediaContentRepository).save(any(MediaContent.class));
    }

    @Test
    void getContentById_found_and_notFound() {
        UUID id = UUID.randomUUID();
        MediaContent mc = new MediaContent();
        mc.setId(id);
        when(mediaContentRepository.findById(id)).thenReturn(Optional.of(mc));
        ContentResponse resp = new ContentResponse(id, null, null, null, null, null, null, null, null, List.of(), null, null, null, null, null, null);
        when(contentMapper.toResponse(mc)).thenReturn(resp);

        ContentResponse found = contentService.getContentById(id);
        assertThat(found).isEqualTo(resp);

        UUID missing = UUID.randomUUID();
        when(mediaContentRepository.findById(missing)).thenReturn(Optional.empty());
        assertThrows(ContentNotFoundException.class, () -> contentService.getContentById(missing));
    }

    @Test
    void updateContent_publishesEvent_whenStatusChangesToPublished() {
        UUID id = UUID.randomUUID();
        MediaContent mc = new MediaContent();
        mc.setId(id);
        mc.setContentStatus(ContentStatus.DRAFT);
        mc.setContentType(ContentType.VIDEO);
        mc.setTitle("old");
        mc.setUpdatedAt(OffsetDateTime.now());
        ContentTag t = new ContentTag();
        t.setTag("oldTag");
        mc.setTags(new java.util.ArrayList<>(List.of(t)));

        when(mediaContentRepository.findById(id)).thenReturn(Optional.of(mc));
        when(mediaContentRepository.save(any(MediaContent.class))).thenAnswer(inv -> inv.getArgument(0));
        when(contentMapper.toResponse(any(MediaContent.class))).thenReturn(new ContentResponse(id, "newTitle", "desc", null, null, null, null, null, null, List.of(), null, null, null, null, null, null));

        UpdateContentRequest req = new UpdateContentRequest("newTitle", "desc", ContentType.VIDEO, Genre.EDUCATION, "en", 100, "thumb", "stream", List.of("t1"), java.time.LocalDate.now(), "channel", ContentStatus.PUBLISHED);

        ContentResponse res = contentService.updateContent(id, req);

        assertThat(res).isNotNull();
        verify(producer).publishContentPublished(publishedCaptor.capture());
        ContentPublishedEvent event = publishedCaptor.getValue();
        assertThat(event.contentId()).isEqualTo(id);
    }

    @Test
    void updateContent_doesNotPublish_ifAlreadyPublished() {
        UUID id = UUID.randomUUID();
        MediaContent mc = new MediaContent();
        mc.setId(id);
        mc.setContentStatus(ContentStatus.PUBLISHED);
        mc.setTags(new java.util.ArrayList<>());

        when(mediaContentRepository.findById(id)).thenReturn(Optional.of(mc));
        when(mediaContentRepository.save(any(MediaContent.class))).thenAnswer(inv -> inv.getArgument(0));
        when(contentMapper.toResponse(any(MediaContent.class))).thenReturn(new ContentResponse(id, "title", "desc", null, null, null, null, null, null, List.of(), null, null, null, null, null, null));

        UpdateContentRequest req = new UpdateContentRequest("title", "desc", ContentType.VIDEO, Genre.EDUCATION, "en", 100, "thumb", "stream", List.of("t1"), java.time.LocalDate.now(), "channel", ContentStatus.PUBLISHED);

        contentService.updateContent(id, req);

        verify(producer, never()).publishContentPublished(any());
    }

    @Test
    void archiveContent_publishesArchivedEvent() {
        UUID id = UUID.randomUUID();
        MediaContent mc = new MediaContent();
        mc.setId(id);
        mc.setContentStatus(ContentStatus.PUBLISHED);
        mc.setTags(new java.util.ArrayList<>());
        when(mediaContentRepository.findById(id)).thenReturn(Optional.of(mc));

        contentService.archiveContent(id);

        verify(producer).publishContentArchived(archivedCaptor.capture());
        ContentArchivedEvent ev = archivedCaptor.getValue();
        assertThat(ev.contentId()).isEqualTo(id);
        assertThat(mc.getContentStatus()).isEqualTo(ContentStatus.ARCHIVED);
    }

    @Test
    void getGenres_returnsAllGenres() {
        List<Genre> genres = contentService.getGenres();
        assertThat(genres).contains(Genre.ENTERTAINMENT, Genre.EDUCATION);
    }
}
