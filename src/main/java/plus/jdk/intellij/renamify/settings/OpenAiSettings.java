package plus.jdk.intellij.renamify.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@State(
        name = "OpenAiSettings",
        storages = {@Storage("OpenAiSettings.xml")}
)
public class OpenAiSettings implements PersistentStateComponent<OpenAiSettings.State> {
    @Data
    public static class State {
        public String baseUrl = "http://127.0.0.1:11434/v1";
        public String modelName = "qwen3:4b";
        public double temperature = 0.7;
        public int maxTokens = 100;
        public String apiKey = "";
        private String prompt = """
                                    我现在有一个中文变量名：“%s”：
                                    请帮我翻译成英文变量名，并使用小驼峰命名法。
                                    尽可能的简洁形象一些，
                                    给出 4 个备选项，将输出结果使用英文逗号分隔返回给我,
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