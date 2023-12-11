package capstone3.createppt.work;

import capstone3.createppt.entity.Work;
import capstone3.createppt.repository.WorkRepository;
import capstone3.createppt.file.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class WorkService {

    private final WorkRepository workRepository;
    private final FileService fileService;

    public WorkService(WorkRepository workRepository, FileService fileService) {
        this.workRepository = workRepository;
        this.fileService = fileService;
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

    // 작업 모두 조회
    public List<Work> findFiles() {return workRepository.findAll();}

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
}
