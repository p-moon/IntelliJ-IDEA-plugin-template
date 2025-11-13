package plus.jdk.intellij.renamify;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiIdentifier;
import com.intellij.util.ProcessingContext;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import plus.jdk.intellij.renamify.settings.OpenAiSettings;

import java.time.Duration;

@Slf4j
public class ChineseToEnglishCompletionContributor extends CompletionContributor {

    /**
     * 用于存储和管理与OpenAI的聊天模型实例的交互
     */
//    private final OpenAiChatModel openAiChatModel;
    public ChineseToEnglishCompletionContributor() {
        OpenAiSettings.State aiConfig = OpenAiSettings.getInstance().getState();
        ChatModel chatModel = OpenAiChatModel.builder()
                .baseUrl(aiConfig.getBaseUrl())
                .modelName(aiConfig.modelName)
                .maxTokens(aiConfig.maxTokens)
                .timeout(Duration.ofSeconds(10))
                .build();

        // 优化：Pattern 直接匹配 PsiIdentifier，触发更自然
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(PsiIdentifier.class),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        try {
                            com.intellij.psi.PsiElement element = parameters.getPosition();
                            String text = element.getText().replace("IntellijIdeaRulezzz", "");
                            if (isChinese(text)) {
                                String prompt = String.format(aiConfig.getPrompt(), text);
                                ChatRequest chatRequest = ChatRequest.builder().maxOutputTokens(aiConfig.maxTokens)
                                        .temperature(aiConfig.getTemperature())
                                        .responseFormat(ResponseFormat.TEXT)
                                        .messages(UserMessage.from(prompt)).build();
                                ChatResponse chatResponse = chatModel.chat(chatRequest);
                                String result = chatResponse.aiMessage().text();
                                resultSet.addElement(
                                        LookupElementBuilder.create(result)
                                                .withPresentableText(result)
                                                .withLookupString(text)
                                                .withTypeText("通过 AI 生成变量名")
                                                .withInsertHandler((insertionContext, item) -> {
                                                    int startOffset = insertionContext.getStartOffset();
                                                    int tailOffset = insertionContext.getTailOffset();
                                                    insertionContext.getDocument().replaceString(startOffset, tailOffset, result);
                                                })
                                );
                            }
                        } catch (Exception e) {
                            log.error("变量名补全失败", e);
                        }
                    }
                });
    }

    // 判断字符串是否为中文
    private boolean isChinese(String text) {
        return text != null && text.matches(".*[\\u4e00-\\u9fa5]+.*");
    }
}