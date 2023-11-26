package capstone3.createppt.member;

import capstone3.createppt.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원 생성
    @GetMapping("/members/new")
    public String creatForm() {
        return "members/createMemberForm";
    }

    // 회원 생성
    @PostMapping("/members/new")
    public String create(MemberDto memberDto) {
        try {
            // 회원 가입
            memberService.join(memberDto);
        } catch (IllegalStateException e) {
            // 중복 로그인 ID로 회원 가입하는 경우
            return "redirect:/members/new";
        }

        return "redirect:/";
    }

    // 회원 조회
    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        return "members/memberList";
    }

    // 회원 삭제
    @PostMapping("/members/delete")
    public String delete(@RequestParam("loginId") String loginId) {
        try {
            // 회원 삭제
            memberService.deleteMember(loginId);
            return "redirect:/members";
        } catch (IllegalArgumentException e) {
            // 입력한 회원이 존재하지 않는 경우
            return "redirect:/members";
        }
    }
}
