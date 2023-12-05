package capstone3.createppt.extract;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.net.URI;

@Service
@Slf4j
public class CaptionWebClientService {
    public String post(String filePath) throws IOException {

        URI uri = URI.create("http://localhost:8000/caption");

        // File to MultipartFile
        File file = new File(filePath);
        Resource image = new FileSystemResource(file);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", image);

        MultiValueMap<String, HttpEntity<?>> multipartBody = builder.build();

        // 요청 보내기
        String response = WebClient.create()
                .method(HttpMethod.POST)
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(multipartBody)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> ex.getStatusCode().value() == 404 ? Mono.empty() : Mono.error(ex))
                .block();

        return captionParsing(response);
    }

    public String captionParsing(String response) {
        JSONObject jsonObject = new JSONObject(response);
        String caption = jsonObject.getString("caption");

        return caption;
    }
}
