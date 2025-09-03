package com.globalbooks.payment.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CorrelationIdFilter ensures every request has a correlationId for tracing.
 * - Reads X-Correlation-Id header if present, otherwise generates a UUID.
 * - Stores the value in MDC so logback JSON encoder emits it.
 * - Echoes the value back in response header for client propagation.
 */
@Component // FIXED: Register filter so it's applied to all HTTP requests
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // FIXED: Prefer inbound header for correlation; generate one if absent
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString(); // FIXED: ensure every request has one
        }

        // FIXED: Put into MDC so logs and outbound messages can include it
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
        try {
            // FIXED: Echo header back to caller to enable propagation across services
            response.setHeader(CORRELATION_ID_HEADER, correlationId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CORRELATION_ID_MDC_KEY); // FIXED: avoid leak across threads
        }
    }
}