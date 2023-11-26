package capstone3.createppt;

import capstone3.createppt.login.LoginService;
import capstone3.createppt.login.SessionConst;
import capstone3.createppt.login.LoginForm;
import capstone3.createppt.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    private LoginService loginService;

    public HomeController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    // 도메인/으로 들어오면 호출
    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        // 세션 가져오기
        HttpSession session = request.getSession(false);

        // 세션이 없으면 로그인 페이지로 이동
        if (session == null) {
            System.out.println("세션이 없음");
            return "login";
        }

        // 세션이 존재하면 로그인 정보 가져오기
        Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        // 로그인되어 있지 않으면 로그인 페이지로 이동
        if (loginMember == null) {
            System.out.println("로그인 페이지로");
            return "login";
        }

        //세션이 유지되면 홈으로 이동
        model.addAttribute("member", loginMember);
        System.out.println("홈 화면으로");
        return "home";
    }

    // 로그인
    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request) {
        // 유효성 검사
        if (bindingResult.hasErrors()) {
            return "login"; // 실패하면 다시 로그인 페이지
        }

        // 로그인 시도
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        // 로그인 실패 시
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            System.out.println("로그인 실패");
            return "login";
        }

        // 관리자일 경우
        if (loginMember.getLoginId().equals("admin")) {
            System.out.println("관리자입니다.");
            return "redirect:/admin";
        }

        // 로그인 성공하면 세션 가져오기(없으면 새로운 세션 생성)
        HttpSession session = request.getSession();
        // 세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        System.out.println("로그인 성공: " + loginMember.getId() + " " + loginMember.getLoginId());
        return "redirect:/";
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        // 세션 가져오기(없으면 null 반환)
        HttpSession session = request.getSession(false);

        // 세션이 존재하면 세션 무효화
        if (session != null) {
            session.invalidate();
        }

        // 로그아웃 성공
        System.out.println("로그아웃");
        // return "login";
        return "redirect:/";
    }
}