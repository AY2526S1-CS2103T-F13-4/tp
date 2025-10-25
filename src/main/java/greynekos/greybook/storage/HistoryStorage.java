package greynekos.greybook.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import greynekos.greybook.commons.exceptions.DataLoadingException;
import greynekos.greybook.model.History;
import greynekos.greybook.model.ReadOnlyHistory;

/**
 * Represents a storage for {@link greynekos.greybook.model.History}.
 */
public interface HistoryStorage {

    /**
     * Returns the file path of the History data file.
     */
    Path getHistoryFilePath();

    /**
     * Returns History data from storage. Returns {@code Optional.empty()} if
     * storage file is not found.
     *
     * @throws DataLoadingException
     *             if the loading of data from preference file failed.
     */
    Optional<History> readHistory() throws DataLoadingException;

    /**
     * Saves the given {@link greynekos.greybook.model.ReadOnlyHistory} to the
     * storage.
     *
     * @param history
     *            cannot be null.
     * @throws IOException
     *             if there was any problem writing to the file.
     */
    void saveHistory(ReadOnlyHistory history) throws IOException;

}
