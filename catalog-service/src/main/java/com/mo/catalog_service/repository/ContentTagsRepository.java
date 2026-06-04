package com.mo.catalog_service.repository;

import com.mo.catalog_service.entity.ContentTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContentTagsRepository extends JpaRepository<ContentTag, UUID> {
}
