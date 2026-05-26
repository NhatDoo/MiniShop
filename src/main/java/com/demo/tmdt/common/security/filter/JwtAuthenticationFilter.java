package com.demo.tmdt.common.security.filter;

import com.demo.tmdt.common.exception.ErrorCode;
import com.demo.tmdt.common.security.JwtAuthenticationEntryPoint;
import com.demo.tmdt.common.security.UserPrincipal;
import com.demo.tmdt.service.JWTService;
import com.demo.tmdt.service.SessionService;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final SessionService sessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                var jwt = jwtService.verifyToken(token);
                JWTClaimsSet claims = jwt.getJWTClaimsSet();

                String userId = claims.getSubject();
                String sid = claims.getStringClaim("sid");
                String type = claims.getStringClaim("type");

                if ("access".equals(type) && sessionService.isValid(sid)) {
                    UserPrincipal principal = new UserPrincipal(userId, null, null, sid);

                    var auth = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            null
                    );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    request.setAttribute(JwtAuthenticationEntryPoint.ERROR_CODE_ATTRIBUTE, ErrorCode.INVALID_ACCESS_TOKEN);
                }
            } catch (ParseException | RuntimeException ignored) {
                SecurityContextHolder.clearContext();
                request.setAttribute(JwtAuthenticationEntryPoint.ERROR_CODE_ATTRIBUTE, ErrorCode.INVALID_ACCESS_TOKEN);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");

        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        return null;
    }
}
