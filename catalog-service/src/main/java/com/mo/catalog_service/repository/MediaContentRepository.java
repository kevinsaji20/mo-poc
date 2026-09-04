package com.mo.catalog_service.repository;

import com.mo.catalog_service.entity.MediaContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MediaContentRepository extends JpaRepository<MediaContent, UUID> {
}
