package dimage.searchpic.service;

import dimage.searchpic.domain.tag.Tag;
import dimage.searchpic.domain.tag.repository.TagRepository;
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
}
