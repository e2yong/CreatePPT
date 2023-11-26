package capstone3.createppt.testGPT;

import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/chat")
public class ChatGptController {
    private final ChatService chatService;
    private final ChatgptService chatgptService;

    // ChatGPT와 Single Chat
    @PostMapping("/single")
    public String test(@RequestBody String question) {
        return chatService.getChatResponse(question);
    }

    // ChatGPT와 Multi Chat
    @PostMapping("/multi")
    public String testMulti(@RequestBody String question) {
        return chatService.getMultiChatResponse(question);
    }
}
