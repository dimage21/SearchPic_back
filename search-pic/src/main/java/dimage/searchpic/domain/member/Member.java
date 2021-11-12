package dimage.searchpic.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dimage.searchpic.domain.BaseEntity;
import dimage.searchpic.domain.locationmark.LocationMark;
import dimage.searchpic.domain.post.Post;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@DynamicUpdate
public class Member extends BaseEntity {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nickname;

    private String email;

    private String profileUrl;

    @ColumnDefault("0")
    private Long postCount = 0L; // 유저가 등록한 글 개수

    @Embedded
    private Provider provider;

    // 마크한 장소
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    List<LocationMark> marks = new ArrayList<>();

    // 작성한 게시글
    @JsonIgnore
    @OneToMany(mappedBy = "author")
    List<Post> posts = new ArrayList<>();

    @Builder
    public Member(Long id, String nickname, String email, String profileUrl, Provider provider) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.profileUrl = profileUrl;
        this.provider = provider;
    }

    public void update(String nickname, String newProfileUrlPath) {
        this.nickname = nickname;
        this.profileUrl = newProfileUrlPath;
    }

    public void update(String nickname) {
        this.nickname = nickname;
    }

    public void addPostCount() {
        this.postCount++;
    }
    public void reducePostCount() {
        this.postCount--;
    }
}