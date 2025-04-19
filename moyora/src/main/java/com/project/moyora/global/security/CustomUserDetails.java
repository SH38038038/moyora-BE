package com.project.moyora.global.security;

import com.project.moyora.app.domain.GenderType;
import com.project.moyora.app.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomUserDetails implements UserDetails, OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    public CustomUserDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한이 별도로 없다면 비워서 리턴하거나 기본 ROLE_USER 부여
        return List.of((GrantedAuthority) () -> "ROLE_USER");
    }

    @Override
    public String getPassword() {
        // password 필드가 없다면 null 반환 또는 예외 처리
        return null;
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Spring Security는 unique한 값인 email을 username으로 활용 가능
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isDeleted(); // deletedAt이 null이면 false (즉, 잠기지 않음)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getVerified(); // 이메일 인증 여부로 활성화 판단
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes; // OAuth2User의 사용자 속성 반환
    }

    @Override
    public String getName() {
        return user.getName(); // OAuth2User의 이름 반환
    }

    public User getUser() {
        return user;
    }


    // GenderType enum 사용
    public GenderType getGender() {
        return user.getGender(); // User 엔티티에서 GenderType 사용
    }
}
