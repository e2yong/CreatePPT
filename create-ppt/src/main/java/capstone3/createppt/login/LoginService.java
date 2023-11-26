package capstone3.createppt.login;

import capstone3.createppt.entity.Member;
import capstone3.createppt.Repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final MemberRepository memberRepository;

    public LoginService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 로그인
    public Member login(String loginId, String password) {
        /**
         * @return null 로그인 실패
         */
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }

}
