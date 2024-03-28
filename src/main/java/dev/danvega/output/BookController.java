package dev.danvega.output;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final ChatClient chatClient;

    public BookController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/craig")
    public String getBooksByCraig() {
        String promptMessage = """
                Generate a list of books written by the author {author}.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(promptMessage, Map.of("author","Craig Walls"));
        Prompt prompt = promptTemplate.create();
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }

    @GetMapping("/by-author")
    public Author getBooksByAuthor(@RequestParam(value = "author", defaultValue = "Ken Kousen") String author) {
        var outputParser = new BeanOutputParser<>(Author.class);
        String format = outputParser.getFormat();
        System.out.println("format = " + format);

        String promptMessage = """
                Generate a list of books written by the author {author}.
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(promptMessage, Map.of("author",author,"format", format));
        Prompt prompt = promptTemplate.create();

        Generation generation = chatClient.call(prompt).getResult();
        Author authorResult = outputParser.parse(generation.getOutput().getContent());
        return authorResult;
    }

}
