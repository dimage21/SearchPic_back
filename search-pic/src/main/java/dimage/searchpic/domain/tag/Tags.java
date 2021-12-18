package dimage.searchpic.domain.tag;

import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;

import java.util.List;
import java.util.stream.Collectors;

public class Tags {
    private static final int MAX_COUNT = 5;
    private static final int MAX_TAG_LENGTH = 15;
    private final List<Tag> tagList;

    public Tags(List<Tag> tags){
        this.tagList = removeDuplicateWords(tags);
        validateCount(tagList);
        validateSize(tagList);
    }

    private void validateSize(List<Tag> tags) {
        tags.stream().filter(tag -> tag.getName().length() > MAX_TAG_LENGTH)
                .findFirst()
                .ifPresent(t-> { throw new CustomException(ErrorInfo.MAX_TAG_LENGTH_LIMIT); });
    }

    private void validateCount(List<Tag> tags) {
        if (tags.size() > MAX_COUNT)
            throw new CustomException(ErrorInfo.MAX_TAG_COUNT_LIMIT);
    }

    private List<Tag> removeDuplicateWords(List<Tag> tags) {
        return tags.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Tag> getTagList() {
        return tagList;
    }
}
