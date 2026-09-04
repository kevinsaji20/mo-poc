package com.mo.query_service.config;

import com.mo.query_service.dto.request.MetricsQueryRequest;
import com.mo.query_service.enums.Granularity;
import com.mo.query_service.exception.InvalidMetricQueryException;
import com.mo.query_service.web.ValidMetricQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.OffsetDateTime;

@Configuration
@RequiredArgsConstructor
public class MetricQueryArgumentResolver implements HandlerMethodArgumentResolver {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private static final int MINUS_HOURS = 24;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ValidMetricQuery.class)
                && parameter.getParameterType().equals(MetricsQueryRequest.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mvContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        String fromParam = webRequest.getParameter("from");
        String toParam = webRequest.getParameter("to");
        String granularityParam = webRequest.getParameter("granularity");
        String pageParam = webRequest.getParameter("page");
        String sizeParam = webRequest.getParameter("size");
        String sortBy = webRequest.getParameter("sortBy");
        String sortDir = webRequest.getParameter("sortDir");

        OffsetDateTime to = toParam != null
                ? OffsetDateTime.parse(toParam)
                : OffsetDateTime.now();

        OffsetDateTime from = fromParam != null
                ? OffsetDateTime.parse(fromParam)
                : to.minusHours(MINUS_HOURS);

        Granularity granularity = granularityParam != null
                ? Granularity.valueOf(granularityParam.toUpperCase())
                : Granularity.HOUR;

        int page = pageParam != null
                ? Integer.parseInt(pageParam)
                : DEFAULT_PAGE;

        int size = sizeParam != null
                ? Integer.parseInt(sizeParam)
                : DEFAULT_SIZE;

        if (page < 0) {
            throw new InvalidMetricQueryException("page must be greater than or equal to 0");
        }

        if (size < 1 || size > MAX_SIZE) {
            throw new InvalidMetricQueryException("size must be between 1 and 100");
        }

        if (from.isAfter(to)) {
            throw new InvalidMetricQueryException("`from` must be before or equal to `to`");
        }

        return new MetricsQueryRequest(
                from,
                to,
                granularity,
                page,
                size,
                sortBy,
                sortDir
        );
    }
}
