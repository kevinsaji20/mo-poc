package com.mo.catalog_service.service;

import com.mo.catalog_service.dto.request.CreateContentRequest;
import com.mo.catalog_service.dto.request.UpdateContentRequest;
import com.mo.catalog_service.dto.response.ContentResponse;
import com.mo.catalog_service.entity.ContentTag;
import com.mo.catalog_service.entity.MediaContent;
import com.mo.catalog_service.enums.ContentStatus;
import com.mo.catalog_service.enums.Genre;
import com.mo.catalog_service.exception.ContentNotFoundException;
import com.mo.catalog_service.kafka.producer.ContentEventProducer;
import com.mo.catalog_service.mapper.ContentMapper;
import com.mo.catalog_service.repository.MediaContentRepository;
import com.mo.common.kafka.event.ContentArchivedEvent;
import com.mo.common.kafka.event.ContentPublishedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final MediaContentRepository mediaContentRepository;
    private final ContentMapper contentMapper;
    private final ContentEventProducer producer;

    @Transactional
    @PreAuthorize("hasRole('CONTENT_ADMIN')")
    public ContentResponse createContent(CreateContentRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID createdBy = UUID.fromString(authentication.getName());

        MediaContent content = new MediaContent();

        content.setTitle(request.title());
        content.setDescription(request.description());
        content.setContentType(request.contentType());
        content.setGenre(request.genre());
        content.setLanguage(request.language());
        content.setDurationSeconds(request.durationSeconds());
        content.setThumbnailUrl(request.thumbnailUrl());
        content.setStreamUrl(request.streamUrl());
        content.setReleaseDate(request.releaseDate());
        content.setChannelName(request.channelName());

        content.setContentStatus(ContentStatus.DRAFT);
        content.setCreatedBy(createdBy);
        content.setCreatedAt(OffsetDateTime.now());
        content.setUpdatedAt(OffsetDateTime.now());

        List<ContentTag> tags = request.tags().stream().map(tag -> {
            ContentTag contentTag = new ContentTag();
            contentTag.setTag(tag);
            contentTag.setContent(content);
            return contentTag;
        }).toList();

        content.setTags(tags);

        MediaContent savedContent = mediaContentRepository.save(content);

        return contentMapper.toResponse(savedContent);
    }

    @Transactional(readOnly = true)
    public ContentResponse getContentById(UUID contendId) {
        MediaContent content = mediaContentRepository
                .findById(contendId)
                .orElseThrow(ContentNotFoundException::new);

        return contentMapper.toResponse(content);
    }

    @Transactional
    @PreAuthorize("hasRole('CONTENT_ADMIN')")
    public ContentResponse updateContent(UUID contentId, UpdateContentRequest request) {
        MediaContent content = mediaContentRepository
                .findById(contentId)
                .orElseThrow(ContentNotFoundException::new);

        ContentStatus oldStatus = content.getContentStatus();

        content.setTitle(request.title());
        content.setDescription(request.description());
        content.setContentType(request.contentType());
        content.setGenre(request.genre());
        content.setLanguage(request.language());
        content.setDurationSeconds(request.durationSeconds());
        content.setThumbnailUrl(request.thumbnailUrl());
        content.setStreamUrl(request.streamUrl());
        content.setReleaseDate(request.releaseDate());
        content.setChannelName(request.channelName());
        content.setContentStatus(request.status());

        content.setUpdatedAt(OffsetDateTime.now());

        content.getTags().clear();

        List<ContentTag> tags = request.tags().stream().map(tag -> {
            ContentTag contentTag = new ContentTag();
            contentTag.setTag(tag);
            contentTag.setContent(content);
            return contentTag;
        }).toList();

        content.getTags().addAll(tags);

        if (oldStatus != ContentStatus.PUBLISHED && request.status() == ContentStatus.PUBLISHED) {
            producer.publishContentPublished(
                    new ContentPublishedEvent(
                            content.getId(),
                            content.getContentType().toString(),
                            content.getTitle(),
                            content.getUpdatedAt()
                    )
            );
        }

        MediaContent updatedContent = mediaContentRepository.save(content);

        return contentMapper.toResponse(updatedContent);
    }

    @Transactional
    @PreAuthorize("hasRole('CONTENT_ADMIN')")
    public void archiveContent(UUID contentId) {
        MediaContent content = mediaContentRepository
                .findById(contentId)
                .orElseThrow(ContentNotFoundException::new);

        content.setContentStatus(ContentStatus.ARCHIVED);
        content.setUpdatedAt(OffsetDateTime.now());

        producer.publishContentArchived(
                new ContentArchivedEvent(
                        content.getId(),
                        content.getUpdatedAt()
                )
        );
    }

    @Transactional(readOnly = true)
    public List<Genre> getGenres() {
        return List.of(Genre.values());
    }
}
