package capstone3.createppt.work;

import capstone3.createppt.entity.Member;
import capstone3.createppt.entity.Work;
import capstone3.createppt.login.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
public class WorkController {

    private final WorkService workService;

    @Autowired
    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    // 작업 목록 조회
    @GetMapping("/works")
    public String list(Model model, HttpServletRequest request) {
        // 세션 가져오기(없으면 null 반환)
        HttpSession session = request.getSession(false);

        // 세션이 존재하면 로그인 정보 가져오기
        Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
        model.addAttribute("member", loginMember);
        Long memberId = loginMember.getId();

        // 로그인 회원의 작업 목록 조회
        List<Work> works = workService.findWorksByMemberId(memberId);
        model.addAttribute("works", works);

        return "works/workList";
    }

    // 작업 삭제
    @PostMapping("/works/delete")
    public String delete(@RequestParam("deleteName") String deleteName, HttpServletRequest request) {
        // 세션 가져오기(없으면 null 반환)
        HttpSession session = request.getSession(false);

        // 세션이 존재하면 로그인 정보 가져오기
        Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
        Long memberId = loginMember.getId();
        String title = deleteName;

        try {
            // 파일 삭제
            workService.deleteWorkByMemberAndTitle(memberId, title);
            return "redirect:/works";
        } catch (IllegalArgumentException e) {
            // 파일이 존재하지 않는 경우
            return "redirect:/works";
        }
    }

    // 작업 다운로드
    @PostMapping("/works/download")
    public String download(@RequestParam("downloadName") String downloadName,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        // 세션 가져오기(없으면 null 반환)
        HttpSession session = request.getSession(false);

        // 세션이 존재하면 로그인 정보 가져오기
        Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
        Long memberId = loginMember.getId();
        String title = downloadName;

        try {
            // 파일 다운로드
            workService.downloadWorkByMemberAndTitle(memberId, title, response);

            return "redirect:/works";
        } catch (IllegalArgumentException e) {
            // 파일이 존재하지 않는 경우
            return "redirect:/works";
        } catch (Exception e) {
            // 다운로드 오류 처리
            return "redirect:/works";
        }
    }
}
