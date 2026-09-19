package com.mo.catalog_service.mapper;

import com.mo.catalog_service.dto.response.ContentResponse;
import com.mo.catalog_service.dto.response.ContentTagResponse;
import com.mo.catalog_service.entity.ContentTag;
import com.mo.catalog_service.entity.MediaContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContentMapper {
    @Mapping(source = "id", target = "contentId")
    ContentResponse toResponse(MediaContent content);

    ContentTagResponse toTagResponse(ContentTag tag);

    List<ContentTagResponse> toTagResponses(List<ContentTag> tags);
}
