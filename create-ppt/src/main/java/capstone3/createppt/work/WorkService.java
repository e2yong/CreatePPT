package capstone3.createppt.work;

import capstone3.createppt.entity.Work;
import capstone3.createppt.file.PathConst;
import capstone3.createppt.generate.GenerateService;
import capstone3.createppt.repository.WorkRepository;
import capstone3.createppt.file.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@Transactional
public class WorkService {

    private final WorkRepository workRepository;
    private final FileService fileService;
    private final GenerateService generateService;

    public WorkService(WorkRepository workRepository, FileService fileService, GenerateService generateService) {
        this.workRepository = workRepository;
        this.fileService = fileService;
        this.generateService = generateService;
    }

    // 작업 DB에 저장
    public Long saveWork(HttpServletRequest request) {
        // 세션 가져오기(없으면 null 반환)
        HttpSession session = request.getSession(false);

        // 세션에서 필요한 값 가져오기
        String title = (String) session.getAttribute("title");
        Long memberId = (Long) session.getAttribute("memberId");
        Long uploadFileId = (Long) session.getAttribute("uploadFileId");
        Long pptFileId = (Long) session.getAttribute("pptFileId");
        Long scriptFileId = (Long) session.getAttribute("scriptFileId");

        // work 객체 생성
        Work work = Work.builder()
                .title(title)
                .memberId(memberId)
                .uploadFile(uploadFileId)
                .pptFile(pptFileId)
                .scriptFile(scriptFileId)
                .build();

        // DB에 작업 저장
        workRepository.save(work);

        return work.getWorkId();
    }

    // memberId와 title로 작업 조회
    public Long findWorkByMemberAndTitle(Long memberId, String title) {
        // 작업이 있는지 DB에서 검색
        Optional<Work> work = workRepository.findByMemberIdAndTitle(memberId, title);

        // 작업이 있으면
        if (work.isPresent()) {
            return work.get().getWorkId();
        } else {
            // 로그인 ID를 가진 회원이 없는 경우
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
    }

    // 회원이 작업한 작업 목록 조회
    public List<Work> findWorksByMemberId(Long memberId) {
        return workRepository.findByMemberId(memberId);
    }

    // 작업 모두 조회
    public List<Work> findWorks() {return workRepository.findAll();}

    // Title로 작업 삭제
    public void deleteWorkByMemberAndTitle(Long memberId, String title) {
        // title로 workId 검색
        Long workId = findWorkByMemberAndTitle(memberId, title);

        // workId로 작업 및 파일 삭제
        deleteWork(workId);
    }

    // 작업 삭제(업로드 파일, PPT 파일, 대본 파일 모두 삭제)
    public void deleteWork(Long workId) {
        // 작업이 있는지 DB에서 검색
        Optional<Work> work = workRepository.findByWorkId(workId);

        // 작업이 있으면 파일들 삭제
        if (work.isPresent()) {
            // 파일 삭제
            fileService.deleteFile(work.get().getUploadFile());
            System.out.println("업로드 파일 삭제");
            fileService.deleteFile(work.get().getPptFile());
            System.out.println("PPT 파일 삭제");
            fileService.deleteFile(work.get().getScriptFile());
            System.out.println("대본 파일 삭제");

            // 작업 삭제
            workRepository.deleteByWorkId(workId);

            System.out.println("작업 삭제 완료");
        }
        // 작업이 없으면
        else {
            System.out.println("작업이 없음");
            log.error("작업이 DB에 없음");
            throw new IllegalArgumentException("작업이 DB에 없음");
        }
    }

    // Title로 작업 다운로드
    public void downloadWorkByMemberAndTitle(Long memberId, String title, HttpServletResponse response) throws Exception {
        // 작업에 있는 파일들 압축
        System.out.println(title + "작업 압축");
        String zipPath = workZip(memberId, title);

        // 압축된 파일 다운로드
        System.out.println(title + "작업 다운로드");
        generateService.downloadZip(zipPath, response);

        // zip 디렉터리 비우기
        System.out.println("압축 파일 삭제");
        File zipDirectory = new File(PathConst.ZIP_PATH);
        FileUtils.cleanDirectory(zipDirectory);
    }

    // Title로 작업 조회하고 압축하기
    public String workZip(Long memberId, String title) throws Exception {
        // 작업이 있는지 DB에서 검색
        System.out.println(title + "작업 압축 시작");
        Optional<Work> work = workRepository.findByMemberIdAndTitle(memberId, title);

        // 작업 안에 있는 파일 번호들
        System.out.println(title + "작업 속 파일 ID 가져오기");
        Long uploadFileId = work.get().getUploadFile();
        Long pptFileId = work.get().getPptFile();
        Long scriptFileId = work.get().getScriptFile();

        // File List에 파일 넣기
        String uploadPath = fileService.findFilePathById(uploadFileId);
        File uploadFile = new File(uploadPath);

        String pptPath = fileService.findFilePathById(pptFileId);
        File pptFile = new File(pptPath);

        String scriptPath = fileService.findFilePathById(scriptFileId);
        File scriptFile = new File(scriptPath);

        List<File> files = new ArrayList<>();
        files.add(uploadFile);
        files.add(pptFile);
        files.add(scriptFile);

        // result.zip 이름으로 파일 객체 생성
        System.out.println(title + "작업 압축 파일 생성");
        File zipFile = new File(PathConst.ZIP_PATH, title + ".zip");

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

        System.out.println(title + "작업 압축 종료");
        return zipFile.getPath();
    }
}
