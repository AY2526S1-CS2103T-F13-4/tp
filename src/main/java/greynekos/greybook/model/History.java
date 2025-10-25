package greynekos.greybook.model;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import greynekos.greybook.commons.core.history.CommandHistory;

/**
 * Represents User's command history.
 */
public class History implements ReadOnlyHistory {

    private CommandHistory commandHistory;

    /**
     * Creates a {@code History} with default values.
     */
    public History() {
        this.commandHistory = new CommandHistory();
    }

    public History(CommandHistory commandHistory) {
        this.commandHistory = commandHistory;
    }

    /**
     * Creates a {@code History} with the history in {@code history}.
     */
    public History(ReadOnlyHistory history) {
        this();
        resetData(history);
    }

    /**
     * Resets the existing data of this {@code History} with {@code newHistory}.
     */
    public void resetData(ReadOnlyHistory newHistory) {
        requireNonNull(newHistory);
        setCommandHistory(newHistory.getCommandHistory());
    }

    public CommandHistory getCommandHistory() {
        return commandHistory;
    }

    public void setCommandHistory(CommandHistory history) {
        requireNonNull(history);
        this.commandHistory = history;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof History)) {
            return false;
        }

        History otherHistory = (History) other;
        return Objects.equals(commandHistory, otherHistory.commandHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandHistory);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("History : " + commandHistory);
        return sb.toString();
    }

}
