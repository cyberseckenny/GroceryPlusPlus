package me.grocery.grocerylist.ai;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates prompts to a user based on their answer to an initial question.
 * The prompts are intended to refine the grocery list or meal plan the user is seeking to create.
 */
public class GroceryListConstructor {
    private final String API_KEY = ApiKeyReader.getApiKey();
    private final String FOLLOW_UP_PROMPT = "Suppose you asked someone the question \"%s\" and " +
            "they answered \"%s\" Ask them five follow up questions that would allow you to " +
            "create a well-rounded meal plan for them based on their wants. Format this data " +
            "into a text file separating each question be a new line. Do not add any extra text.";

    // TODO: make sure api isn't evil
    private final String GENERATION_PROMPT = "Suppose you asked someone the question \"%s\" and " +
            "they answered \"%s\".  You then asked them five follow up questions: \"%s, %s, %s, " +
            "%s, %s\" and they answered \"%s, %s, %s, %s, %s\". Please provide them with a " +
            "well-rounded and nutritious grocery list based on their wants. Additionally, " +
            "provide general health and diet advice given what you asked the user and their " +
            "respective answers. Format this data into a JSON file with a key for each major food" +
            " group and it's corresponding pair as an array containing the foods. At the end of " +
            "the JSON file, add a key for \"advice\" and include the general health and diet " +
            "advice you generated. Do not add any extra text.";

    private final String initialPrompt;
    private final String initialAnswer;

    /**
     * @param initialPrompt the initial question asked to the user regarding meal plan
     * @param initialAnswer the answer to the initial question
     */
    public GroceryListConstructor(String initialPrompt, String initialAnswer) {
        this.initialPrompt = initialPrompt;
        this.initialAnswer = initialAnswer;
    }

    /**
     * Asks AI to generate questions to created a refined grocery list based on user's answer to
     * the initial prompt.
     *
     * @return List of follow questions generated by AI.
     */
    public List<String> followUpQuestions() {
        List<String> questions = new ArrayList<>();
        OpenAiService service = new OpenAiService(API_KEY);

        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage message = new ChatMessage(ChatMessageRole.USER.value(),
                String.format(FOLLOW_UP_PROMPT, initialPrompt, initialAnswer));

        messages.add(message);
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                // replace strings in follow up prompt with initial prompt and initial
                // answer
                .messages(messages)
                .model("gpt-3.5-turbo")
                // .maxTokens(400)
                .build();

        ChatMessage response =
                service.createChatCompletion(completionRequest).getChoices().get(0).getMessage();

        String answer = response.getContent();
        Collections.addAll(questions, answer.split("\n"));

        return questions;
    }

    /**
     * Generate a grocery list and store it into JSON based on user answers to questions.
     * Also provides general health advice to be stored in the JSON file.
     * @param questions the questions asked to the user
     * @param answers the answers to the questions from the user
     * @return the {@link JSONObject} with grocery list and advice data
     */
    public JSONObject generateGroceryList(List<String> questions, List<String> answers) {
        List<ChatMessage> messages = new ArrayList<>();
        OpenAiService service = new OpenAiService(API_KEY);

        // TODO: write this properly, it could cause a lot of issues.
        ChatMessage message = new ChatMessage(ChatMessageRole.USER.value(),
                String.format(GENERATION_PROMPT, initialPrompt, initialAnswer, questions.get(0),
                        questions.get(1), questions.get(2), questions.get(3), questions.get(4),
                        answers.get(0), answers.get(1), answers.get(2), answers.get(3),
                        answers.get(4)));
        messages.add(message);

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                // replace strings in follow up prompt with initial prompt and initial
                // answer
                .messages(messages)
                .model("gpt-3.5-turbo")
                // .maxTokens(400)
                .build();

        ChatMessage response =
                service.createChatCompletion(completionRequest).getChoices().get(0).getMessage();

        String answer = response.getContent();

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(answer);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject;
    }

    /**
     * @return the initial prompt
     */
    public String getInitialPrompt() {
        return initialPrompt;
    }

    /**
     * @return the answer to the initial prompt
     */
    public String getInitialAnswer() {
        return initialAnswer;
    }
}
