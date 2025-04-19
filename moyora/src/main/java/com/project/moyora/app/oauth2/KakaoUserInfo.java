package com.project.moyora.app.oauth2;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.profile = (Map<String, Object>) kakaoAccount.get("profile");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getName() {
        if (kakaoAccount.containsKey("name")) {
            return (String) kakaoAccount.get("name");
        }
        return profile != null ? (String) profile.get("nickname") : null;
    }

    public String getGender() {
        return (String) kakaoAccount.get("gender");
    }

    public String getBirthday() {
        return (String) kakaoAccount.get("birthday");
    }

    public String getBirthYear() {
        return (String) kakaoAccount.get("birthyear");
    }
}
