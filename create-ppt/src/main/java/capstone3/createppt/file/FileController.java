package capstone3.createppt.file;

import capstone3.createppt.entity.DocFile;
import capstone3.createppt.entity.Member;
import capstone3.createppt.login.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static capstone3.createppt.extract.ExtractText.extractTextAndImage;
import static capstone3.createppt.file.PathConst.EXTRACT_PATH;
import static capstone3.createppt.file.PathConst.UPLOAD_PATH;

@Slf4j
@Controller
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // 파일 업로드
    @GetMapping("/files/upload")
    public String uploadForm() {
        return "files/uploadFileForm";
    }

    @PostMapping("/files/upload")
    public String upload(HttpServletRequest request, FileForm form) throws IOException {
        // 세션 가져오기(없으면 null 반환)
        HttpSession session = request.getSession(false);

        // 세션이 존재하면 로그인 정보 가져오기
        Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
        Long memberId = loginMember.getId();

        String loginId = loginMember.getLoginId();
        System.out.println("파일을 업로드하는 사용자: " + loginId);

        String title = form.getTitle();
        System.out.println("작업할 발표 제목: " + title);

        String filename = loginId + "_" + title;
        session.setAttribute("filename", filename);

        try {
            // 파일 업로드
            System.out.println("파일 업로드");
            Long uploadFileId = fileService.uploadFile(loginId, form);

            // 회원 번호, 업로드 파일 번호 세션에 저장
            session.setAttribute("memberId", memberId);
            session.setAttribute("title", title);
            session.setAttribute("uploadFileId", uploadFileId);

            return "redirect:/files/extract";
        } catch (IllegalArgumentException e) {
            // 파일이 존재하지 않는 경우
            return "redirect:/";
        }
    }

    // 파일 추출
    @GetMapping("/files/extract")
    public String extractFile() {
        return "files/extractFile";
    }

    @PostMapping("/files/extract")
    public String extract(HttpServletRequest request){
        // 세션 가져오기(없으면 null 반환)
        HttpSession session = request.getSession(false);
        String filename = (String)session.getAttribute("filename");

        // 파일 추출
        String inputFile = UPLOAD_PATH + filename + "_upload.docx";
        String outputFile = EXTRACT_PATH + filename + "_extract.txt";
        extractTextAndImage(inputFile, outputFile);

        return "redirect:/generate/summary";
    }

    // 파일 목록 조회
    @GetMapping("/files")
    public String list(Model model) {
        List<DocFile> files = fileService.findFiles();
        model.addAttribute("files", files);

        return "files/fileList";
    }

    // 파일 삭제
    @PostMapping("/files/delete")
    public String delete(@RequestParam("deleteName") String deleteName) {
        try {
            // 파일 삭제
            fileService.deleteFile(deleteName);
            return "redirect:/files";
        } catch (IllegalArgumentException e) {
            // 파일이 존재하지 않는 경우
            return "redirect:/files";
        }
    }

    // 파일 다운로드
    @PostMapping("/files/download")
    public String download(@RequestParam("downloadName") String downloadName,
                           HttpServletResponse response) {
        try {
            // 파일 다운로드
            fileService.downloadFile(downloadName, response);
            return "redirect:/files";
        } catch (IllegalArgumentException e) {
            // 파일이 존재하지 않는 경우
            return "redirect:/files";
        } catch (Exception e) {
            // 다운로드 오류 처리
            return "redirect:/files";
        }
    }
}
