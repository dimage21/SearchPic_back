package dimage.searchpic.domain.member.repository;

import dimage.searchpic.domain.member.Member;
import dimage.searchpic.domain.member.ProviderName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    // 특정 제공사의 고유 회원번호로 등록된 멤버가 이미 존재하면 해당 멤버 리턴
    @Query(value = "select m from Member m where m.provider.providerId = :id and m.provider.providerName = :name")
    Optional<Member> findByProviderId(@Param("id") String providerId, @Param("name") ProviderName providerName);
}