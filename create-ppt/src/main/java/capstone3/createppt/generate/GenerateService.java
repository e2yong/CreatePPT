package capstone3.createppt.generate;

import capstone3.createppt.file.PathConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@Transactional
public class GenerateService {

    // 종료 직전 작업했던 폴더 비우기
    public void cleanWorkDirectory() throws IOException {
        // 사용 중인 파일 종료
        System.gc();

        // extract 파일 비우기
        File extractDirectory = new File(PathConst.EXTRACT_PATH);
        FileUtils.cleanDirectory(extractDirectory);

        // image 파일 비우기
        File imageDirectory = new File(PathConst.IMAGE_PATH);
        FileUtils.cleanDirectory(imageDirectory);

        // zip 파일 비우기
        File zipDirectory = new File(PathConst.ZIP_PATH);
        FileUtils.cleanDirectory(zipDirectory);
    }

    // 결과 다운로드
    public void downloadResult(String zipPath, HttpServletResponse response) {
        File file = new File(zipPath);

        if (file.exists()) {
            try {
                // 다운로드할 파일의 콘텐츠 타입 설정
                response.setContentType("application/octet-stream");
                // 다운로드할 파일의 크기 설정
                response.setContentLength((int) file.length());
                // 브라우저가 파일을 다운로드하도록 지시하는 헤더 설정
                response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

                // 파일을 읽기 위한 입력 스트릠 생성
                FileInputStream fileInputStream = new FileInputStream(file);
                // HTTP 응답의 출력 스트림 가져오기
                OutputStream responseOutputStream = response.getOutputStream();
                // 파일을 읽어서 HTTP 응답으로 복사
                FileCopyUtils.copy(fileInputStream, responseOutputStream);

                // 사용한 스트림 닫기
                fileInputStream.close();
                responseOutputStream.close();

                log.info("파일 다운로드 완료");
            } catch (IOException e) {
                log.error("파일 다운로드 중 오류 발생", e);
                throw new RuntimeException("파일 다운로드 중 오류 발생");
            }
        } else {
            log.error("파일이 서버에 존재하지 않음");
            throw new IllegalArgumentException("파일이 서버에 존재하지 않습니다.");
        }
    }

    // 결과 압축
    public String resultZip(String pptPath, String scriptPath) throws Exception {
        // 압축할 파일들 객체 생성
        File pptFile = new File(pptPath);
        File scriptFile = new File(scriptPath);

        List<File> files = new ArrayList<>();
        files.add(pptFile);
        files.add(scriptFile);

        // result.zip 이름으로 파일 객체 생성
        File zipFile = new File(PathConst.ZIP_PATH, "result.zip");

        // 스트림에 사용 할 byte 지정
        byte[] buf = new byte[4096];

        // result.zip 생성
        try (ZipOutputStream out =
                     new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (File file : files) {
                // 파일 객체를 통해 FileInputStream 객체 생성
                try (FileInputStream in = new FileInputStream(file)){
                    ZipEntry ze = new ZipEntry(file.getName());
                    out.putNextEntry(ze);

                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    out.closeEntry();
                }
            }
        }

        return zipFile.getPath();
    }
}