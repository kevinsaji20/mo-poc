package com.mo.catalog_service.controller;

import com.mo.catalog_service.dto.request.CreateContentRequest;
import com.mo.catalog_service.dto.request.UpdateContentRequest;
import com.mo.catalog_service.dto.response.ContentResponse;
import com.mo.catalog_service.enums.ContentStatus;
import com.mo.catalog_service.enums.ContentType;
import com.mo.catalog_service.enums.Genre;
import com.mo.catalog_service.service.ContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentControllerTest {

    @Mock
    ContentService contentService;

    private ContentController controller;

    @BeforeEach
    void setup() {
        controller = new ContentController(contentService);
    }

    @Test
    void getContents_shouldReturnList() {
        ContentResponse resp = new ContentResponse(
                UUID.randomUUID(),
                "t",
                "d",
                null,
                null,
                null,
                null,
                null,
                null,
                java.util.List.of(),
                null,
                null,
                null,
                null,
                null,
                null
        );
        when(contentService.getContent(any())).thenReturn(List.of(resp));

        ResponseEntity<java.util.List<ContentResponse>> response = controller.getContents(new com.mo.catalog_service.dto.request.ContentQueryParamRequest(null, null, null));
        assert response.getStatusCode().is2xxSuccessful();
        assert response.getBody() != null && response.getBody().size() == 1;

        verify(contentService).getContent(any());
    }

    @Test
    void createContent_shouldReturnCreated() {
        CreateContentRequest req = new CreateContentRequest("t","d", ContentType.VIDEO, Genre.ENTERTAINMENT, "en", 10, "thumb", "stream", List.of("tag"), java.time.LocalDate.now(), "ch");
        ContentResponse resp = new ContentResponse(UUID.randomUUID(), "t", "d", null, null, null, null, null, null, java.util.List.of(), null, null, null, null, null, null);
        when(contentService.createContent(any())).thenReturn(resp);

        ResponseEntity<ContentResponse> respEntity = controller.createContent(req);
        assert respEntity.getStatusCode().is2xxSuccessful() || respEntity.getStatusCode().isSameCodeAs(org.springframework.http.HttpStatus.CREATED);
        assert respEntity.getBody() != null;
        verify(contentService).createContent(any());
    }

    @Test
    void updateContent_shouldReturnOk() {
        UUID id = UUID.randomUUID();
        UpdateContentRequest req = new UpdateContentRequest("t","d", ContentType.VIDEO, Genre.ENTERTAINMENT, "en", 10, "thumb", "stream", List.of("tag"), java.time.LocalDate.now(), "ch", ContentStatus.DRAFT);
                ContentResponse resp = new ContentResponse(id, "t", "d", null, null, null, null, null, null, java.util.List.of(), null, null, null, null, null, null);
        when(contentService.updateContent(eq(id), any())).thenReturn(resp);

        ResponseEntity<ContentResponse> respEntity = controller.updateContent(id, req);
        assert respEntity.getStatusCode().is2xxSuccessful();
        assert respEntity.getBody() != null;
        verify(contentService).updateContent(eq(id), any());
    }

    @Test
    void deleteContent_shouldReturnNoContent() {
        UUID id = UUID.randomUUID();
        doNothing().when(contentService).archiveContent(id);

        ResponseEntity<Void> respEntity = controller.deleteContent(id);
        assert respEntity.getStatusCode().isSameCodeAs(org.springframework.http.HttpStatus.NO_CONTENT);
        verify(contentService).archiveContent(id);
    }

    @Test
    void getContentById_shouldReturnContent() {
        UUID id = UUID.randomUUID();
        ContentResponse resp = new ContentResponse(id, "t", "d", null, null, null, null, null, null, java.util.List.of(), null, null, null, null, null, null);
        when(contentService.getContentById(id)).thenReturn(resp);

        ResponseEntity<ContentResponse> respEntity = controller.getContentById(id);
        assert respEntity.getStatusCode().is2xxSuccessful();
        assert respEntity.getBody() != null;
        verify(contentService).getContentById(id);
    }

    @Test
    void getGenres_shouldReturnGenres() {
        when(contentService.getGenres()).thenReturn(List.of(Genre.ENTERTAINMENT, Genre.EDUCATION));

        ResponseEntity<java.util.List<Genre>> respEntity = controller.getGenres();
        assert respEntity.getStatusCode().is2xxSuccessful();
        assert respEntity.getBody() != null && respEntity.getBody().contains(Genre.ENTERTAINMENT);

        verify(contentService).getGenres();
    }
}
