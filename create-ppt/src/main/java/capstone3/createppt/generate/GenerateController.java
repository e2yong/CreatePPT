package capstone3.createppt.generate;

import capstone3.createppt.entity.DocFile;
import capstone3.createppt.file.FileService;
import capstone3.createppt.work.WorkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.File;
import java.io.IOException;

import static capstone3.createppt.file.PathConst.RESULT_PATH;
import static capstone3.createppt.file.PathConst.ZIP_PATH;

@Slf4j
@Controller
public class GenerateController {
    private final FileService fileService;
    private final WorkService workService;
    private final GenerateService generateService;
    private final GenerateWebClientService webClientService;

    @Autowired
    public GenerateController(FileService fileService, WorkService workService, GenerateService generateService, GenerateWebClientService webClientService) {
        this.fileService = fileService;
        this.workService = workService;
        this.generateService = generateService;
        this.webClientService = webClientService;
    }

    // 요약문 생성
    @GetMapping("/generate/summary")
    public String generateSummary() {
        return "generate/generateSummary";
    }

    @PostMapping("/generate/summary")
    public String summary(HttpServletRequest request) {
        // 세션 가져오기(없으면 null 반환)
        HttpSession session = request.getSession(false);
        String filename = (String)session.getAttribute("filename");

        // 요약문 및 대본을 생성할 추출 파일
        System.out.println("요약문 및 대본 생성");
        String extractFile = filename + "_extract.txt";
        // 요약문 및 대본 생성
        String jsonString = webClientService.postSummary(extractFile);

        // 대본 파일
        System.out.println("대본 파일을 DB에 저장");
        String scriptName = filename + "_script.docx";
        String scriptPath = RESULT_PATH + scriptName;
        File scriptFile = new File(scriptPath);

        // 대본 파일 DB에 저장
        DocFile docFile = DocFile.builder()
                .fileName(scriptName)
                .fileType("docx")
                .filePath(scriptPath)
                .build();

        // 대본 파일 번호 세션에 저장
        Long scriptFileId = fileService.saveFile(docFile);
        session.setAttribute("scriptFileId", scriptFileId);

        return "redirect:/generate/ppt";
    }

    // ppt 생성
    @GetMapping("/generate/ppt")
    public String generatePpt() {
        return "generate/generatePpt";
    }

    @PostMapping("/generate/ppt")
    public String ppt(HttpServletRequest request) {
        // 세션 가져오기(없으면 null 반환)
        HttpSession session = request.getSession(false);
        String filename = (String)session.getAttribute("filename");

        // PPT 생성용 텍스트 파일
        String pptTxtFile = filename + "_pptx.txt";
        // PPT 생성
        // String jsonString = webClientService.postPpt(pptTxtFile);

        // PPT 파일
        System.out.println("PPT 파일을 DB에 저장");
        String pptName = filename + "_ppt.pptx";
        String pptPath = RESULT_PATH + pptName;

        // 대본 파일 DB에 저장
        DocFile docFile = DocFile.builder()
                .fileName(pptName)
                .fileType("pptx")
                .filePath(pptPath)
                .build();

        // 대본 파일 번호 세션에 저장
        Long pptFileId = fileService.saveFile(docFile);
        session.setAttribute("pptFileId", pptFileId);

        // 작업한 내용 DB에 저장
        workService.saveWork(request);

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
