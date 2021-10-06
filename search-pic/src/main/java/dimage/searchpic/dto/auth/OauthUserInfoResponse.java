package dimage.searchpic.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class OauthUserInfoResponse {

    @Getter
    public static class KakaoInfoResponse {
        private String id;
        @JsonProperty("kakao_account")
        private KaKaoAccount kakaoAccount;

        public KakaoInfoResponse(){}

        public KakaoInfoResponse(String id, KaKaoAccount kakaoAccount) {
            this.id = id;
            this.kakaoAccount = kakaoAccount;
        }

        @Getter
        public static class KaKaoAccount {
            private KaKaoProfile profile;
            private String email;

            public KaKaoAccount(){}

            public KaKaoAccount(KaKaoProfile profile, String email) {
                this.profile = profile;
                this.email = email;
            }
        }

        @Getter
        public static class KaKaoProfile {
            private String nickname;

            @JsonProperty("profile_image_url")
            private String imageUrl;

            public KaKaoProfile(){}

            public KaKaoProfile(String nickname, String imageUrl) {
                this.nickname = nickname;
                this.imageUrl = imageUrl;
            }
        }
    }
}
