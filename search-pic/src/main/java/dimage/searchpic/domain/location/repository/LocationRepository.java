package dimage.searchpic.domain.location.repository;

import dimage.searchpic.domain.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location,Long>, LocationRepositoryCustom {
    @Query("select l from Location l where l.placeName in :placeNames order by FIND_IN_SET(l.placeName, :placeString)")
    List<Location> findLocations(@Param("placeNames") List<String> placeNames,@Param("placeString") String placeString);

    Optional<Location> findByXAndY(double x, double y);
}
