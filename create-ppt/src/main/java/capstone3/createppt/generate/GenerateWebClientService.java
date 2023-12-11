package capstone3.createppt.generate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Service
public class GenerateWebClientService {

    public String postSummary(String extractFileName) {

        URI uri = URI.create("http://localhost:8000/summary");

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("filename", extractFileName);

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

        return response;
    }

    public String postPpt(String pptTxtFileName) {

        URI uri = URI.create("http://localhost:8000/ppt");

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("filename", pptTxtFileName);

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

        return response;
    }
}
