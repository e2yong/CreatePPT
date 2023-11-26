package capstone3.createppt.file;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

// Word(.docx) 파일을 Text(.txt) 파일로 변환 및 저장
public class Word2Text {

    // Word 파일에서 텍스트 추출
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
    public static void extractTextAndImage(String inputFilePath, String outputFilePath, String imagePath) {
        try {
            // 파일 입출력 스트림
            FileInputStream fis = new FileInputStream(inputFilePath);
            FileOutputStream fos = new FileOutputStream(outputFilePath);

            // 워드(.docx) 파일 처리
            XWPFDocument docx = new XWPFDocument(OPCPackage.open(fis));
            // 워드의 모든 이미지 데이터 추출
            List<XWPFPictureData> images = docx.getAllPictures();
            int imageCounter = 1;   // 이미지 순서

            // 텍스트 추출을 위한 StringBuilder
            StringBuilder text = new StringBuilder();

            // 문단과 런을 순회하며 텍스트 추출
            for (XWPFParagraph paragraph : docx.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    text.append(run.getText(0));
                }
            }

            // 이미지 처리
            for (XWPFPictureData image : images) {
                // 이미지를 서버에 저장
                String imageName = "image" + imageCounter++ + "." + image.suggestFileExtension();
                byte[] imageBytes = image.getData();
                File imageFile = new File(imagePath, imageName);
                FileUtils.writeByteArrayToFile(imageFile, imageBytes);

                // 텍스트에 이미지 정보 추가
                // 나중에 이미지 캡셔닝 적용
                text.append("<" + imageName + ", " + imagePath + ">");
            }

            // 파일 저장
            System.out.println("==== docx text extractor ====");
            FileUtils.writeStringToFile(new File(outputFilePath), text.toString(), "UTF-8");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // 이미지 캡셔닝으로 이미지를 텍스트로 변환
}