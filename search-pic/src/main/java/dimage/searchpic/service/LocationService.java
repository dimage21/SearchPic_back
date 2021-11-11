package dimage.searchpic.service;

import dimage.searchpic.domain.location.Location;
import dimage.searchpic.domain.location.repository.LocationRepository;
import dimage.searchpic.dto.analysis.AnalysisLocationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class LocationService {
    private final LocationRepository locationRepository;

    public List<AnalysisLocationResponse> findByNames(List<String> names) {
        List<Location> locations = locationRepository.findLocations(names,
                names.stream().map(Object::toString).collect(Collectors.joining(",")));
        return locations.stream().map(location->AnalysisLocationResponse.of(location,true)) // TODO: 스크립 여부 추후 수정
                .collect(Collectors.toList());
    }
}
