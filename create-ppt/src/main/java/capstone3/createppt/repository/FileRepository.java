package capstone3.createppt.repository;

import capstone3.createppt.entity.DocFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<DocFile, Long> {

    // 파일 조회
    Optional<DocFile> findByFileId(Long fileId);
    Optional<DocFile> findByFileName(String fileName);

    // 파일 삭제
    void deleteByFileId(Long fileId);
    void deleteByFileName(String fileName);
}
