package greynekos.greybook.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import greynekos.greybook.commons.core.LogsCenter;
import greynekos.greybook.commons.exceptions.DataLoadingException;
import greynekos.greybook.model.History;
import greynekos.greybook.model.ReadOnlyGreyBook;
import greynekos.greybook.model.ReadOnlyHistory;
import greynekos.greybook.model.ReadOnlyUserPrefs;
import greynekos.greybook.model.UserPrefs;

/**
 * Manages storage of GreyBook data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private GreyBookStorage greyBookStorage;
    private UserPrefsStorage userPrefsStorage;
    private HistoryStorage historyStorage;

    /**
     * Creates a {@code StorageManager} with the given {@code GreyBookStorage} and
     * {@code UserPrefStorage}.
     */
    public StorageManager(GreyBookStorage greyBookStorage, UserPrefsStorage userPrefsStorage,
            HistoryStorage historyStorage) {
        this.greyBookStorage = greyBookStorage;
        this.userPrefsStorage = userPrefsStorage;
        this.historyStorage = historyStorage;
    }

    // ================ History methods ==============================

    @Override
    public Path getHistoryFilePath() {
        return historyStorage.getHistoryFilePath();
    }

    @Override
    public Optional<History> readHistory() throws DataLoadingException {
        return historyStorage.readHistory();
    }

    @Override
    public void saveHistory(ReadOnlyHistory history) throws IOException {
        historyStorage.saveHistory(history);
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }

    // ================ GreyBook methods ==============================

    @Override
    public Path getGreyBookFilePath() {
        return greyBookStorage.getGreyBookFilePath();
    }

    @Override
    public Optional<ReadOnlyGreyBook> readGreyBook() throws DataLoadingException {
        return readGreyBook(greyBookStorage.getGreyBookFilePath());
    }

    @Override
    public Optional<ReadOnlyGreyBook> readGreyBook(Path filePath) throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + filePath);
        return greyBookStorage.readGreyBook(filePath);
    }

    @Override
    public void saveGreyBook(ReadOnlyGreyBook greyBook) throws IOException {
        saveGreyBook(greyBook, greyBookStorage.getGreyBookFilePath());
    }

    @Override
    public void saveGreyBook(ReadOnlyGreyBook greyBook, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        greyBookStorage.saveGreyBook(greyBook, filePath);
    }

}
