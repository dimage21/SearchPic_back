package dimage.searchpic.dto.auth;

import dimage.searchpic.domain.member.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LoginInfoRequest {
    Provider provider;
    String email;
}
