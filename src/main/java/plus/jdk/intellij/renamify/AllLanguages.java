package plus.jdk.intellij.renamify;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public final class AllLanguages extends Language {
    public static final @NotNull AllLanguages INSTANCE = new AllLanguages();

    private AllLanguages() {
        super("All");
    }

    @Override
    public @NotNull String getDisplayName() {
        return "All Languages";
    }

    @Override
    public boolean isCaseSensitive() {

        // We return true since most programming languages are case-sensitive
        // This won't negatively affect languages that aren't case-sensitive
        return true;
    }
}