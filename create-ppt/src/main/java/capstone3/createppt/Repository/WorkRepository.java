package capstone3.createppt.Repository;

import capstone3.createppt.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {

    // 작업 번호로 작업 조회
    Optional<Work> findByWorkId(Long workId);

    // 회원 번호로 작업 조회
    Optional<Work> findByMemberId(Long memberId);

    // 작업 삭제
    void deleteByWorkId(Long workId);

}