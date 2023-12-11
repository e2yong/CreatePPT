package capstone3.createppt.generate;

import capstone3.createppt.file.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

import static capstone3.createppt.file.PathConst.RESULT_PATH;
import static capstone3.createppt.file.PathConst.ZIP_PATH;

@Slf4j
@Controller
public class GenerateController {
    private final FileService fileService;
    private final GenerateService generateService;

    public GenerateController(FileService fileService, GenerateService generateService) {
        this.fileService = fileService;
        this.generateService = generateService;
    }

    // 요약문 생성
    @GetMapping("/generate/summary")
    public String generateSummary() {
        return "generate/generateSummary";
    }

    @PostMapping("/generate/summary")
    public String summary() {
        return "redirect:/generate/ppt";
    }

    // ppt 생성
    @GetMapping("/generate/ppt")
    public String generatePpt() {
        return "generate/generatePpt";
    }

    @PostMapping("/generate/ppt")
    public String ppt() {
        return "redirect:/generate/result";
    }

    // 결과 다운로드
    @GetMapping("/generate/result")
    public String generateResult() {
        return "generate/result";
    }

    @PostMapping("/generate/download")
    public String downloadPpt(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 세션 가져오기(없으면 null 반환)
        HttpSession session = request.getSession(false);
        String filename = (String)session.getAttribute("filename");

        // 다운받을 ppt 파일
        String pptName = filename + "_ppt.pptx";
        String pptPath = RESULT_PATH + pptName;

        // 다운받을 대본 파일
        String scriptName = filename + "_script.docx";
        String scriptPath = RESULT_PATH + scriptName;

        // result.zip으로 압축하기
        generateService.resultZip(pptPath, scriptPath);
        String zipPath = ZIP_PATH + "result.zip";

        // 다운받기
        generateService.downloadResult(zipPath, response);

        return "redirect:/generate/download";
    }

    @PostMapping("/generate/end")
    public String endGenerate() throws IOException {
        // 작업한 extract, image, zip 폴더 비우기
        generateService.cleanWorkDirectory();

        return "redirect:/";
    }
}
