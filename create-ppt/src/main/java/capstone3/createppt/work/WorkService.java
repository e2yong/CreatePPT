package capstone3.createppt.work;

import capstone3.createppt.entity.Work;
import capstone3.createppt.Repository.WorkRepository;
import capstone3.createppt.file.FileService;
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

    // 작업 모두 조회
    public List<Work> finFiles() {return workRepository.findAll();}

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
