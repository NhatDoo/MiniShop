package com.demo.tmdt.common.security;

import com.demo.tmdt.common.annotation.RequiredRole;
import com.demo.tmdt.common.exception.AppException;
import com.demo.tmdt.common.exception.ErrorCode;
import com.demo.tmdt.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class RequiredRoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequiredRole requiredRole = findRequiredRole(handlerMethod);
        if (requiredRole == null) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        Role currentRole = parseRole(principal.getRole());
        boolean allowed = Arrays.asList(requiredRole.value()).contains(currentRole);
        if (!allowed) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return true;
    }

    private RequiredRole findRequiredRole(HandlerMethod handlerMethod) {
        RequiredRole methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(
                handlerMethod.getMethod(),
                RequiredRole.class
        );
        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        return AnnotatedElementUtils.findMergedAnnotation(
                handlerMethod.getBeanType(),
                RequiredRole.class
        );
    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
    }
}
