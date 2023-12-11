package capstone3.createppt.extract;

import capstone3.createppt.file.PathConst;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class CaptionWebClientServiceTest {

    CaptionWebClientService captionService = new CaptionWebClientService();

    @Test
    void post() throws IOException {
        String caption = captionService.post(PathConst.IMAGE_PATH + "test.jpg");

        System.out.println(caption);
    }
}