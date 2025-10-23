package greynekos.greybook.logic.parser.commandoption;

import java.text.ParseException;

import greynekos.greybook.logic.parser.ArgumentParser;
import greynekos.greybook.logic.parser.Prefix;

/**
 * Represents a required prefix option. If this option appears zero or more than
 * one time when parsing, a {@link ParseException} will be thrown.
 */
public class RequiredMutuallyExclusivePrefixOption<T> extends PrefixOption<T>
        implements
            OptionalOption<T>,
            MutuallyExclusiveOption<T> {
    private final String prefixGroup;

    private RequiredMutuallyExclusivePrefixOption(String prefixGroup, Prefix prefix, String name,
            ArgumentParser<T> parser) {
        super(prefix, name, parser);
        // TODO: Allow retrieval of option via the `prefixGroup` directly instead of via
        // each individual option
        this.prefixGroup = prefixGroup;
    }

    public static RequiredMutuallyExclusivePrefixOption<String> of(String prefixGroup, Prefix prefix, String name) {
        return new RequiredMutuallyExclusivePrefixOption<>(prefixGroup, prefix, name, x -> x);
    }

    public static <S> RequiredMutuallyExclusivePrefixOption<S> of(String prefixGroup, Prefix prefix, String name,
            ArgumentParser<S> parser) {
        return new RequiredMutuallyExclusivePrefixOption<>(prefixGroup, prefix, name, parser);
    }
}
