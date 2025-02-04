package com.raf.foodOrder.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
@Getter
public class CustomUserDetails implements UserDetails {

    private final int id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String role;

    public CustomUserDetails(int id, String email, String password, Collection<? extends GrantedAuthority> authorities, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

