package capstone3.createppt.file;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class Word2TextTest {

    // 파일 저장 경로
    private final String path = "C:/Users/e2yon/Documents/CreatePPT/CreatePPT/create-ppt/src/main/";
    private final String inputPath = "resources/storage/upload/";
    private final String outputPath = "resources/storage/extract/";
    private final String imagePath = "resources/storage/extract/image/";
    private final String input = "duple_upload.docx";
    private final String output1 = "duple_extract1.txt";
    private final String output2 = "duple_extract2.txt";

    @Test
    void extract() {
        String inputFilePath = path+inputPath+input; // 실제 Word 파일 경로로 변경하세요
        String outputFilePath = path+outputPath+output1; // 원하는 출력 경로로 변경하세요
        String imageSavePath = path + imagePath;

        Word2Text.extractText(inputFilePath, outputFilePath);
        // Word2Text.extractTextAndImage(inputFilePath, outputFilePath, imageSavePath);
    }
}