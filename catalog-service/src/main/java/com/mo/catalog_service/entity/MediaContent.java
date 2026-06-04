package com.mo.catalog_service.entity;

import com.mo.catalog_service.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "media_content")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaContent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    private String language;
    private Integer durationSeconds;
    private String thumbnailUrl;
    private String streamUrl;
    private LocalDate releaseDate;
    private String channelName;

    @Enumerated(EnumType.STRING)
    private ContentStatus contentStatus;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID createdBy;

    @OneToMany(
            mappedBy = "content",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ContentTag> tags;
}
