package greynekos.greybook.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import greynekos.greybook.model.History;

/**
 * A utility class containing a typical {@code History} class
 */
public class TypicalHistory {

    private TypicalHistory() {
    } // prevents instantiation

    /**
     * Returns an {@code History} with a typical history.
     */
    public static History getTypicalHistory() {
        History history = new History();
        for (String command : getTypicalCommands()) {
            history.getCommandHistory().addCommand(command);
        }
        return history;
    }

    public static List<String> getTypicalCommands() {
        return new ArrayList<>(Arrays.asList("mark 1 p/", "find test"));
    }
}
