package capstone3.createppt.testGPT;

import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatgptService chatgptService;

    // Single Chat
    public String getChatResponse(String prompt) {
        // ChatGPT에게 질문
        return chatgptService.sendMessage(prompt);
    }

    // Multi Chat
    public String getMultiChatResponse(String prompt) {
        // Multi Message
        List<MultiChatMessage> messages = Arrays.asList(
                new MultiChatMessage("system","You are a helpful assistant."),
                new MultiChatMessage("user", prompt),
                new MultiChatMessage("assistant","The Los Angeles Dodgers won the World Series in 2020."),
                new MultiChatMessage("user","Where was it played?"));
        return chatgptService.multiChat(messages);
    }
}
