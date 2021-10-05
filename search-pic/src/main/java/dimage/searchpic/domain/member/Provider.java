package dimage.searchpic.domain.member;

import lombok.*;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PUBLIC)
public class Provider {
    @Column
    private String providerId; // 각 제공사에서 제공해주는 client unique id

    @Column
    @Enumerated(EnumType.STRING)
    private ProviderName providerName; // 제공사 이름

    public static Provider create(String providerId, ProviderName name) {
        return new Provider(providerId, name);
    }
}
