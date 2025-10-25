package greynekos.greybook.commons.core.history;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandHistoryTest {

    private CommandHistory history;

    @BeforeEach
    void setUp() {
        history = new CommandHistory();
    }

    @Test
    public void addCommand_addedInCorrectOrder() {
        history.addCommand("a");
        history.addCommand("b");
        history.addCommand("c");

        assertEquals(history.getPrevCommand(), "c");
        assertEquals(history.getPrevCommand(), "b");
        assertEquals(history.getPrevCommand(), "a");
        assertEquals(history.getPrevCommand(), "a"); // Oldest command
        assertEquals(history.getPrevCommand(), "a"); // Oldest command
    }

    @Test
    public void addCommand_returnsNextAndPrevCorrectly() {
        history.addCommand("a");
        history.addCommand("b");
        history.addCommand("c");

        assertEquals(history.getPrevCommand(), "c");
        assertEquals(history.getPrevCommand(), "b");
        assertEquals(history.getNextCommand(), "c");
        assertEquals(history.getPrevCommand(), "b");
        assertEquals(history.getPrevCommand(), "a");
        assertEquals(history.getPrevCommand(), "a");
        assertEquals(history.getNextCommand(), "b");
        assertEquals(history.getNextCommand(), "c");
        assertEquals(history.getNextCommand(), CommandHistory.NO_PREV_OR_NEXT_COMMAND);
        assertEquals(history.getNextCommand(), CommandHistory.NO_PREV_OR_NEXT_COMMAND);
    }

    @Test
    public void addCommand_noDuplicates() {
        history.addCommand("a");
        history.addCommand("b");
        history.addCommand("b"); // should be ignored

        assertEquals(history.getPrevCommand(), "b");
        assertEquals(history.getPrevCommand(), "a");
    }

    @Test
    void addCommand_storeMaxHistoryLimit() {
        for (int i = 0; i < CommandHistory.SIZE_LIMIT + 10; i++) {
            history.addCommand("cmd" + (i + 1));
        }

        // Should only keep last {@code CommandHistory.SIZE_LIMIT} commands
        for (int i = 0; i < CommandHistory.SIZE_LIMIT; i++) {
            history.getPrevCommand();
        }
        // Oldest remaining should be cmd11
        assertEquals(history.getPrevCommand(), "cmd11");
    }

    @Test
    void addCommand_resetAfterAddingNewCommand() {
        history.addCommand("a");
        history.addCommand("b");
        history.addCommand("c");

        assertEquals("c", history.getPrevCommand());
        assertEquals("b", history.getPrevCommand());

        // Add new command should reset cursor
        history.addCommand("d");

        assertEquals("d", history.getPrevCommand());
    }

    @Test
    void history_testEmptyHistory() {
        assertEquals(history.getPrevCommand(), CommandHistory.NO_PREV_OR_NEXT_COMMAND);
        assertEquals(history.getNextCommand(), CommandHistory.NO_PREV_OR_NEXT_COMMAND);
    }

    @Test
    void resetHistory_resetProperly() {
        history.addCommand("a");
        history.addCommand("b");
        history.addCommand("c");

        history.resetHistory();
        assertEquals(history.getHistory(), Arrays.asList());

        // Resetting an empty history should not change
        history.resetHistory();
        assertEquals(history.getHistory(), Arrays.asList());
    }

}
