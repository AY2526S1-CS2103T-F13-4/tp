package greynekos.greybook.model;

import greynekos.greybook.commons.core.history.CommandHistory;

/**
 * Unmodifiable view of history.
 */
public interface ReadOnlyHistory {

    CommandHistory getCommandHistory();

}
