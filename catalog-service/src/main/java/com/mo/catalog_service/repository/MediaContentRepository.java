package com.mo.catalog_service.repository;

import com.mo.catalog_service.entity.MediaContent;
import com.mo.catalog_service.enums.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaContentRepository
        extends JpaRepository<MediaContent, UUID>,
        JpaSpecificationExecutor<MediaContent> {
    List<MediaContent> findByGenre(Genre genre);
}
