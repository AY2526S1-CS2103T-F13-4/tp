package seedu.address.logic.commands.util;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonIdentifier;
import seedu.address.model.person.StudentID;

/**
 * Common utility methods for commands
 */
public class CommandUtil {

    private static final String MESSAGE_PERSON_NOT_FOUND = "Error, user does not exist.";

    /**
     * Finds the person to delete based on the identifier. The identifier can be
     * either an index or a student ID.
     *
     * @param model
     *            The model containing the person list
     * @param identifier
     *            The identifier as a {@link PersonIdentifier} (either Index or
     *            StudentID)
     * @return The person to delete
     * @throws CommandException
     *             if the person cannot be found
     */
    public static Person resolvePerson(Model model, PersonIdentifier identifier) throws CommandException {
        if (identifier instanceof Index) {
            return findPersonByIndex(model, (Index) identifier);
        } else {
            model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
            return findPersonByStudentId(model, (StudentID) identifier);
        }
    }

    /**
     * Checks if the identifier is an index.
     *
     * @param identifier
     *            The identifier string
     * @return true if the identifier is a valid index
     */
    public static boolean isIndex(String identifier) {
        return StringUtil.isNonZeroUnsignedInteger(identifier);
    }

    /**
     * Finds a person by their index in the displayed list.
     *
     * @param model
     *            The model containing the person list
     * @param index
     *            The index as an {@link Index}
     * @return The person at the specified index
     * @throws CommandException
     *             if the index is out of bounds
     */
    private static Person findPersonByIndex(Model model, Index index) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_PERSON_NOT_FOUND);
        }

        return lastShownList.get(index.getZeroBased());
    }

    /**
     * Finds a person by their student ID.
     *
     * @param model
     *            The model containing the person list
     * @param studentId
     *            The student ID as a {@link StudentID}
     * @return The person with the specified student ID
     * @throws CommandException
     *             if no person with the student ID is found
     */
    private static Person findPersonByStudentId(Model model, StudentID studentId) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        Optional<Person> person = lastShownList.stream().filter(p -> p.getStudentID().equals(studentId)).findFirst();

        if (!person.isPresent()) {
            throw new CommandException(MESSAGE_PERSON_NOT_FOUND);
        }

        return person.get();
    }

}
