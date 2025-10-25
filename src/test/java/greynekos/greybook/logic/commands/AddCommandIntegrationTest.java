package greynekos.greybook.logic.commands;

import static greynekos.greybook.logic.commands.CommandTestUtil.assertCommandFailure;
import static greynekos.greybook.logic.commands.CommandTestUtil.assertCommandSuccess;
import static greynekos.greybook.testutil.TypicalPersons.getTypicalGreyBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import greynekos.greybook.logic.Messages;
import greynekos.greybook.logic.commands.stubs.AddPersonArgumentParseResultStub;
import greynekos.greybook.model.History;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.ModelManager;
import greynekos.greybook.model.UserPrefs;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for
 * {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalGreyBook(), new UserPrefs(), new History());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();
        AddPersonArgumentParseResultStub argStub = new AddPersonArgumentParseResultStub(validPerson);

        Model expectedModel = new ModelManager(model.getGreyBook(), new UserPrefs(), new History());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(), model, argStub,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)), expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getGreyBook().getPersonList().get(0);
        AddPersonArgumentParseResultStub argStub = new AddPersonArgumentParseResultStub(personInList);
        assertCommandFailure(new AddCommand(), model, argStub, AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

}
