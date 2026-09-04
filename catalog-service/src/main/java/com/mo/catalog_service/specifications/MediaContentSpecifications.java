package com.mo.catalog_service.specifications;

import com.mo.catalog_service.entity.MediaContent;
import com.mo.catalog_service.enums.ContentStatus;
import com.mo.catalog_service.enums.ContentType;
import com.mo.catalog_service.enums.Genre;
import org.springframework.data.jpa.domain.Specification;

public final class MediaContentSpecifications {
    private MediaContentSpecifications() {}

    public static Specification<MediaContent> hasGenre(Genre genre) {
        return (root, query, cb) -> {
            if (genre == null) {
                return cb.conjunction();
            }
             return cb.equal(root.get("genre"), genre);
        };
    }

    public static Specification<MediaContent> hasContentType(ContentType contentType) {
        return (root, query, cb) -> {
            if (contentType == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("contentType"), contentType);
        };
    }

    public static Specification<MediaContent> hasContentStatus(ContentStatus contentStatus) {
        return (root, query, cb) -> {
            if (contentStatus == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), contentStatus);
        };
    }
}
