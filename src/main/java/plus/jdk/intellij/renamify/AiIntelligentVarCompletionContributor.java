package plus.jdk.intellij.renamify;

import com.alibaba.fastjson2.JSON;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiIdentifier;
import com.intellij.util.ProcessingContext;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.DefaultChatRequestParameters;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import plus.jdk.intellij.renamify.settings.OpenAiSettings;

import java.time.Duration;
import java.util.HashMap;

@Slf4j
public class AiIntelligentVarCompletionContributor extends CompletionContributor {

    /**
     * 用于存储和管理与OpenAI的聊天模型实例的交互
     */
//    private final OpenAiChatModel openAiChatModel;
    public AiIntelligentVarCompletionContributor() {
        OpenAiSettings.State aiConfig = OpenAiSettings.getInstance().getState();
        ChatModel chatModel = OpenAiChatModel.builder()
                .baseUrl(aiConfig.getBaseUrl())
                .modelName(aiConfig.modelName)
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
                        com.intellij.psi.PsiElement element = parameters.getPosition();
                        String text = element.getText().replace("IntellijIdeaRulezzz", "");
                        if (isChinese(text)) {
                            String prompt = String.format(aiConfig.getPrompt(), text);
                            ChatRequest chatRequest = ChatRequest.builder()
                                    .parameters(
                                             new OpenAiChatRequestParameters.Builder()
                                                     .reasoningEffort("none")
                                                     .customParameters(new HashMap<>(){{
                                                         put("enable_thinking", false);
                                                     }})
                                                    .responseFormat(ResponseFormat.TEXT)
                                                    .build())
                                    .messages(UserMessage.from(prompt)).build();
                            // 直接同步调用AI接口，保证补全结果能展示
                            try {
                                log.info("开始调用AI接口，request: {}", chatRequest);
                                ChatResponse chatResponse = chatModel.chat(chatRequest);
                                String result = chatResponse.aiMessage().text();
                                log.info("获取到了AI 的返回结果，results: {}", chatResponse);
                                for (String res : result.split(",")) {
                                    String trimmed = fixResult(res.trim());
                                    try {
                                        resultSet.addElement(
                                                LookupElementBuilder.create(trimmed)
                                                        .withPresentableText(trimmed)
                                                        .withLookupString(text)
                                                        .withTypeText("通过 AI 生成变量名")
                                                        .withInsertHandler((insertionContext, item) -> {
                                                            int startOffset = insertionContext.getStartOffset();
                                                            int tailOffset = insertionContext.getTailOffset();
                                                            insertionContext.getDocument().replaceString(startOffset, tailOffset, trimmed);
                                                        })
                                        );
                                    } catch (Exception ignore) {
                                    }
                                }
                                resultSet.restartCompletionOnAnyPrefixChange();
                            } catch (Exception e) {
                                log.error("AI补全请求异常", e);
                            }
                        }
                    }
                });
    }

    /**
     * 按照“-”或空格分割字符串，并将各部分首字母大写拼接。
     * 例如：abc-def -> AbcDef，abc def -> AbcDef
     */
    public static String fixResult(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String[] parts;
        if (input.contains("-")) {
            parts = input.split("-");
        } else if(input.contains("_")) {
            parts = input.split("_");
        }else {
            parts = input.split("\\s+");
        }
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (part.length() == 1) {
                sb.append(part.toUpperCase());
            } else {
                sb.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1).toLowerCase());
            }
        }
        String result = sb.toString();
        if(result.length() == 1) {
            return result;
        }
        return Character.toUpperCase(result.charAt(0)) + result.substring(1);
    }


    // 判断字符串是否为中文
    private boolean isChinese(String text) {
        return text != null && text.matches(".*[\\u4e00-\\u9fa5]+.*");
    }
}