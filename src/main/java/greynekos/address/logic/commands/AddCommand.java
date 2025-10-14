package greynekos.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static greynekos.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static greynekos.address.logic.parser.CliSyntax.PREFIX_NAME;
import static greynekos.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static greynekos.address.logic.parser.CliSyntax.PREFIX_STUDENTID;
import static greynekos.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;

import greynekos.address.logic.Messages;
import greynekos.address.logic.commands.exceptions.CommandException;
import greynekos.address.logic.parser.ArgumentParseResult;
import greynekos.address.logic.parser.GreyBookParser;
import greynekos.address.logic.parser.ParserUtil;
import greynekos.address.logic.parser.commandoption.RequiredPrefixOption;
import greynekos.address.logic.parser.commandoption.ZeroOrMorePrefixOption;
import greynekos.address.model.Model;
import greynekos.address.model.person.Email;
import greynekos.address.model.person.Name;
import greynekos.address.model.person.Person;
import greynekos.address.model.person.Phone;
import greynekos.address.model.person.StudentID;
import greynekos.address.model.tag.Tag;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the address book. " + "Parameters: "
            + PREFIX_NAME + "NAME " + PREFIX_PHONE + "PHONE " + PREFIX_EMAIL + "EMAIL " + PREFIX_STUDENTID
            + "STUDENTID " + "[" + PREFIX_TAG + "TAG]...\n" + "Example: " + COMMAND_WORD + " " + PREFIX_NAME
            + "John Doe " + PREFIX_PHONE + "98765432 " + PREFIX_EMAIL + "johnd@example.com " + PREFIX_STUDENTID
            + "A0000000X " + PREFIX_TAG + "friends " + PREFIX_TAG + "owesMoney";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";

    private final RequiredPrefixOption<Name> nameOption =
            RequiredPrefixOption.of(PREFIX_NAME, "NAME", ParserUtil::parseName);
    private final RequiredPrefixOption<Phone> phoneOption =
            RequiredPrefixOption.of(PREFIX_PHONE, "PHONE", ParserUtil::parsePhone);
    private final RequiredPrefixOption<Email> emailOption =
            RequiredPrefixOption.of(PREFIX_EMAIL, "EMAIL", ParserUtil::parseEmail);
    private final ZeroOrMorePrefixOption<Tag> tagOption =
            ZeroOrMorePrefixOption.of(PREFIX_TAG, "TAG", ParserUtil::parseTag);
    private final RequiredPrefixOption<StudentID> studentIdOption =
            RequiredPrefixOption.of(PREFIX_STUDENTID, "STUDENTID", ParserUtil::parseStudentID);

    @Override
    public void addToParser(GreyBookParser parser) {
        parser.newCommand(COMMAND_WORD, MESSAGE_USAGE, this).addOptions(nameOption, phoneOption, emailOption,
                studentIdOption, tagOption);
    }

    @Override
    public CommandResult execute(Model model, ArgumentParseResult arg) throws CommandException {
        requireNonNull(model);

        Person toAdd = getParseResult(arg);

        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.addPerson(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public Person getParseResult(ArgumentParseResult argResult) {
        return new Person(argResult.getValue(nameOption), argResult.getValue(phoneOption),
                argResult.getValue(emailOption), argResult.getValue(studentIdOption),
                Set.copyOf(argResult.getAllValues(tagOption)));
    }
}
