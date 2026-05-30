package com.demo.tmdt.common.security;

import com.demo.tmdt.common.annotation.CurrentUser;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)  && UserPrincipal.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter, // Metadata của parameter đang cần resolve.
            ModelAndViewContainer mavContainer, // gần như khoogn sử dụng do @RestController đã hỗ trợ r
            NativeWebRequest webRequest, // lấy dữ liệu từ beazer hoặc cookie ( tuy nhien cung khong can toi lun )
            WebDataBinderFactory binderFactory// gần như khoogn sử dụng do @RestController đã hỗ trợ r
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Unauthenticated");
        }

        return principal;
    }
}
