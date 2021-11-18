package dimage.searchpic.service;

import dimage.searchpic.domain.tag.Tag;
import dimage.searchpic.domain.tag.repository.TagRepository;
import dimage.searchpic.dto.tag.TagDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {
    private final TagRepository tagRepository;

    public List<Tag> getTags(List<String> tagNames) {
        return tagNames.stream().map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(new Tag(name)))
        ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TagDetailResponse> getTopTags() {
        List<Tag> topTags = tagRepository.getTopTags(6);
        return topTags.stream().map(TagDetailResponse::of)
                .collect(Collectors.toList());
    }
}
