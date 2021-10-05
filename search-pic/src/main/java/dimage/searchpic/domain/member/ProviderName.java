package dimage.searchpic.domain.member;

import java.util.Arrays;

public enum ProviderName {
    NAVER,KAKAO;

    // 일치하는 ProviderName 리턴
    public static ProviderName create(String providerName) {
        return Arrays.stream(ProviderName.values())
                .filter(value -> value.toString().equals(providerName.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}