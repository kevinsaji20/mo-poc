package com.mo.catalog_service.mapper;

import com.mo.catalog_service.dto.response.ContentResponse;
import com.mo.catalog_service.entity.MediaContent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContentMapper {
    ContentResponse toResponse(MediaContent content);
}
