# Spring Into AI

It's impossible to spend a day in tech without hearing the words "Artificial Intelligence". In this presentation we will embark on a journey into the world of Artificial Intelligence (AI) specifically designed for beginners. We'll start by introducing the fundamental concepts of AI, demystifying its jargon, and exploring its potential impact on our everyday lives.

Next, we will explore Spring AI. Its goal is to simplify the development of applications that incorporate artificial intelligence functionality, without introducing unnecessary complexity. We will cover the basics of setting up a Spring AI project, how to integrate it with existing Spring Boot applications, and how to use its various components to implement common AI tasks.

Whether you want to add chatbots to your app, generate recommendations, or analyze sentiments in text, Spring AI provides a streamlined and efficient approach to integrating these features. By the end of this talk, you will have a solid grasp of AI basics and how to incorporate them into your Spring applications using Spring AI.

## Agenda 

- Getting Started Demo
- Prompts Demo
  - SimplePrompt
  - DadJokesController
  - YouTube
- OutputParser
- Bring You Own Data
  - Stuff the Prompt 
  - RAG Demo
  - Function

## Getting Started Demo

In this demo you will create a simple `ChatController` that can send a message to OpenAI.

```java
@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/api/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a Dad Joke") String message) {
        return Map.of("generation",chatClient.call(message));
    }

}
```

Request 

```
http :8080/api/generate
```

Response 

```
HTTP/1.1 200
Connection: keep-alive
Content-Type: application/json
Date: Wed, 27 Mar 2024 20:43:20 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "generation": "Why don't scientists trust atoms?\n\nBecause they make up everything!"
}
```

## Prompts Demo


### SimplePrompt

Walk through the Prompt class and its different constructors. 

```java
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
```

### DadJokesController

Show the different types of messages

```java
@RestController
public class DadJokeController {

    private final ChatClient chatClient;

    public DadJokeController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/api/jokes")
    public String jokes() {
        var system = new SystemMessage("You primary function is to tell Dad Jokes. If someone asks you for any other type of joke please tell them you only know Dad Jokes");
        var user = new UserMessage("Tell me a joke");
//        var user = new UserMessage("Tell me a very serious joke about the earth");
        Prompt prompt = new Prompt(List.of(system, user));
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
```

###  YouTube 

Show off `PromptTemplate` by using a simple string 

```java
@GetMapping("/popular-step-one")
public String findPopularYouTubersStepOne(@RequestParam(value = "genre", defaultValue = "tech") String genre) {
    String message = """
        List 10 of the most popular YouTubers in {genre} along with their current subscriber counts. If you don't know
        the answer , just say "I don't know".
        """;
    PromptTemplate promptTemplate = new PromptTemplate(message);
    Prompt prompt = promptTemplate.create(Map.of("genre",genre));
    return chatClient.call(prompt).getResult().getOutput().getContent();
}
```

And then externalizing that to a classpath resource

```java
@GetMapping("/popular")
public String findPopularYouTubers(@RequestParam(value = "genre", defaultValue = "tech") String genre) {
    PromptTemplate promptTemplate = new PromptTemplate(ytPromptResource);
    Prompt prompt = promptTemplate.create(Map.of("genre", genre));
    return chatClient.call(prompt).getResult().getOutput().getContent();
}
```

## OutputParser

If you make a call with the following prompt and ask for the content you will get it back as a String

```java
@GetMapping("/ken")
public Generation getBooksByKen() {
    String promptMessage = """
            Generate a list of books written by the author {author}.
            """;

    PromptTemplate promptTemplate = new PromptTemplate(promptMessage, Map.of("author","Ken Kousen"));
    Prompt prompt = promptTemplate.create();
    return chatClient.call(prompt).getResult().getOutput().getContent();
}
```

Request 

```
http :8080/api/books/craig
```

Response

1. "Spring in Action"
2. "Spring Boot in Action"
3. "Modular Java: Creating Flexible Applications with OSGi and Spring"
4. "XDoclet in Action"
5. "Spring Microservices in Action"
6. "Getting started with Spring Framework: a hands-on guide to begin developing applications using Spring Framework"
7. "Spring in Action, Fifth Edition"
8. "Spring in Action, Fourth Edition"
9. "Spring Boot in Action, Second Edition"

You can ask for a JSON formatted String: 

```java
String promptMessage = """
        Generate a list of books written by the author {author}. Please return it to me in JSON format.
        """;
```

And you will get this back, but then you still need to convert this raw JSON into an object. 

```json
[
  {
    "author": "Craig Walls",
    "title": "Spring in Action",
    "year": "2014"
  },
  {
    "author": "Craig Walls",
    "title": "Spring Boot in Action",
    "year": "2015"
  },
  {
    "author": "Craig Walls",
    "title": "Spring in Action, Fifth Edition",
    "year": "2018"
  },
  {
    "author": "Craig Walls",
    "title": "Modular Java: Creating Flexible Applications with Osgi and Spring",
    "year": "2009"
  },
  {
    "author": "Craig Walls",
    "title": "XDoclet in Action",
    "year": "2003"
  }
]
```

In the final demo we use the `BeanOutputParser`

```java
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
```

##  Bring Your Own Data Demos

How to use your own data in AI applications

### Stuffing the Prompt

Request 

```
http :8080/olympics/2024
```

Response

```
HTTP/1.1 200
Connection: keep-alive
Content-Length: 46
Content-Type: text/plain;charset=UTF-8
Date: Wed, 27 Mar 2024 20:52:41 GMT
Keep-Alive: timeout=60

I'm sorry but I don't know the answer to that.
```

Adding context to the prompt: 

```java
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
```

Response 

```
~ 🚀 http :8080/olympics/2024 stuffit==true
HTTP/1.1 200
Connection: keep-alive
Content-Length: 557
Content-Type: text/plain;charset=UTF-8
Date: Thu, 28 Mar 2024 02:00:34 GMT
Keep-Alive: timeout=60

Archery, athletics, badminton, basketball , basketball 3×3, boxing, canoe slalom, canoe sprint, road cycling, cycling track, mountain biking, BMX freestyle, BMX racing, equestrian, fencing, football, golf, artistic gymnastics, rhythmic gymnastics, trampoline, handball, hockey, judo, modern pentathlon, rowing, rugby, sailing, shooting, table tennis, taekwondo, tennis, triathlon, volleyball, beach volleyball, diving, marathon swimming, artistic swimming, swimming, water polo, weightlifting,wrestling,breaking, sport climbing, skateboarding, and surfing.
```

### RAG Demo


The RAG Demo uses a `SimpleVectorStore` to store some FAQs about the upcoming Summer Olympics in Paris.

```java
@Bean
SimpleVectorStore simpleVectorStore(EmbeddingClient embeddingClient) {
    var simpleVectorStore = new SimpleVectorStore(embeddingClient);
    var vectorStoreFile = new File(vectorStorePath);
    if (vectorStoreFile.exists()) {
        log.info("Vector Store File Exists,");
        simpleVectorStore.load(vectorStoreFile);
    } else {
        log.info("Vector Store File Does Not Exist, load documents");
        TextReader textReader = new TextReader(faq);
        textReader.getCustomMetadata().put("filename", "olympic-faq.txt");
        List<Document> documents = textReader.get();
        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> splitDocuments = textSplitter.apply(documents);
        simpleVectorStore.add(splitDocuments);
        simpleVectorStore.save(vectorStoreFile);
    }
    return simpleVectorStore;
}
```

Request

```
http :8080/faq message=="Where are the next 3 summer games?"
```

Response

```
HTTP/1.1 200 
Connection: keep-alive
Content-Length: 133
Content-Type: text/plain;charset=UTF-8
Date: Thu, 28 Mar 2024 12:18:27 GMT
Keep-Alive: timeout=60

The next three Summer Olympic Games will be held in Paris, France in 2024, Los Angeles, USA in 2028, and Brisbane, Australia in 2032.
```

### Function Demo 

The code related to this demo is in the `functions` package. In this demo there is a `CityController` with a
`/cities` endpoint that will answer questions about cities around the world. 

Request

```
http :8080/cities message=="What is the largest city in Ohio"
```

Response 

```
HTTP/1.1 200
Connection: keep-alive
Content-Length: 51
Content-Type: text/plain;charset=UTF-8
Date: Thu, 25 Apr 2024 21:38:46 GMT
Keep-Alive: timeout=60

The largest city in Ohio by population is Columbus.
```

If you were to ask it what the current weather is like in Columbus it wouldn't know the answer to that.

Request

```
http :8080/cities message=="What is current weather in Columbus"
```

Response 

```
HTTP/1.1 200
Connection: keep-alive
Content-Length: 185
Content-Type: text/plain;charset=UTF-8
Date: Thu, 25 Apr 2024 21:40:54 GMT
Keep-Alive: timeout=60

Sorry, as an AI, I'm unable to provide real-time information such as current weather updates. I recommend checking a reliable weather website or app for the most up-to-date information.
```

We can define a function by declaring a bean and the weather service will be responsible for determining the current weather. 

```java
@Bean
@Description("Get the current weather conditions for the given city.")
public Function<WeatherService.Request,WeatherService.Response> currentWeatherFunction() {
    return new WeatherService(props);
}
```