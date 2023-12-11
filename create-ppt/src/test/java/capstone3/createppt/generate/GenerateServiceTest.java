package capstone3.createppt.generate;

import capstone3.createppt.file.PathConst;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenerateServiceTest {

    @Test
    void clean() throws Exception {
        GenerateService service = new GenerateService();

        service.cleanWorkDirectory();
    }

    @Test
    void zip() throws Exception {
        GenerateService service = new GenerateService();

        String pptPath = PathConst.RESULT_PATH + "test_TEST_ppt.pptx";
        String scriptPath = PathConst.RESULT_PATH + "test_TEST_script.docx";

        service.resultZip(pptPath, scriptPath);
    }
}