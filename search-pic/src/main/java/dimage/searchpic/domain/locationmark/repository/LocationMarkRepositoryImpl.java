package dimage.searchpic.domain.locationmark.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dimage.searchpic.dto.location.MarkLocationResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static dimage.searchpic.domain.location.QLocation.location;
import static dimage.searchpic.domain.locationmark.QLocationMark.locationMark;
import static dimage.searchpic.domain.member.QMember.member;

@RequiredArgsConstructor
public class LocationMarkRepositoryImpl implements LocationMarkRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<MarkLocationResponse> findLocationsMemberMarked(Long memberId) {
        return queryFactory
                .select(Projections.fields(MarkLocationResponse.class,
                        location.id.as("id"),
                        location.repImageUrl.as("imageUrl"),
                        location.x.as("x"),
                        location.y.as("y")
                ))
                .from(locationMark)
                .leftJoin(locationMark.member, member)
                .leftJoin(locationMark.location, location)
                .where(member.id.eq(memberId))
                .fetch();
    }
}