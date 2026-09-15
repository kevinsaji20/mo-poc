package com.mo.catalog_service.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class MediaContentSpecificationsTest {

    @Test
    void hasGenre_withNull_callsConjunction() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate conj = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conj);

        @SuppressWarnings("unchecked")
                Root<com.mo.catalog_service.entity.MediaContent> root = (Root<com.mo.catalog_service.entity.MediaContent>) mock(Root.class);
                CriteriaQuery<?> query = mock(CriteriaQuery.class);

                Predicate p = MediaContentSpecifications.hasGenre(null).toPredicate(root, query, cb);

        verify(cb).conjunction();
        assert p == conj;
    }

    @Test
    void hasContentType_withNull_callsConjunction() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate conj = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conj);

        @SuppressWarnings("unchecked")
                Root<com.mo.catalog_service.entity.MediaContent> root = (Root<com.mo.catalog_service.entity.MediaContent>) mock(Root.class);
                CriteriaQuery<?> query = mock(CriteriaQuery.class);

                Predicate p = MediaContentSpecifications.hasContentType(null).toPredicate(root, query, cb);

        verify(cb).conjunction();
        assert p == conj;
    }

    @Test
    void hasContentStatus_withNull_callsConjunction() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate conj = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conj);

        @SuppressWarnings("unchecked")
                Root<com.mo.catalog_service.entity.MediaContent> root = (Root<com.mo.catalog_service.entity.MediaContent>) mock(Root.class);
                CriteriaQuery<?> query = mock(CriteriaQuery.class);

                Predicate p = MediaContentSpecifications.hasContentStatus(null).toPredicate(root, query, cb);

        verify(cb).conjunction();
        assert p == conj;
    }
}
