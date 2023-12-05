package capstone3.createppt.member;

import capstone3.createppt.entity.Member;
import capstone3.createppt.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 회원 가입
    public Long join(MemberForm form) {
        // 빌더로 회원 생성
        Member member = Member.builder()
                .loginId(form.getLoginId())
                .password(form.getPassword())
                .name(form.getName())
                .build();

        // 중복 회원 검증
        validateDuplicateMember(member);

        // 회원 가입
        memberRepository.save(member);

        // 회원 가입 후 회원 ID 반환
        return member.getId();
    }

    // 중복 회원 검증(로그인 ID)
    private void validateDuplicateMember(Member member) {
        Optional<Member> result = memberRepository.findByLoginId(member.getLoginId());

        result.ifPresent(m -> {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        });
    }

    // 회원 모두 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 로그인 ID로 회원 조회
    public Member findMember(String loginId) {
        Optional<Member> result = memberRepository.findByLoginId(loginId);

        if (result.isPresent()) {
            System.out.println("회원을 검색했습니다.");
            return result.get();
        } else {
            // 로그인 ID를 가진 회원이 없는 경우
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
    }

    // 회원 삭제
    public void deleteMember(String loginId) {
        // 먼저 해당 로그인 ID를 가진 회원을 데이터베이스에서 검색
        Optional<Member> member = memberRepository.findByLoginId(loginId);

        if (member.isPresent()) {
            // memberRepository.deleteById(member.get().getId());
            memberRepository.deleteByLoginId(loginId);
            System.out.println("회원을 삭제했습니다.");
        } else {
            // 로그인 ID를 가진 회원이 없는 경우
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
    }
}