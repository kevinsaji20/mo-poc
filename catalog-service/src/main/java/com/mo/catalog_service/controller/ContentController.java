package com.mo.catalog_service.controller;

import com.mo.catalog_service.dto.request.CreateContentRequest;
import com.mo.catalog_service.dto.request.UpdateContentRequest;
import com.mo.catalog_service.dto.response.ContentResponse;
import com.mo.catalog_service.enums.Genre;
import com.mo.catalog_service.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {
    private final ContentService contentService;

    @PostMapping
    public ResponseEntity<ContentResponse> createContent(
            @Valid @RequestBody CreateContentRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contentService.createContent(request));
    }

    @PostMapping("/{contentId}")
    public ResponseEntity<ContentResponse> updateContent(
            @PathVariable UUID contentId,
            @Valid @RequestBody UpdateContentRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contentService.updateContent(contentId, request));
    }

    @DeleteMapping("/{contentId}")
    public ResponseEntity<Void> deleteContent(
            @PathVariable UUID contentId
    ) {
        contentService.archiveContent(contentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{contentId}")
    public ResponseEntity<ContentResponse> getContentById(
            @PathVariable UUID contentId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contentService.getContentById(contentId));
    }

    @GetMapping("/genres")
    public ResponseEntity<List<Genre>> getGenres() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contentService.getGenres());
    }

}
