package com.mo.catalog_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "content_tags")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private MediaContent content;

    private String tag;
}
