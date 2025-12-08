package plus.jdk.intellij.renamify.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(
        name = "OpenAiSettings",
        storages = {@Storage("OpenAiSettings-1.0.2.xml")}
)
public class OpenAiSettings implements PersistentStateComponent<OpenAiSettings.State> {
    @Data
    public static class State {
        public String baseUrl = "http://127.0.0.1:11434/v1";
        public String modelName = "qwen3:1.7b";
        public double temperature = 0.7;
        public int maxTokens = 100;
        public String apiKey = "";
        public List<String> fileSuffixBlackList = new ArrayList<>();
        private String prompt =
"""
给定一个中文变量名"％s"，请为我生成对应的变量名以及中文变量说明，要求如下：

先理解变量语义，抓住核心含义，而不是逐字直译。

翻译成英文变量名，使用 lowerCamelCase（首字母小写，其余单词首字母大写），不要包含特殊字符。

命名尽量简洁、优雅、有高级感。

给出 4 个备选项，使用 JSON 数组格式，每个备选项包含变量名和推荐理由，例如：

[{"variable": "userName", "reason": "用户名"}, {"variable": "userAge", "reason": "用户年龄"}]

注意，仅输出 JSON 数组，不要包含其他内容。
""";
    }

    private State state = new State();

    @Nullable
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(State state) {
        this.state = state;
    }

    // 获取实例
    public static OpenAiSettings getInstance() {
        return ApplicationManager.getApplication().getService(OpenAiSettings.class);
    }
}