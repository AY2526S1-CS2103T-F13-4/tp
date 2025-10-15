package greynekos.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import greynekos.address.commons.core.LogsCenter;
import greynekos.address.commons.exceptions.DataLoadingException;
import greynekos.address.commons.exceptions.IllegalValueException;
import greynekos.address.commons.util.FileUtil;
import greynekos.address.commons.util.JsonUtil;
import greynekos.address.model.ReadOnlyGreyBook;

/**
 * A class to access GreyBook data stored as a json file on the hard disk.
 */
public class JsonGreyBookStorage implements GreyBookStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonGreyBookStorage.class);

    private Path filePath;

    public JsonGreyBookStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getGreyBookFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyGreyBook> readGreyBook() throws DataLoadingException {
        return readGreyBook(filePath);
    }

    /**
     * Similar to {@link #readGreyBook()}.
     *
     * @param filePath
     *            location of the data. Cannot be null.
     * @throws DataLoadingException
     *             if loading the data from storage failed.
     */
    public Optional<ReadOnlyGreyBook> readGreyBook(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableGreyBook> jsonGreyBook =
                JsonUtil.readJsonFile(filePath, JsonSerializableGreyBook.class);
        if (!jsonGreyBook.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonGreyBook.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    @Override
    public void saveGreyBook(ReadOnlyGreyBook greyBook) throws IOException {
        saveGreyBook(greyBook, filePath);
    }

    /**
     * Similar to {@link #saveGreyBook(ReadOnlyGreyBook)}.
     *
     * @param filePath
     *            location of the data. Cannot be null.
     */
    public void saveGreyBook(ReadOnlyGreyBook greyBook, Path filePath) throws IOException {
        requireNonNull(greyBook);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableGreyBook(greyBook), filePath);
    }

}
