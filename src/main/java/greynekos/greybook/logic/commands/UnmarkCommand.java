package greynekos.greybook.logic.commands;

import static greynekos.greybook.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static java.util.Objects.requireNonNull;

import java.util.List;

import greynekos.greybook.logic.Messages;
import greynekos.greybook.logic.commands.exceptions.CommandException;
import greynekos.greybook.logic.commands.util.CommandUtil;
import greynekos.greybook.logic.parser.ArgumentParseResult;
import greynekos.greybook.logic.parser.GreyBookParser;
import greynekos.greybook.logic.parser.ParserUtil;
import greynekos.greybook.logic.parser.commandoption.SinglePreambleOption;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.person.All;
import greynekos.greybook.model.person.AttendanceStatus;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.PersonIdentifier;
import greynekos.greybook.model.person.PersonIdentifierOrAll;

/**
 * The UnmarkCommand clears a club member's attendance status.
 */
public class UnmarkCommand extends Command {

    public static final String COMMAND_WORD = "unmark";

    public static final String MESSAGE_UNMARK_PERSON_SUCCESS = "Cleared %1$s's attendance status";
    public static final String MESSAGE_UNMARK_ALL_SUCCESS = "All attendance status have been cleared.";

    /**
     * Unmark Command Usage and Error Messages
     */
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Clears a person's attendance status.\n"
            + "Parameters: INDEX (must be a positive integer) or  STUDENT_ID (format: A0000000Y)\n" + "Example: "
            + COMMAND_WORD + " 1 OR " + COMMAND_WORD + " A0123456J OR " + COMMAND_WORD + " all";

    /**
     * Unmark Command Preamble and Prefix Options
     */

    private final SinglePreambleOption<PersonIdentifierOrAll> identifierOrAllOption = SinglePreambleOption
            .of("ALL or INDEX or STUDENTID", ParserUtil::parsePersonIdentifierOrAll);

    @Override
    public void addToParser(GreyBookParser parser) {
        parser.newCommand(COMMAND_WORD, MESSAGE_USAGE, this).addOptions(identifierOrAllOption);
    }

    @Override
    public CommandResult execute(Model model, ArgumentParseResult arg) throws CommandException {
        requireNonNull(model);

        PersonIdentifierOrAll identifier = getParseResult(arg);
        if (identifier instanceof All) {
            return executeUnmarkAll(model);
        }

        Person personToUnmark = CommandUtil.resolvePerson(model, (PersonIdentifier) identifier);

        Person unmarkedPerson = createUnmarkedPerson(personToUnmark);
        model.setPerson(personToUnmark, unmarkedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(String.format(MESSAGE_UNMARK_PERSON_SUCCESS, unmarkedPerson.getName(),
                Messages.format(unmarkedPerson)));
    }

    private CommandResult executeUnmarkAll(Model model) {
        List<Person> personList = model.getFilteredPersonList();

        for (Person person : personList) {
            Person resetPerson = createUnmarkedPerson(person);
            model.setPerson(person, resetPerson);
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(MESSAGE_UNMARK_ALL_SUCCESS);
    }

    private static Person createUnmarkedPerson(Person person) {
        assert person != null;
        return new Person(person.getName(), person.getPhone(), person.getEmail(), person.getStudentID(),
                person.getTags(), new AttendanceStatus());
    }

    @Override
    public PersonIdentifierOrAll getParseResult(ArgumentParseResult argResult) {
        return argResult.getValue(identifierOrAllOption);
    }
}
