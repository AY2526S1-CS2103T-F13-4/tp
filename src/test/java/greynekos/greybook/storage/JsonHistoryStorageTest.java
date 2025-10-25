package greynekos.greybook.storage;

import static greynekos.greybook.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import greynekos.greybook.commons.core.history.CommandHistory;
import greynekos.greybook.commons.exceptions.DataLoadingException;
import greynekos.greybook.model.History;

public class JsonHistoryStorageTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonHistoryStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readHistory_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readHistory(null));
    }

    private Optional<History> readHistory(String historyFileInTestDataFolder) throws DataLoadingException {
        Path prefsFilePath = addToTestDataPathIfNotNull(historyFileInTestDataFolder);
        return new JsonHistoryStorage(prefsFilePath).readHistory(prefsFilePath);
    }

    @Test
    public void readHistory_missingFile_emptyResult() throws DataLoadingException {
        assertFalse(readHistory("NonExistentFile.json").isPresent());
    }

    @Test
    public void readHistory_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readHistory("NotJsonFormatHistory.json"));
    }

    private Path addToTestDataPathIfNotNull(String historyFileInTestDataFolder) {
        return historyFileInTestDataFolder != null ? TEST_DATA_FOLDER.resolve(historyFileInTestDataFolder) : null;
    }

    @Test
    public void readHistory_fileInOrder_successfullyRead() throws DataLoadingException {
        History expected = getTypicalHistory();
        History actual = readHistory("TypicalHistory.json").get();
        assertEquals(expected, actual);
    }

    @Test
    public void readHistory_valuesMissingFromFile_defaultValuesUsed() throws DataLoadingException {
        History actual = readHistory("EmptyHistory.json").get();
        assertEquals(new History(), actual);
    }

    private History getTypicalHistory() {
        History history = new History();
        history.setCommandHistory(new CommandHistory(Arrays.asList("mark 1 p/", "find test")));
        return history;
    }

    @Test
    public void saveHistory_nullHistory_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveHistory(null, "SomeFile.json"));
    }

    @Test
    public void saveHistory_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveHistory(new History(), null));
    }

    /**
     * Saves {@code history} at the specified {@code prefsFileInTestDataFolder}
     * filepath.
     */
    private void saveHistory(History history, String prefsFileInTestDataFolder) {
        try {
            new JsonHistoryStorage(addToTestDataPathIfNotNull(prefsFileInTestDataFolder)).saveHistory(history);
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file", ioe);
        }
    }

    @Test
    public void saveHistory_allInOrder_success() throws DataLoadingException, IOException {

        History original = new History();

        original.setCommandHistory(new CommandHistory(Arrays.asList("mark 1 p/", "find help")));

        Path historyFilePath = testFolder.resolve("TempHistory.json");
        JsonHistoryStorage jsonHistoryStorage = new JsonHistoryStorage(historyFilePath);

        // Try writing when the file doesn't exist
        jsonHistoryStorage.saveHistory(original);
        History readBack = jsonHistoryStorage.readHistory().get();
        assertEquals(original, readBack);

        // Try saving when the file exists
        original.setCommandHistory(new CommandHistory(Arrays.asList("find test")));
        jsonHistoryStorage.saveHistory(original);
        readBack = jsonHistoryStorage.readHistory().get();
        assertEquals(original, readBack);
    }

}
