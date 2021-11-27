package dimage.searchpic.domain.location.repository;

import dimage.searchpic.domain.location.Location;
import java.util.List;

public interface LocationRepositoryCustom {
    List<Location> nearSpotsFromPlace(double x, double y, double distance, long offset, int size);
}