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
        public String modelName = "qwen3:0.6b";
        public double temperature = 0.7;
        public int maxTokens = 100;
        public String apiKey = "";
        private String prompt =
                """
                        I have a Chinese variable name: "%s". \s
                        Please translate it into an English variable name using lowerCamelCase, like userName、userAge.
                        
                        Requirements:
                        1. No poetic, figurative, or imaginative translation.
                        2. Provide 4 alternative variable names.
                        3. Return them in one line, separated by ",".
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