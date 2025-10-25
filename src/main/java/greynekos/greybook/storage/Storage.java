package greynekos.greybook.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import greynekos.greybook.commons.exceptions.DataLoadingException;
import greynekos.greybook.model.History;
import greynekos.greybook.model.ReadOnlyGreyBook;
import greynekos.greybook.model.ReadOnlyHistory;
import greynekos.greybook.model.ReadOnlyUserPrefs;
import greynekos.greybook.model.UserPrefs;

/**
 * API of the Storage component
 */
public interface Storage extends GreyBookStorage, UserPrefsStorage, HistoryStorage {

    @Override
    Optional<History> readHistory() throws DataLoadingException;

    @Override
    void saveHistory(ReadOnlyHistory history) throws IOException;

    @Override
    Optional<UserPrefs> readUserPrefs() throws DataLoadingException;

    @Override
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    Path getGreyBookFilePath();

    @Override
    Optional<ReadOnlyGreyBook> readGreyBook() throws DataLoadingException;

    @Override
    void saveGreyBook(ReadOnlyGreyBook greyBook) throws IOException;

}
