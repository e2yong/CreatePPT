package capstone3.createppt.extract;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static capstone3.createppt.file.PathConst.*;

// Word(.docx) 파일을 Text(.txt) 파일로 변환 및 저장
public class ExtractText {

    // 워드 파일에서 텍스트 추출
    public static void extractText(String inputFilePath, String outputFilePath) {
        try {
            // 파일 입출력 스트림
            FileInputStream fis = new FileInputStream(inputFilePath);
            FileOutputStream fos = new FileOutputStream(outputFilePath);

            // 워드(.docx) 파일 처리
            XWPFDocument file = new XWPFDocument(OPCPackage.open(fis));
            XWPFWordExtractor ext = new XWPFWordExtractor(file);

            // 텍스트 추출
            System.out.println("==== docx text extractor ====");
            String text = ext.getText();

            // 파일 저장
            fos.write(text.getBytes());

            // 파일 입출력 스트림 닫기
            fis.close();
            fos.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // 워드 파일에서 텍스트와 이미지 추출
    public static void extractTextAndImage(String inputFilePath, String outputFilePath) {
        try {
            // 파일 입출력 스트림
            FileInputStream fis = new FileInputStream(inputFilePath);
            FileOutputStream fos = new FileOutputStream(outputFilePath);

            // 워드(.docx) 파일 처리
            XWPFDocument docx = new XWPFDocument(OPCPackage.open(fis));
            // 텍스트 추출을 위한 StringBuilder
            StringBuilder text = new StringBuilder();
            int imageCounter = 1;   // 이미지 순서

            // 문단과 런을 순회하며 텍스트 추출
            for (XWPFParagraph paragraph : docx.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    // 만약 이미지가 있으면
                    if (run.getEmbeddedPictures() != null && !run.getEmbeddedPictures().isEmpty()) {
                        // 이미지 캡셔닝
                        for (XWPFPicture picture : run.getEmbeddedPictures()) {
                            // XWPFPicureData로 변환
                            XWPFPictureData image = picture.getPictureData();

                            // 이미지 파일 이름 생성(image1.jpg)
                            String imageName = "image" + imageCounter++ + "." + image.suggestFileExtension();

                            // 이미지 서버에 저장
                            byte[] imageBytes = image.getData();
                            File imageFile = new File(IMAGE_PATH, imageName);
                            FileUtils.writeByteArrayToFile(imageFile, imageBytes);

                            // 이미지 캡셔닝
                            CaptionWebClientService captionService = new CaptionWebClientService();
                            String caption = captionService.post(imageFile.getPath());

                            // 텍스트 파일에 이미지 정보 추가
                            text.append("\n<image: " + imageName + ", caption: " + caption + ", path: " + IMAGE_PATH + imageName + ">\n");
                        }
                    }
                    // 이미지가 없으면
                    else {
                        text.append(run.getText(0));
                    }
                }
                text.append("\n");
            }

            // 텍스트 파일 저장
            System.out.println("==== docx text extractor ====");
            FileUtils.writeStringToFile(new File(outputFilePath), text.toString(), "UTF-8");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}