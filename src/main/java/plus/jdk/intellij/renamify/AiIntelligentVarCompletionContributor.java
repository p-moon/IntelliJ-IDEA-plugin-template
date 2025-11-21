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
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.jetbrains.annotations.NotNull;
import plus.jdk.intellij.renamify.model.VariableResult;
import plus.jdk.intellij.renamify.settings.OpenAiSettings;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import com.intellij.openapi.util.IconLoader;

@Slf4j
public class AiIntelligentVarCompletionContributor extends CompletionContributor {

    // JEXL 脚本处理
    JexlEngine jexl = new JexlBuilder().create();

    /**
     * 用于存储和管理与OpenAI的聊天模型实例的交互
     */
//    private final OpenAiChatModel openAiChatModel;
    public AiIntelligentVarCompletionContributor() {
        OpenAiSettings.State aiConfig = OpenAiSettings.getInstance().getState();
        ChatModel chatModel = OpenAiChatModel.builder()
                .baseUrl(aiConfig.getBaseUrl())
                .modelName(aiConfig.modelName)
                .responseFormat(ResponseFormat.JSON)
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
                                List<VariableResult> variableResults = JSON.parseArray(result, VariableResult.class);
                                for (VariableResult variableResult : variableResults) {
                                    try {
                                        String finalProcessed = variableResult.getVariable();
                                        resultSet.addElement(
                                                LookupElementBuilder.create(finalProcessed)
                                                        .withPresentableText(finalProcessed)
                                                        .withIcon(IconLoader.getIcon("/pluginIcon.svg", AiIntelligentVarCompletionContributor.class))
                                                        .withLookupString(text)
                                                        .withTypeText(String.format("Renamify:%s", variableResult.getReason()))
                                                        .withInsertHandler((insertionContext, item) -> {
                                                            int startOffset = insertionContext.getStartOffset();
                                                            int tailOffset = insertionContext.getTailOffset();
                                                            insertionContext.getDocument().replaceString(startOffset, tailOffset, finalProcessed);
                                                        })
                                        );
                                    } catch (Exception e) {
                                        log.error("AI补全请求异常", e);
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



    // 判断字符串是否为中文
    private boolean isChinese(String text) {
        return text != null && text.matches(".*[\\u4e00-\\u9fa5]+.*");
    }
}