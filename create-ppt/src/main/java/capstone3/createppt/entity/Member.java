package capstone3.createppt.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;            // 회원번호, PRIMARY KEY

    @Column(name = "login_id")
    private String loginId;     // 로그인ID, UNIQUE KEY

    @Column(name = "password")
    private String password;    // 비밀번호

    @Column(name = "name")
    private String name;        // 이름

    @Builder
    public Member(String loginId, String password, String name) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }

}