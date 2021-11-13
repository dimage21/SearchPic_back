package dimage.searchpic.service;

import dimage.searchpic.domain.location.Location;
import dimage.searchpic.domain.location.repository.LocationRepository;
import dimage.searchpic.domain.locationmark.LocationMark;
import dimage.searchpic.domain.locationmark.repository.LocationMarkRepository;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.domain.member.repository.MemberRepository;
import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.common.NotFoundException;
import dimage.searchpic.exception.locationmark.AlreadyMarkedException;
import dimage.searchpic.exception.post.BadAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LocationMarkService {
    private final LocationMarkRepository markRepository;
    private final MemberRepository memberRepository;
    private final LocationRepository locationRepository;

    public void markLocation(Long memberId, Long locationId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ErrorInfo.MEMBER_NULL));
        Location location = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException(ErrorInfo.LOCATION_NULL));
        if (member.checkAlreadyMarked(location))
            throw new AlreadyMarkedException(ErrorInfo.ALREADY_MARKED_PLACE);
        LocationMark locationMark = new LocationMark(location, member);
        markRepository.save(locationMark);
    }

    public void unMarkLocation(Long memberId, Long locationId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ErrorInfo.MEMBER_NULL));
        Location location = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException(ErrorInfo.LOCATION_NULL));
        LocationMark locationMark = markRepository.findByLocationAndMember(location, member).orElseThrow(() -> new BadAccessException(ErrorInfo.NOT_MARKED_PLACE));
        markRepository.delete(locationMark);
    }
}