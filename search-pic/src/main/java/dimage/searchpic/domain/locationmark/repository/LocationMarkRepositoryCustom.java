package dimage.searchpic.domain.locationmark.repository;

import dimage.searchpic.dto.location.MarkLocationResponse;

import java.util.List;

public interface LocationMarkRepositoryCustom {
    List<MarkLocationResponse> findLocationsMemberMarked(Long memberId);
}
