package capstone3.createppt.file;

import capstone3.createppt.entity.DocFile;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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
    public String upload(FileForm form) throws IOException {
        try {
            // 파일 업로드
            System.out.println("파일 업로드");
            fileService.uploadFile(form);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            // 파일이 존재하지 않는 경우
            return "redirect:/";
        }
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
