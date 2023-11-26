package capstone3.createppt.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "docfile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;        // 파일번호, PRIMARY KEY

    @Column(name = "file_name")
    private String fileName;    // 저장된 파일 이름, UNIQUE KEY

    @Column(name = "file_type")
    private String fileType;    // 파일 확장자

    @Column(name = "file_path")
    private String filePath;    // 파일 저장 경로

    @Builder
    public DocFile(String fileName, String fileType, String filePath) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
    }

}
