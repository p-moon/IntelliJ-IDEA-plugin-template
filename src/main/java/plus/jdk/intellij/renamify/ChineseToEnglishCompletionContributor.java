package plus.jdk.intellij.renamify;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiIdentifier;
import com.intellij.util.ProcessingContext;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.jetbrains.annotations.NotNull;

public class ChineseToEnglishCompletionContributor extends CompletionContributor {

    /**
     * 用于存储和管理与OpenAI的聊天模型实例的交互
     */
    private final OpenAiChatModel openAiChatModel;

    public ChineseToEnglishCompletionContributor() {
        openAiChatModel = OpenAiChatModel.builder()
//                .apiKey("8939f2ddc2f941fda81b6c4c5b18f2de.39bDB9kr0mxiuE9T")
                .baseUrl("http://127.0.0.1:1234/v1")
//                .temperature(0.7)
//                .maxTokens(2048)
                .modelName("qwen/qwen3-vl-4b")
                .build();

        // 优化：Pattern 直接匹配 PsiIdentifier，触发更自然
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(PsiIdentifier.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        com.intellij.psi.PsiElement element = parameters.getPosition();
                        String text = element.getText();

                        // 日志：调试用
                        System.out.println("[ChineseToEnglishCompletion] PsiElement text: " + text);

                        if (isChinese(text)) {
                            String prompt = String.format("""
                                    我现在有一个中文变量名：%s，请帮我翻译成英文变量名，并使用小驼峰命名法。
                                    尽可能的简洁形象一些，给出 4 个备选项，将输出结果使用英文逗号分隔返回给我
                            """, text);
                            String result = openAiChatModel.chat(prompt);
                            String camel = PinyinUtil.toCamelCase(text);

                            resultSet.addElement(
                                LookupElementBuilder.create(camel)
                                    .withPresentableText(camel)
                                    .withLookupString(text)
                                    .withTypeText("通过 AI 生成变量名")
                                    .withInsertHandler((insertionContext, item) -> {
                                        int startOffset = insertionContext.getStartOffset();
                                        int tailOffset = insertionContext.getTailOffset();
                                        insertionContext.getDocument().replaceString(startOffset, tailOffset, result);
                                    })
                            );
                        }
                    }
                });
    }

    // 判断字符串是否为中文
    private boolean isChinese(String text) {
        return text != null && text.matches(".*[\\u4e00-\\u9fa5]+.*");
    }

    // ------ 简单拼音工具（建议接入第三方库完善） ------
    static class PinyinUtil {
        // 伪实现：后续建议用 TinyPinyin/Hanzi2Pinyin 等库
        static String toCamelCase(String chinese) {
            // 占位实现
            return "var" + "翻译展示后的内容";
        }
    }
}