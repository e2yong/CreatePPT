package capstone3.createppt.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "work")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Work {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_id")
    private Long workId;            // 작업 번호, PRIMARY KEY

    @Column(name = "title")
    private String title;           // 발표 주제

    @Column(name = "member_id")
    private Long memberId;          // 회원 번호

    @Column(name = "upload_file")
    private Long uploadFile;        // 업로드 파일 번호

    @Column(name = "ppt_file")
    private Long pptFile;           // PPT 파일 번호

    @Column(name = "script_file")
    private Long scriptFile;        // 대본 파일 번호

    @Builder
    public Work(String title, Long memberId, Long uploadFile, Long pptFile, Long scriptFile) {
        this.title = title;
        this.memberId = memberId;
        this.uploadFile = uploadFile;
        this.pptFile = pptFile;
        this.scriptFile = scriptFile;
    }
}
