package greynekos.greybook.logic.commands;

import static greynekos.greybook.logic.commands.CommandTestUtil.assertCommandFailure;
import static greynekos.greybook.logic.commands.CommandTestUtil.assertCommandSuccess;
import static greynekos.greybook.logic.commands.CommandTestUtil.showPersonAtIndex;
import static greynekos.greybook.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static greynekos.greybook.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static greynekos.greybook.testutil.TypicalPersons.getTypicalGreyBook;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import greynekos.greybook.commons.core.index.Index;
import greynekos.greybook.logic.commands.stubs.DeletePersonArgumentParseResultStub;
import greynekos.greybook.model.History;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.ModelManager;
import greynekos.greybook.model.UserPrefs;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.StudentID;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalGreyBook(), new UserPrefs(), new History());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeletePersonArgumentParseResultStub argStub = new DeletePersonArgumentParseResultStub(INDEX_FIRST_PERSON);
        DeleteCommand deleteCommand = new DeleteCommand();

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, personToDelete.getName());

        ModelManager expectedModel = new ModelManager(model.getGreyBook(), new UserPrefs(), new History());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, argStub, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeletePersonArgumentParseResultStub argStub = new DeletePersonArgumentParseResultStub(outOfBoundIndex);
        DeleteCommand deleteCommand = new DeleteCommand();

        assertCommandFailure(deleteCommand, model, argStub, DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeletePersonArgumentParseResultStub argStub = new DeletePersonArgumentParseResultStub(INDEX_FIRST_PERSON);
        DeleteCommand deleteCommand = new DeleteCommand();

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, personToDelete.getName());

        Model expectedModel = new ModelManager(model.getGreyBook(), new UserPrefs(), new History());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, argStub, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of GreyBook list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getGreyBook().getPersonList().size());

        DeletePersonArgumentParseResultStub argStub = new DeletePersonArgumentParseResultStub(outOfBoundIndex);
        DeleteCommand deleteCommand = new DeleteCommand();

        assertCommandFailure(deleteCommand, model, argStub, DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_validStudentIdUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        StudentID studentID = personToDelete.getStudentID();
        DeletePersonArgumentParseResultStub argStub = new DeletePersonArgumentParseResultStub(studentID);
        DeleteCommand deleteCommand = new DeleteCommand();

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, personToDelete.getName());

        ModelManager expectedModel = new ModelManager(model.getGreyBook(), new UserPrefs(), new History());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, argStub, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidStudentIdUnfilteredList_throwsCommandException() {
        StudentID nonExistentStudentID = new StudentID("A9999999W");
        DeletePersonArgumentParseResultStub argStub = new DeletePersonArgumentParseResultStub(nonExistentStudentID);
        DeleteCommand deleteCommand = new DeleteCommand();

        assertCommandFailure(deleteCommand, model, argStub, DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_validStudentIdFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        StudentID studentID = personToDelete.getStudentID();
        DeletePersonArgumentParseResultStub argStub = new DeletePersonArgumentParseResultStub(studentID);
        DeleteCommand deleteCommand = new DeleteCommand();

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, personToDelete.getName());

        Model expectedModel = new ModelManager(model.getGreyBook(), new UserPrefs(), new History());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, argStub, expectedMessage, expectedModel);
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
