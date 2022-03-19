package dimage.searchpic.domain.locationmark;

import dimage.searchpic.domain.location.Location;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.locationmark.AlreadyMarkedException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(
    uniqueConstraints = { @UniqueConstraint(columnNames = { "location_id", "member_id" }) }
)
public class LocationMark {
    @Id
    @Column(name = "location_mark_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public LocationMark(Location location, Member member) {
        this.member = member;
        this.location = location;
    }

    public void setMember(Member member) {
        this.member = member;
        member.getMarks().add(this);
    }

    public void delete() {
        member.getMarks().remove(this);
    }
}
