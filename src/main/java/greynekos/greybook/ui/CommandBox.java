package greynekos.greybook.ui;

import greynekos.greybook.commons.core.history.CommandHistory;
import greynekos.greybook.logic.commands.CommandResult;
import greynekos.greybook.logic.commands.exceptions.CommandException;
import greynekos.greybook.logic.parser.exceptions.ParseException;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;
    private final CommandHistory history;

    private boolean shouldUpdateBuffer = true;
    private String lastTextBuffer = "";

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor, CommandHistory history) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.history = history;
        // calls #setStyleToDefault() whenever there is a change to the text of the
        // command box.
        commandTextField.textProperty().addListener((unused1, unused2, newString) -> {
            setStyleToDefault();
            if (shouldUpdateBuffer) {
                lastTextBuffer = newString;
            }
        });
        commandTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.KP_UP) {
                handleUpArrowEntered();
            } else if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.KP_DOWN) {
                handleDownArrowEntered();
            } else if (event.getCode() == KeyCode.C && event.isControlDown()) {
                handleCtrlCEntered();
            }
        });
        Platform.runLater(() -> commandTextField.requestFocus());
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.isEmpty()) {
            return;
        }

        try {
            commandExecutor.execute(commandText);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Handles the up arrow key pressed event.
     */
    private void handleUpArrowEntered() {
        shouldUpdateBuffer = false;
        commandTextField.setText(history.getPrevCommand());
        moveCaretToEnd();
    }

    /**
     * Handles the down arrow key pressed event.
     */
    private void handleDownArrowEntered() {
        String nextCommand = history.getNextCommand();
        if (nextCommand == CommandHistory.NO_PREV_OR_NEXT_COMMAND) {
            commandTextField.setText(lastTextBuffer);
            shouldUpdateBuffer = true;
        } else {
            commandTextField.setText(nextCommand);
            shouldUpdateBuffer = false;
        }
        moveCaretToEnd();
    }

    /**
     * Handles the Ctrl+C key pressed event. Simply clears the text field.
     */
    private void handleCtrlCEntered() {
        commandTextField.setText("");
    }

    /**
     * Moves the caret to the end of the input.
     */
    private void moveCaretToEnd() {
        commandTextField.positionCaret(commandTextField.getText().length());
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see greynekos.greybook.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

}
