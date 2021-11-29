package dimage.searchpic.domain.locationmark.repository;

import dimage.searchpic.domain.location.Location;
import dimage.searchpic.domain.locationmark.LocationMark;
import dimage.searchpic.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LocationMarkRepository extends JpaRepository<LocationMark,Long>,LocationMarkRepositoryCustom {
    Optional<LocationMark> findByLocationAndMember(Location location, Member member);
}