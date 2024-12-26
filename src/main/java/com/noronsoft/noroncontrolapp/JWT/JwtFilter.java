package com.noronsoft.noroncontrolapp.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtFilter extends HttpFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String requestPath = request.getRequestURI();

        if (requestPath.contains("/swagger-ui/") || requestPath.contains("/v3/api-docs")) {
            chain.doFilter(request, response);
            return;
        }
        if (requestPath.contains("/api/client")) {
            chain.doFilter(request, response);
            return;
        }
        if (requestPath.contains("/api/pusher")) {
            chain.doFilter(request, response);
            return;
        }
        if (requestPath.contains("/api/notifications")) {
            chain.doFilter(request, response);
            return;
        }


        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        String token = authorizationHeader.substring(7);
        try {
            String username = jwtUtil.extractUsername(token, false);
            Integer userId = jwtUtil.extractUserId(token, false);

            if (!jwtUtil.validateToken(token, username, false)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Invalid token");
                return;
            }

            request.setAttribute("userId", userId);
            request.setAttribute("username", username);

        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Error while validating token");
            return;
        }

        chain.doFilter(request, response);
    }
}
