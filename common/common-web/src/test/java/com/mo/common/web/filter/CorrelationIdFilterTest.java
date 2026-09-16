package com.mo.common.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CorrelationIdFilterTest {

    @Test
    void addsCorrelationIdWhenMissing() throws Exception {
        CorrelationIdFilter filter = new CorrelationIdFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(CorrelationIdFilter.HEADER)).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        verify(response).setHeader(eq(CorrelationIdFilter.HEADER), anyString());
        verify(chain).doFilter(request, response);
    }

    @Test
    void preservesIncomingCorrelationId() throws Exception {
        CorrelationIdFilter filter = new CorrelationIdFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(CorrelationIdFilter.HEADER)).thenReturn("incoming-id");

        filter.doFilterInternal(request, response, chain);

        verify(response).setHeader(CorrelationIdFilter.HEADER, "incoming-id");
        verify(chain).doFilter(request, response);
    }
}
