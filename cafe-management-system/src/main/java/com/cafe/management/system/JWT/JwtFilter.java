package com.cafe.management.system.JWT;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerUserDetailsService service;

    Claims claims = null;
    private String userName = null;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        logger.info("Request path: " + httpServletRequest.getServletPath());
        logger.info("Authorization header: " + httpServletRequest.getHeader("Authorization"));

        if(httpServletRequest.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            String authorizationHeader = httpServletRequest.getHeader("Authorization");
            String token = null;

            logger.info("Authorization header: " + authorizationHeader);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
                userName = jwtUtil.extractUsername(token);
                claims = jwtUtil.extractAllClaims(token);
                logger.info("Claims extracted: " + claims);
                logger.info("User Role: " + claims.get("role"));
            }

            if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = service.loadUserByUsername(userName);
                if (jwtUtil.validateToken(token)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                    );
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    logger.info("Authenticated user: " + userName);
                } else {
                    logger.info("Invalid JWT token");
                }
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }
    public boolean isAdmin() {
        boolean isAdmin = "admin".equalsIgnoreCase((String) claims.get("role"));
        logger.info("admin: " + isAdmin);
        return isAdmin;
    }

    public boolean isUser() {
        boolean isUser = "user".equalsIgnoreCase((String) claims.get("role"));
        logger.info("admin: " + isUser);
        return isUser;
       // return "user".equalsIgnoreCase((String) claims.get("role"));
    }

    public String getCurrentUser() {
        return userName;
    }
}
