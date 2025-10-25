package greynekos.greybook.logic.commands;

import static greynekos.greybook.logic.commands.CommandTestUtil.assertCommandSuccess;
import static greynekos.greybook.testutil.TypicalPersons.getTypicalGreyBook;

import org.junit.jupiter.api.Test;

import greynekos.greybook.logic.commands.stubs.ArgumentParseResultStub;
import greynekos.greybook.model.GreyBook;
import greynekos.greybook.model.History;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.ModelManager;
import greynekos.greybook.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_emptyGreyBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, new ArgumentParseResultStub(), ClearCommand.MESSAGE_SUCCESS,
                expectedModel);
    }

    @Test
    public void execute_nonEmptyGreyBook_success() {
        Model model = new ModelManager(getTypicalGreyBook(), new UserPrefs(), new History());
        Model expectedModel = new ModelManager(getTypicalGreyBook(), new UserPrefs(), new History());
        expectedModel.setGreyBook(new GreyBook());

        assertCommandSuccess(new ClearCommand(), model, new ArgumentParseResultStub(), ClearCommand.MESSAGE_SUCCESS,
                expectedModel);
    }

}
