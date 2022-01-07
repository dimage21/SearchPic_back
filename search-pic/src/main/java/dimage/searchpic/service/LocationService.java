package dimage.searchpic.service;

import dimage.searchpic.domain.location.Location;
import dimage.searchpic.domain.location.repository.LocationRepository;
import dimage.searchpic.domain.locationmark.repository.LocationMarkRepository;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.domain.member.repository.MemberRepository;
import dimage.searchpic.domain.post.repository.PostRepository;
import dimage.searchpic.domain.tag.repository.TagRepository;
import dimage.searchpic.dto.location.LocationRequest;
import dimage.searchpic.dto.location.LocationResponse;
import dimage.searchpic.dto.location.CoordResponse;
import dimage.searchpic.dto.location.MarkLocationResponse;
import dimage.searchpic.dto.tag.TagResponse;
import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.common.NotFoundException;
import dimage.searchpic.util.ResponseConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationMarkRepository locationMarkRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final RestTemplate restTemplate;
    private final ResponseConverter responseConverter;

    @Value("${location.key}") private String key;
    @Value("${location.coord-url}") private String requestUrl;

    public List<LocationResponse> findByNames(List<String> names, Long memberId) {
        List<Location> locations = locationRepository.findLocations(names,
                names.stream().map(Object::toString).collect(Collectors.joining(",")));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ErrorInfo.MEMBER_NULL));
        return locations.stream().map(location -> LocationResponse.of(location, member.checkAlreadyMarked(location),null))
                .collect(Collectors.toList());
    }

    public LocationResponse findOnePlace(Long locationId,Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ErrorInfo.MEMBER_NULL));
        Location location = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException(ErrorInfo.LOCATION_NULL));

        List<TagResponse> topTags = tagRepository.getLocationTopTags(location.getId(), 3); // top 3 태그
        List<String> topTagNames = topTags.stream().map(TagResponse::getName).collect(Collectors.toList());

        return LocationResponse.of(location, member.checkAlreadyMarked(location),topTagNames);
    }

    public Location requestLocationInfo(double x, double y) {
        LocationRequest locationRequest = new LocationRequest(x, y);
        URI uri = UriComponentsBuilder.fromHttpUrl(requestUrl)
                .queryParams(responseConverter.convert(locationRequest))
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",key);

        ResponseEntity<CoordResponse> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                CoordResponse.class
        );
        return Objects.requireNonNull(result.getBody()).createLocation(x,y);
    }

    @Transactional
    public Location findOrCreate(String repUrl, double x, double y) {
        return locationRepository.findByXAndY(x, y)
                .orElseGet(
                        () -> {
                            Location location = requestLocationInfo(x, y);
                            location.setRepImageUrl(repUrl);
                            locationRepository.save(location);
                            return location;
                        }
                );
    }
    public List<MarkLocationResponse> findAllLocations(){
        return locationRepository.findAll().stream().map(MarkLocationResponse::of).collect(Collectors.toList());
    }

    public List<MarkLocationResponse> findLocationsMemberMarked(Long memberId){
        return locationMarkRepository.findLocationsMemberMarked(memberId);
    }
    public List<MarkLocationResponse> findLocationsMemberWrite(Long memberId){
        return postRepository.findLocationsMemberWrite(memberId);
    }

    // distance km 이내에 위치한 포토스팟 6개 조회
    public List<LocationResponse> nearSpotsInfo(long memberId, double x, double y,double distance) {
        List<Location> locations = locationRepository.nearSpotsFromPosition(x, y,distance);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ErrorInfo.MEMBER_NULL));

        return locations.stream().map(location -> {
            List<TagResponse> topTags = tagRepository.getLocationTopTags(location.getId(), 3); // top 3 태그 조회
            List<String> topTagNames = topTags.stream().map(TagResponse::getName).collect(Collectors.toList());
            return LocationResponse.of(location, topTagNames,member.checkAlreadyMarked(location),x,y);
        })
        .collect(Collectors.toList());
    }
}