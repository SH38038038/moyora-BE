package com.project.moyora.app.oauth2;

public interface OAuth2UserInfo {
    String getProvider();      // kakao, google, etc.
    String getProviderId();    // 고유 사용자 ID
    String getEmail();         // 이메일
    String getName();          // 이름 또는 닉네임
}
