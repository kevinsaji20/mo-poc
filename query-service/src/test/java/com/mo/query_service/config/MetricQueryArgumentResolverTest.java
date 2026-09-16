package com.mo.query_service.config;

import com.mo.query_service.dto.request.MetricsQueryRequest;
import com.mo.query_service.enums.Granularity;
import com.mo.query_service.exception.InvalidMetricQueryException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MetricQueryArgumentResolverTest {

    private final MetricQueryArgumentResolver resolver = new MetricQueryArgumentResolver();

    @Test
    void resolveArgument_usesDefaultsAndParsesValues() throws Exception {
        Method method = TestController.class.getDeclaredMethod("endpoint", MetricsQueryRequest.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("from", "2024-01-01T00:00:00Z");
        request.addParameter("to", "2024-01-02T00:00:00Z");
        request.addParameter("granularity", "day");
        request.addParameter("page", "2");
        request.addParameter("size", "10");

        MetricsQueryRequest actual = (MetricsQueryRequest) resolver.resolveArgument(
                parameter,
                null,
                new ServletWebRequest(request),
                null
        );

        assertThat(actual.from()).isEqualTo(OffsetDateTime.parse("2024-01-01T00:00:00Z"));
        assertThat(actual.to()).isEqualTo(OffsetDateTime.parse("2024-01-02T00:00:00Z"));
        assertThat(actual.granularity()).isEqualTo(Granularity.DAY);
        assertThat(actual.page()).isEqualTo(2);
        assertThat(actual.size()).isEqualTo(10);
        assertThat(actual.offset()).isEqualTo(10);
    }

    @Test
    void resolveArgument_rejectsInvalidPageAndRange() throws Exception {
        Method method = TestController.class.getDeclaredMethod("endpoint", MetricsQueryRequest.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        final MockHttpServletRequest invalidRangeRequest = new MockHttpServletRequest();
        invalidRangeRequest.addParameter("from", "2024-01-03T00:00:00Z");
        invalidRangeRequest.addParameter("to", "2024-01-02T00:00:00Z");

        assertThatThrownBy(() -> resolver.resolveArgument(parameter, null, new ServletWebRequest(invalidRangeRequest), null))
                .isInstanceOf(InvalidMetricQueryException.class)
                .hasMessage("`from` must be before or equal to `to`");

        final MockHttpServletRequest invalidPageRequest = new MockHttpServletRequest();
        invalidPageRequest.addParameter("page", "-1");
        assertThatThrownBy(() -> resolver.resolveArgument(parameter, null, new ServletWebRequest(invalidPageRequest), null))
                .isInstanceOf(InvalidMetricQueryException.class)
                .hasMessage("page must be greater than or equal to 0");
    }

    private static class TestController {
        public void endpoint(@com.mo.query_service.web.ValidMetricQuery MetricsQueryRequest query) {
        }
    }
}
