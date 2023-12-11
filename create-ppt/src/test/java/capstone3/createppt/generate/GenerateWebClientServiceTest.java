package capstone3.createppt.generate;

import org.junit.jupiter.api.Test;

class GenerateWebClientServiceTest {

    @Test
    void postSummary() {
        GenerateWebClientService generateService = new GenerateWebClientService();

        String result = generateService.postSummary("test_TEST_extract.txt");

        System.out.println(result);
    }
}