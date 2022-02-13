package dimage.searchpic.dto.location.api;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public abstract class KakaoApiResponse<T extends Document> {
    public KakaoApiResponse(List<T> documents) {
        this.documents = documents;
    }
    private List<T> documents;
}