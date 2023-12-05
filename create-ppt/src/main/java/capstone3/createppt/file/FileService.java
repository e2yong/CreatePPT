package capstone3.createppt.file;

import capstone3.createppt.entity.DocFile;
import capstone3.createppt.extract.ExtractText;
import capstone3.createppt.repository.FileRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import static capstone3.createppt.extract.ExtractText.*;
import static capstone3.createppt.file.PathConst.*;

@Service
@Slf4j
@Transactional
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    // 파일 업로드
    public String uploadFile(FileForm form) throws IOException {

        String title = form.getTitle();
        MultipartFile file = form.getFile();

        if (!file.isEmpty()) {
            // 원본 파일 이름(확장자 포함)
            String orgFileName = file.getOriginalFilename();

            // 파일 확장자
            String fileType = orgFileName.substring(orgFileName.lastIndexOf(".") + 1);
            // 확장자가 .docx가 아닐 경우
            if (!"docx".equalsIgnoreCase(fileType)){
                // 에러 처리
                log.error("잘못된 파일 확장자");
                throw new IllegalArgumentException("잘못된 파일 확장자");
            }

            // 업로드할 파일 이름
            String fileName = title + "_upload." + fileType;

            // 업로드 파일 경로
            String filePath = UPLOAD_PATH + fileName;

            // 빌더
            DocFile docFile = DocFile.builder()
                    .fileName(fileName)
                    .fileType(fileType)
                    .filePath(filePath)
                    .build();

            // 중복 파일 검증
            validateDuplicateFile(docFile);

            // 파일 저장
            saveFile(file, docFile);

            // 파일 추출
            String inputFile = UPLOAD_PATH + fileName;
            String outputFile = EXTRACT_PATH + title + "_extract.txt";
            // extractText(inputFile, outputFile);
            extractTextAndImage(inputFile, outputFile);

            return fileName;
        } else {
            // 에러 처리
            log.error("업로드된 파일이 없음");
            throw new IllegalArgumentException("업로드된 파일이 없음");
        }
    }

    // 파일 저장(업로드 파일)
    public Long saveFile(MultipartFile file, DocFile docFile) throws IOException {
        // 파일 DB에 저장
        fileRepository.save(docFile);

        // 파일을 서버에 저장
        file.transferTo(new File(docFile.getFilePath()));

        // 파일 저장 후 파일 번호 반환
        return docFile.getFileId();
    }

    // 중복 파일 검증
    private void validateDuplicateFile(DocFile file) {
        Optional<DocFile> result = fileRepository.findByFileName(file.getFileName());

        result.ifPresent(m -> {
            throw new IllegalStateException("이미 존재하는 파일입니다.");
        });
    }



    // 파일 모두 조회
    public List<DocFile> findFiles() {
        return fileRepository.findAll();
    }



    // 파일 삭제(파일 이름으로)
    public void deleteFile(String fileName) {
        // 파일이 있는지 데이터베이스에서 검색
        Optional<DocFile> docFile = fileRepository.findByFileName(fileName);

        // 파일 삭제
        if (docFile.isPresent()) {
            // 파일을 DB에서 삭제
            fileRepository.deleteByFileName(fileName);

            // 파일을 서버에서 삭제
            File file = new File(docFile.get().getFilePath());
            if (file.delete()) {
                log.info("파일 삭제");
            } else {
                log.error("파일 삭제 실패");
                throw new IllegalArgumentException("파일 삭제 실패");
            }

            System.out.println("파일 삭제 완료");
        } else {
            // 파일이 DB에 없을 경우
            log.error("파일이 DB에 없음");
            throw new IllegalArgumentException("파일이 DB에 없음");
        }
    }

    // 파일 삭제(파일 번호로)
    public void deleteFile(Long fileId) {
        // 파일이 있는지 데이터베이스에서 검색
        Optional<DocFile> docFile = fileRepository.findByFileId(fileId);

        // 파일 삭제
        if (docFile.isPresent()) {
            // 파일을 DB에서 삭제
            fileRepository.deleteByFileId(fileId);

            // 파일을 서버에서 삭제
            File file = new File(docFile.get().getFilePath());
            if (file.delete()) {
                log.info("파일 삭제");
            } else {
                log.error("파일 삭제 실패");
                throw new IllegalArgumentException("파일 삭제 실패");
            }

            System.out.println("파일 삭제 완료");
        } else {
            // 파일이 DB에 없을 경우
            log.error("파일이 DB에 없음");
            throw new IllegalArgumentException("파일이 DB에 없음");
        }
    }



    // 파일 다운로드
    public void downloadFile(String fileName, HttpServletResponse response) {
        // 파일이 있는지 데이터베이스에서 검색
        Optional<DocFile> docFile = fileRepository.findByFileName(fileName);

        // 파일 다운로드
        if (docFile.isPresent()) {
            File file = new File(docFile.get().getFilePath());

            if (file.exists()) {
                try {
                    // 다운로드할 파일의 콘텐츠 타입 설정
                    response.setContentType("application/octet-stream");
                    // 다운로드할 파일의 크기 설정
                    response.setContentLength((int) file.length());
                    // 브라우저가 파일을 다운로드하도록 지시하는 헤더 설정
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + docFile.get().getFileName() + "\"");

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
        } else {
            // 파일이 DB에 없을 경우
            log.error("파일이 데이터베이스에 존재하지 않음");
            throw new IllegalArgumentException("파일이 데이터베이스에 존재하지 않습니다.");
        }
    }

}
