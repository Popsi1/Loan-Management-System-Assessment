package com.example.usermoduleservice.config.securityConfig;

import com.example.usermoduleservice.service.MyUserDetailsService;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {
        @Autowired
        private JWTUtility jwtUtility;

        @Autowired
        private MyUserDetailsService userService;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

                String authorization = request.getHeader("Authorization");

                String token = null;
                String email = null;

                if (null != authorization && authorization.startsWith("Bearer ")) {
                        token = authorization.substring(7);
                        email = jwtUtility.getUserEmailFromToken(token);
                }

                if (null != email && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userService.loadUserByUsername(email);

                        if (jwtUtility.validateToken(token, userDetails)) {
                                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        }

                }
                filterChain.doFilter(request, response);
        }
}