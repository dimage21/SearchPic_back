package dimage.searchpic.domain.location.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dimage.searchpic.domain.location.Location;
import lombok.RequiredArgsConstructor;
import java.util.List;
import static com.querydsl.core.types.dsl.MathExpressions.radians;
import static com.querydsl.core.types.dsl.MathExpressions.sin;
import static com.querydsl.core.types.dsl.MathExpressions.cos;
import static com.querydsl.core.types.dsl.MathExpressions.acos;
import static dimage.searchpic.domain.location.QLocation.location;

@RequiredArgsConstructor
public class LocationRepositoryImpl implements LocationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private JPAQuery<Location> getNearSpots(double x, double y, double distance) {
        return queryFactory
                .select(location)
                .from(location)
                .where(
                    getHaversineDistance(x, y).loe(distance)
                );
    }

    @Override
    public List<Location> nearSpotsFromPlace(double x, double y, double distance, long offset, int size) {
            return getNearSpots(x,y,distance)
                .where(
                    location.x.ne(x).or(location.y.ne(y))
                )
                .orderBy(getHaversineDistance(x, y).asc())
                .offset(offset)
                .limit(size)
                .fetch();
    }

    @Override
    public List<Location> nearSpotsFromPosition(double x, double y, double distance) {
        return getNearSpots(x,y,distance)
                .orderBy(getHaversineDistance(x, y).asc())
                .limit(6)
                .fetch();
    }

    private NumberExpression<Double> getHaversineDistance(double x, double y) {
        return acos(
                    sin(radians(Expressions.constant(y)))
                    .multiply(sin(radians(location.y)))
                    .add(cos(radians(Expressions.constant(y)))
                            .multiply(cos(radians(location.y)))
                            .multiply(
                                cos(radians(location.x)
                                .subtract(radians(Expressions.constant(x))))
                            )
                        )
                    ).multiply(Expressions.constant(6371));
    }
}
