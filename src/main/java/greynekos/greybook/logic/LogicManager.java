package greynekos.greybook.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.logging.Logger;

import greynekos.greybook.commons.core.GuiSettings;
import greynekos.greybook.commons.core.LogsCenter;
import greynekos.greybook.logic.commands.CommandRegistry;
import greynekos.greybook.logic.commands.CommandResult;
import greynekos.greybook.logic.commands.exceptions.CommandException;
import greynekos.greybook.logic.parser.ArgumentParseResult;
import greynekos.greybook.logic.parser.GreyBookParser;
import greynekos.greybook.logic.parser.exceptions.ParseException;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.ReadOnlyGreyBook;
import greynekos.greybook.model.ReadOnlyHistory;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.storage.Storage;
import javafx.collections.ObservableList;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final GreyBookParser greyBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and
     * {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        greyBookParser = new GreyBookParser();
        CommandRegistry.addCommandsToParser(greyBookParser);
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        ArgumentParseResult argParseResult = greyBookParser.parse(commandText);
        commandResult = argParseResult.execute(model);
        model.getHistory().getCommandHistory().addCommand(commandText);

        try {
            storage.saveGreyBook(model.getGreyBook());
            storage.saveHistory(model.getHistory());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    @Override
    public ReadOnlyGreyBook getGreyBook() {
        return model.getGreyBook();
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public Path getGreyBookFilePath() {
        return model.getGreyBookFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }

    @Override
    public ReadOnlyHistory getHistory() {
        return model.getHistory();
    }
}
