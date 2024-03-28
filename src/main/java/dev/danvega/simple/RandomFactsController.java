package dev.danvega.simple;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RandomFactsController {

    private final ChatClient chatClient;

    public RandomFactsController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/random-fact")
    public String getRandomFact(@RequestParam(value = "topic", defaultValue = "sports") String topic) {
        PromptTemplate promptTemplate = new PromptTemplate("Tell me a random fact about {topic}");
        Prompt prompt = promptTemplate.create(Map.of("topic",topic));
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
