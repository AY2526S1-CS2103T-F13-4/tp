package greynekos.greybook.commons.core.history;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import greynekos.greybook.commons.core.LogsCenter;
import greynekos.greybook.commons.util.ToStringBuilder;

/**
 * Stores the command history of Greybook.
 */
public class CommandHistory {

    // An empty string is returned when there is no next command to retrieve
    public static final String NO_PREV_OR_NEXT_COMMAND = "";

    // The maximum number of items that can be stored in the history
    // This helps to prevent excessive memory usage in long-running applications
    public static final int SIZE_LIMIT = 50;

    private static final Logger logger = LogsCenter.getLogger(CommandHistory.class);

    // A "virtual" position between command entry indexes
    private int cursor = 0;
    private final List<String> history;

    public CommandHistory() {
        this.history = new ArrayList<>();
    }

    /**
     * Creates a new {@code CommandHistory} with existing history.
     *
     * @param history
     *            The existing history.
     */
    public CommandHistory(List<String> history) {
        this.history = history;
        resetCursor();
    }

    /**
     * Adds a command to history
     *
     * @param command
     *            The command string.
     */
    public void addCommand(String command) {
        // Avoid duplicate commands (similar to terminal)
        if (!history.isEmpty() && history.get(history.size() - 1).equals(command)) {
            logger.fine("Command is the same as the last command");
            resetCursor();
            return;
        }

        if (history.size() == SIZE_LIMIT) {
            logger.fine(
                    String.format("Reached maximum size limit of %d, removing first item from history.", SIZE_LIMIT));
            history.remove(0);
        }

        history.add(command);
        resetCursor();
    }

    /**
     * Resets the current command position to the latest command.
     */
    private void resetCursor() {
        cursor = history.size();
    }

    /**
     * Clears the command history.
     */
    public void resetHistory() {
        cursor = 0;
        history.clear();
    }

    /**
     * Gets the previous command history in the list.
     *
     * @return The previous command in the history. If the position is already at
     *         the start of the history, returns the first command.
     */
    public String getPrevCommand() {
        if (history.isEmpty()) {
            return NO_PREV_OR_NEXT_COMMAND;
        }

        if (cursor > 0) {
            cursor--;
            return history.get(cursor);
        }

        // Already at oldest command
        return history.get(0);
    }

    /**
     * Gets the next command history in the list.
     *
     * @return The next command in the history. If the position is already at the
     *         end of the history, returns {@code NO_NEXT_COMMAND}.
     */
    public String getNextCommand() {
        if (history.isEmpty()) {
            return NO_PREV_OR_NEXT_COMMAND;
        }

        if (cursor < history.size() - 1) {
            cursor++;
            return history.get(cursor);
        }

        resetCursor();
        return NO_PREV_OR_NEXT_COMMAND;
    }

    public List<String> getHistory() {
        return this.history;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandHistory)) {
            return false;
        }

        CommandHistory otherCommandHistory = (CommandHistory) other;
        return Objects.equals(history, otherCommandHistory.history)
                && Objects.equals(cursor, otherCommandHistory.cursor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(history, cursor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("history", history).add("cursor", cursor).toString();
    }
}
