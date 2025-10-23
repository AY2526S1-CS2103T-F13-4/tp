package greynekos.greybook.logic.parser.commandoption;

import greynekos.greybook.logic.parser.commandoption.NoDuplicateOption;

/**
 * An interface representing an option that cannot appear more than once in the
 * command
 */
public interface MutuallyExclusiveOption<T> extends NoDuplicateOption<T> {
}
