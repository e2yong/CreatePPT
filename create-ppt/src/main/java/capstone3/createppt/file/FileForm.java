package capstone3.createppt.file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class FileForm {

    private String title;
    private MultipartFile file;

}