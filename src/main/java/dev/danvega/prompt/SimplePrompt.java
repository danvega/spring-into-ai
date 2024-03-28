package dev.danvega.prompt;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class SimplePrompt {

    private final ChatClient chatClient;

    public SimplePrompt(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/api/simple-prompt")
    public String simple() {
        return chatClient.call(
                new Prompt("How long has The Java Programming language been around?"))
                .getResult().getOutput().getContent();
    }
}
