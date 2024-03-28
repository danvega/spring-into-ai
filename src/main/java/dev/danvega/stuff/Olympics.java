package dev.danvega.stuff;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/olympics")
public class Olympics {

    private final ChatClient chatClient;
    @Value("classpath:/docs/olympic-sports.txt")
    private Resource docsToStuffResource;
    @Value("classpath:/prompts/olympic-sports.st")
    private Resource olympicSportsResource;

    public Olympics(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/2024")
    public String get2024OlympicSports(
            @RequestParam(value = "message", defaultValue = "What sports are being included in the 2024 Summer Olympics?") String message,
            @RequestParam(value = "stuffit", defaultValue = "false") boolean stuffit
    ) {

        PromptTemplate promptTemplate = new PromptTemplate(olympicSportsResource);
        Map<String,Object> map  = new HashMap<>();
        map.put("question",message);
        if(stuffit) {
            map.put("context", docsToStuffResource);
        } else {
            map.put("context", "");
        }

        Prompt prompt = promptTemplate.create(map);
        ChatResponse response = chatClient.call(prompt);

        return response.getResult().getOutput().getContent();
    }
}
