package greynekos.greybook.commons.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;

import greynekos.greybook.commons.util.ToStringBuilder;

/**
 * Config values used by the app
 */
public class Config {

    public static final Path DEFAULT_CONFIG_FILE = Paths.get("config.json");

    // Config values customizable through config file
    private Level logLevel = Level.INFO;
    private Path userPrefsFilePath = Paths.get("preferences.json");
    private Path historyFilePath = Paths.get("history.json");

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public Path getUserPrefsFilePath() {
        return userPrefsFilePath;
    }

    public void setUserPrefsFilePath(Path userPrefsFilePath) {
        this.userPrefsFilePath = userPrefsFilePath;
    }

    public Path getHistoryFilePath() {
        return historyFilePath;
    }

    public void setHistoryFilePath(Path historyFilePath) {
        this.historyFilePath = historyFilePath;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Config)) {
            return false;
        }

        Config otherConfig = (Config) other;
        return Objects.equals(logLevel, otherConfig.logLevel)
                && Objects.equals(userPrefsFilePath, otherConfig.userPrefsFilePath)
                && Objects.equals(historyFilePath, otherConfig.historyFilePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logLevel, userPrefsFilePath, historyFilePath);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("logLevel", logLevel).add("userPrefsFilePath", userPrefsFilePath)
                .add("historyFilePath", historyFilePath).toString();
    }

}
