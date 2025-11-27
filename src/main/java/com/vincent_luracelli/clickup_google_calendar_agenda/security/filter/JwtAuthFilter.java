package com.vincent_luracelli.clickup_google_calendar_agenda.security.filter;

import com.vincent_luracelli.clickup_google_calendar_agenda.security.common.exception.AccessDeniedException;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.common.exception.InvalidTokenException;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.common.helper.JwtHelper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static com.vincent_luracelli.clickup_google_calendar_agenda.security.common.constants.AuthConstants.AUTHORIZATION_HEADER;
import static com.vincent_luracelli.clickup_google_calendar_agenda.security.service.JwtUserDetailsService.isUserActive;
import static com.vincent_luracelli.clickup_google_calendar_agenda.security.utils.JwtUtils.extractTokenWithoutBearer;
import static com.vincent_luracelli.clickup_google_calendar_agenda.security.utils.JwtUtils.isTokenFormatValid;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;

    private final UserDetailsService userDetailsService;

    private final UserAntPathResolver userAntPathResolver;

    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwtToken = getTokenFromRequest(request);
            Claims claims = jwtHelper.extractAllClaims(jwtToken);

            UserDetails userDetails = validateUserAccessibility(claims);
            addAuthenticationToContext(userDetails);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return userAntPathResolver.isPermittedPath(requestURI);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if (isTokenFormatValid(authorization)) {
            return extractTokenWithoutBearer(authorization);
        }

        throw new InvalidTokenException("Invalid token.");
    }

    private UserDetails validateUserAccessibility(Claims claims) {
        String email = claims.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!isUserActive(userDetails)) {
            throw new AccessDeniedException("User is unactive.");
        }

        return userDetails;
    }

    private void addAuthenticationToContext(UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
