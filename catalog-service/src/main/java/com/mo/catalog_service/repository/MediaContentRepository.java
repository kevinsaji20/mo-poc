package com.mo.catalog_service.repository;

import com.mo.catalog_service.entity.MediaContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MediaContentRepository extends JpaRepository<MediaContent, UUID> {
}
