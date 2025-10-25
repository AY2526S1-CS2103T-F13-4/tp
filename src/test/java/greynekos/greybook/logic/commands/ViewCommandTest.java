package greynekos.greybook.logic.commands;

import static greynekos.greybook.logic.commands.CommandTestUtil.assertCommandSuccess;
import static greynekos.greybook.logic.commands.CommandTestUtil.showPersonAtIndex;
import static greynekos.greybook.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static greynekos.greybook.testutil.TypicalPersons.getTypicalGreyBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import greynekos.greybook.logic.commands.stubs.ArgumentParseResultStub;
import greynekos.greybook.logic.commands.stubs.ViewArgumentParseResultStub;
import greynekos.greybook.model.History;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.ModelManager;
import greynekos.greybook.model.UserPrefs;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.StudentID;

/**
 * Tests for {@code ViewCommand} covering the index path.
 */
public class ViewCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalGreyBook(), new UserPrefs(), new History());
        expectedModel = new ModelManager(model.getGreyBook(), new UserPrefs(), new History());
    }

    @Test
    public void executebyIndex_unfilteredshowsThatPerson() {
        Person target = model.getFilteredPersonList().get(0);
        ArgumentParseResultStub arg = new ViewArgumentParseResultStub(INDEX_FIRST_PERSON); // index "1"

        // Expected: filtered to exactly the target person
        expectedModel.updateFilteredPersonList(p -> p.equals(target));

        assertCommandSuccess(new ViewCommand(), model, arg, ViewCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void executebyIndex_filteredshowsThatPerson() {
        // Arrange: pre-filter the model to first person
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Person target = model.getFilteredPersonList().get(0);
        // index "1" within the filtered list
        ArgumentParseResultStub arg = new ViewArgumentParseResultStub(INDEX_FIRST_PERSON);
        // Expected: still only the same target person shown
        expectedModel.updateFilteredPersonList(p -> p.equals(target));

        assertCommandSuccess(new ViewCommand(), model, arg, ViewCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void executebyStudentId_unfilteredshowsThatPerson() {
        // Take any real person from the full list and use their student ID
        Person target = model.getFilteredPersonList().get(0);
        StudentID studentId = target.getStudentID();

        ArgumentParseResultStub arg = new ViewArgumentParseResultStub(studentId);

        // Expected: ViewCommand resets (since not index) then filters to the target
        expectedModel.updateFilteredPersonList(p -> p.equals(target));
        assertCommandSuccess(new ViewCommand(), model, arg, ViewCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void executebyStudentId_filteredresetsThenShowsThatPerson() {
        // Start with the model filtered to the first person
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // Choose a different person from the full GreyBook (not necessarily in the
        // current filtered list)
        Person target = expectedModel.getGreyBook().getPersonList().get(1); // second person in full list
        StudentID studentId = target.getStudentID();

        ArgumentParseResultStub arg = new ViewArgumentParseResultStub(studentId);

        // Expected: regardless of initial filter, end up showing only the target
        expectedModel.updateFilteredPersonList(p -> p.equals(target));
        assertCommandSuccess(new ViewCommand(), model, arg, ViewCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
