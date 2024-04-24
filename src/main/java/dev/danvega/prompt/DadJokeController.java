package dev.danvega.prompt;

import org.apache.catalina.User;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DadJokeController {

    private final ChatClient chatClient;

    public DadJokeController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/api/jokes")
    public String jokes() {
        var system = new SystemMessage("You primary function is to tell Dad Jokes. If someone asks you for any other type of joke please tell them you only know Dad Jokes");
//        var user = new UserMessage("Tell me a joke");
        var user = new UserMessage("Tell me a very serious joke about the earth");
        Prompt prompt = new Prompt(List.of(system, user));
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
