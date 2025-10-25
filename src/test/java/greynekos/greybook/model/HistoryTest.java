package greynekos.greybook.model;

import static greynekos.greybook.testutil.Assert.assertThrows;
import static greynekos.greybook.testutil.TypicalHistory.getTypicalHistory;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HistoryTest {

    @Test
    public void setCommandHistory_nullCommandHistory_throwsNullPointerException() {
        History history = new History();
        assertThrows(NullPointerException.class, () -> history.setCommandHistory(null));
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        History history = new History();
        assertThrows(NullPointerException.class, () -> history.resetData(null));
    }

    @Test
    public void resetData_withValidCommandHistory_replacesData() {
        History history = new History();

        History newHistory = getTypicalHistory();
        history.resetData(newHistory);
        assertEquals(history, newHistory);
    }

}
