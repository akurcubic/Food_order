package com.raf.foodOrder.security;

import com.raf.foodOrder.exceptions.NotAuthenticatedException;
import com.raf.foodOrder.exceptions.RoleNotSupportedException;
import com.raf.foodOrder.model.CustomUserDetails;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
public class SecurityAspect {

    @Pointcut("@annotation(com.raf.foodOrder.security.CheckSecurity)")
    public void checkSecurityPointcut() {}

    @Pointcut("@annotation(com.raf.foodOrder.security.Authorize)")
    public void authorizePointcut() {}


    @Before("checkSecurityPointcut() && @annotation(checkSecurity)")
    public void checkPermissions(CheckSecurity checkSecurity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthenticatedException("User is not authenticated.");
        }

        Set<String> requiredPermissions = new HashSet<>();
        Collections.addAll(requiredPermissions, checkSecurity.permissions());


        @SuppressWarnings("unchecked")
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();


        Set<String> userPermissions = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        if (!userPermissions.containsAll(requiredPermissions)) {
            throw new AccessDeniedException(checkSecurity.message());
        }
    }

    @Before("authorizePointcut() && @annotation(authorize)")
    public void checkRoles(Authorize authorize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthenticatedException("User is not authenticated.");
        }

        String requiredRole = authorize.value();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String role = userDetails.getRole();

        if (!requiredRole.equals(role)) {
            throw new RoleNotSupportedException("User role '" + role + "' is not supported for this action.");
        }
    }

}
