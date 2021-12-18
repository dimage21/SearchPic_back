package dimage.searchpic.domain.tag;

import dimage.searchpic.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TagsTest {

    @Test
    @DisplayName("정상적인 입력의 경우 제대로 작동한다.")
    public void 정상_작동_테스트() {
        List<String> values = new ArrayList<>(Arrays.asList("태그1", "태그2", "태그3", "태그4", "태그5"));
        List<Tag> tags = values.stream().map(Tag::new).collect(Collectors.toList());
        Tags distinctTags = new Tags(tags);
        assertThat(distinctTags.getTagList().size()).isEqualTo(5);
    }

    @Test
    @DisplayName("같은 단어로 구성된 태그를 제거한 태그 리스트를 반환한다")
    public void 중복_테스트(){
        List<String> values = new ArrayList<>(Arrays.asList("태그1", "태그2", "태그3", "태그4", "태그2"));
        List<Tag> tags = values.stream().map(Tag::new).collect(Collectors.toList());
        Tags distinctTags = new Tags(tags);

        assertThat(distinctTags.getTagList().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("태그를 5개 초과 등록 할 경우 예외가 발생한다.")
    public void 개수_초과시_예외_테스트(){
        List<String> values = new ArrayList<>(Arrays.asList("태그1", "태그2", "태그3", "태그4", "태그5","태그6"));
        List<Tag> tags = values.stream().map(Tag::new).collect(Collectors.toList());
        assertThrows(CustomException.class, ()-> new Tags(tags));
    }
}