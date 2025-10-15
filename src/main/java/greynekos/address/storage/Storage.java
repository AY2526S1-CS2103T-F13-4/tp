package greynekos.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import greynekos.address.commons.exceptions.DataLoadingException;
import greynekos.address.model.ReadOnlyAddressBook;
import greynekos.address.model.ReadOnlyUserPrefs;
import greynekos.address.model.UserPrefs;

/**
 * API of the Storage component
 */
public interface Storage extends AddressBookStorage, UserPrefsStorage {

    @Override
    Optional<UserPrefs> readUserPrefs() throws DataLoadingException;

    @Override
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    @Override
    Path getAddressBookFilePath();

    @Override
    Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException;

    @Override
    void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException;

}
