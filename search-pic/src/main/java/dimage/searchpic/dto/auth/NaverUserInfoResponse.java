package dimage.searchpic.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class NaverUserInfoResponse {
    private Response response;

    @Getter
    public static class Response {
        private String id;
        private String email;
        @JsonProperty("profile_image")
        private String profileImage;

        public Response() { }
        public Response(String id, String email, String profileImage) {
            this.id = id;
            this.email = email;
            this.profileImage = profileImage;
        }
    }
}
