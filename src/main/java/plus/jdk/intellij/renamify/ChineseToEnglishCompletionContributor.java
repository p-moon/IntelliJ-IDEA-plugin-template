package plus.jdk.intellij.renamify;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiIdentifier;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class ChineseToEnglishCompletionContributor extends CompletionContributor {

    public ChineseToEnglishCompletionContributor() {
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
                            // 可生成多种风格
                            String camel = PinyinUtil.toCamelCase(text);

                            resultSet.addElement(
                                LookupElementBuilder.create(camel)
                                    .withPresentableText(camel)
                                    .withLookupString(text)
                                    .withTypeText("变量名(小驼峰)")
                                    .withInsertHandler((insertionContext, item) -> {
                                        int startOffset = insertionContext.getStartOffset();
                                        int tailOffset = insertionContext.getTailOffset();
                                        insertionContext.getDocument().replaceString(startOffset, tailOffset, camel);
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