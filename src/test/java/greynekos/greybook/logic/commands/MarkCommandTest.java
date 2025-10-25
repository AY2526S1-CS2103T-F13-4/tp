package greynekos.greybook.logic.commands;

import static greynekos.greybook.logic.commands.CommandTestUtil.assertCommandFailure;
import static greynekos.greybook.logic.commands.CommandTestUtil.assertCommandSuccess;
import static greynekos.greybook.logic.commands.util.CommandUtil.MESSAGE_PERSON_NOT_FOUND;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_ABSENT;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_PRESENT;
import static greynekos.greybook.logic.parser.CommandParserTestUtil.assertParseFailure;
import static greynekos.greybook.logic.parser.ParserUtil.MESSAGE_INVALID_PERSON_IDENTIFIER;
import static greynekos.greybook.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static greynekos.greybook.testutil.TypicalPersons.getTypicalGreyBook;

import org.junit.jupiter.api.Test;

import greynekos.greybook.logic.Messages;
import greynekos.greybook.logic.parser.ArgumentParseResult;
import greynekos.greybook.logic.parser.GreyBookParser;
import greynekos.greybook.model.GreyBook;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.ModelManager;
import greynekos.greybook.model.UserPrefs;
import greynekos.greybook.model.person.AttendanceStatus;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.StudentID;
import greynekos.greybook.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * MarkCommand.
 */
public class MarkCommandTest {

    private Model model = new ModelManager(getTypicalGreyBook(), new UserPrefs());

    @Test
    public void execute_markByIndex_success() throws Exception {
        Person targetPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        MarkCommand markCommand = new MarkCommand();

        // Build parser and arguments for command
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark " + INDEX_FIRST_PERSON.getOneBased() + " p/";
        ArgumentParseResult arg = parser.parse(userInput);

        Person markedPerson =
                new PersonBuilder(targetPerson).withAttendanceStatus(AttendanceStatus.Status.PRESENT).build();

        Model expectedModel = new ModelManager(new GreyBook(model.getGreyBook()), new UserPrefs());
        expectedModel.setPerson(targetPerson, markedPerson);

        String expectedMessage = String.format(MarkCommand.MESSAGE_MARK_PERSON_SUCCESS, markedPerson.getName(),
                AttendanceStatus.Status.PRESENT, Messages.format(markedPerson));

        assertCommandSuccess(markCommand, model, arg, expectedMessage, expectedModel);
    }

    @Test
    public void execute_markByIndexWithExtraSpaces_success() throws Exception {
        Person targetPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "   mark   " + INDEX_FIRST_PERSON.getOneBased() + "   e/   ";
        ArgumentParseResult arg = parser.parse(userInput);

        Person markedPerson =
                new PersonBuilder(targetPerson).withAttendanceStatus(AttendanceStatus.Status.EXCUSED).build();
        Model expectedModel = new ModelManager(new GreyBook(model.getGreyBook()), new UserPrefs());
        expectedModel.setPerson(targetPerson, markedPerson);

        String expectedMessage = String.format(MarkCommand.MESSAGE_MARK_PERSON_SUCCESS, markedPerson.getName(),
                AttendanceStatus.Status.EXCUSED, Messages.format(markedPerson));

        assertCommandSuccess(markCommand, model, arg, expectedMessage, expectedModel);
    }

    @Test
    public void execute_markByStudentId_success() throws Exception {
        Person targetPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        StudentID sid = targetPerson.getStudentID();
        MarkCommand markCommand = new MarkCommand();

        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark " + sid.value + " l/";
        ArgumentParseResult arg = parser.parse(userInput);

        Person markedPerson =
                new PersonBuilder(targetPerson).withAttendanceStatus(AttendanceStatus.Status.LATE).build();

        Model expectedModel = new ModelManager(new GreyBook(model.getGreyBook()), new UserPrefs());
        expectedModel.setPerson(targetPerson, markedPerson);

        String expectedMessage = String.format(MarkCommand.MESSAGE_MARK_PERSON_SUCCESS, markedPerson.getName(),
                AttendanceStatus.Status.LATE, Messages.format(markedPerson));

        assertCommandSuccess(markCommand, model, arg, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() throws Exception {
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark 999 p/";
        ArgumentParseResult arg = parser.parse(userInput);

        assertCommandFailure(markCommand, model, arg, MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_invalidStudentId_throwsCommandException() throws Exception {
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark A0000000Y a/";
        ArgumentParseResult arg = parser.parse(userInput);

        assertCommandFailure(markCommand, model, arg, MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_missingAttendanceFlag_throwsCommandException() throws Exception {
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark " + INDEX_FIRST_PERSON.getOneBased();
        ArgumentParseResult arg = parser.parse(userInput);

        assertCommandFailure(markCommand, model, arg, MarkCommand.MESSAGE_MISSING_ATTENDANCE_FLAG);
    }

    @Test
    // TODO : Now giving a different error message, fix
    public void parse_invalidPrefix_throwsParseException() {
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark 1 x/";

        assertParseFailure(parser, userInput, MESSAGE_INVALID_PERSON_IDENTIFIER);
    }

    @Test
    public void parse_noIdentifier_throwsParseException() {
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark p/";

        assertParseFailure(parser, userInput, MESSAGE_INVALID_PERSON_IDENTIFIER);
    }

    @Test
    public void parse_multipleIdentifiers_throwsParseException() {
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        Person targetPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String userInput =
                String.format("mark %d %s p/", INDEX_FIRST_PERSON.getOneBased(), targetPerson.getStudentID().value);

        assertParseFailure(parser, userInput, MESSAGE_INVALID_PERSON_IDENTIFIER);
    }

    @Test
    public void parse_multipleAttendanceFlags_throwsParseException() {
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark 1 p/ a/";

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForMutuallyExclusivePrefixes(PREFIX_PRESENT, PREFIX_ABSENT));
    }
}
