package greynekos.greybook.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;

import greynekos.greybook.commons.core.history.CommandHistory;
import greynekos.greybook.commons.exceptions.DataLoadingException;
import greynekos.greybook.commons.util.JsonUtil;
import greynekos.greybook.model.History;
import greynekos.greybook.model.ReadOnlyHistory;

/**
 * A class to access History stored in the hard disk as a json file
 */
public class JsonHistoryStorage implements HistoryStorage {

    private Path filePath;

    public JsonHistoryStorage(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Path getHistoryFilePath() {
        return filePath;
    }

    @Override
    public Optional<History> readHistory() throws DataLoadingException {
        return readHistory(filePath);
    }

    /**
     * Similar to {@link #readHistory()}
     *
     * @param historyFilePath
     *            location of the data. Cannot be null.
     * @throws DataLoadingException
     *             if the file format is not as expected.
     */
    public Optional<History> readHistory(Path historyFilePath) throws DataLoadingException {
        return JsonUtil.readJsonFile(historyFilePath, new TypeReference<List<String>>() {
        }).map(history -> new History(new CommandHistory(history)));
    }

    @Override
    public void saveHistory(ReadOnlyHistory history) throws IOException {
        JsonUtil.saveJsonFile(history.getCommandHistory().getHistory(), filePath);
    }

}
