package capstone3.createppt.member;

import capstone3.createppt.entity.Member;
import capstone3.createppt.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    // 회원 정보 생성
    @Test
    void saveMember() {
        Member saveParams = Member.builder()
                .loginId("testID")
                .password("testPW")
                .name("testName")
                .build();

        Member member = memberRepository.save(saveParams);
        Assertions.assertEquals(member.getLoginId(), "testID");
    }

    @Test
    void deleteMemberById() {
        memberRepository.deleteById(1L);
    }

    @Test
    @DisplayName("로그인 ID로 회원 삭제")
    void deleteMemberByLoginId() {
        // 먼저 해당 로그인 ID를 가진 회원을 데이터베이스에서 검색
        Optional<Member> member = memberRepository.findByLoginId("dyl6266");

        if (member.isPresent()) {
            memberRepository.deleteById(member.get().getId());
        } else {
            // 로그인 ID를 가진 회원이 없는 경우에 대한 처리
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
    }
}