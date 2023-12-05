package capstone3.createppt.repository;

import capstone3.createppt.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 회원 조회
    Optional<Member> findByLoginId(String loginId);

    // 회원 삭제
    void deleteByLoginId(String loginId);

}
