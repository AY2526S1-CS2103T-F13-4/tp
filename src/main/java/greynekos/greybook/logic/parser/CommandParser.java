package greynekos.greybook.logic.parser;

import static greynekos.greybook.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import greynekos.greybook.logic.commands.Command;
import greynekos.greybook.logic.parser.commandoption.NoDuplicateOption;
import greynekos.greybook.logic.parser.commandoption.OneOrMorePreambleOption;
import greynekos.greybook.logic.parser.commandoption.Option;
import greynekos.greybook.logic.parser.commandoption.OptionalSinglePreambleOption;
import greynekos.greybook.logic.parser.commandoption.PrefixOption;
import greynekos.greybook.logic.parser.commandoption.RequiredOption;
import greynekos.greybook.logic.parser.commandoption.SinglePreambleOption;
import greynekos.greybook.logic.parser.exceptions.ParseException;

/**
 * Stores the options associated with its command and handles parsing arguments
 * under their respective options.
 */
public class CommandParser {
    private List<Option<?>> options = new ArrayList<>();
    private String messageUsage;
    private Command command;
    private final List<Option<?>[]> exclusiveGroups = new ArrayList<>();
    private boolean enforceOnePreamble = false;

    CommandParser(String messageUsage, Command command) {
        this.messageUsage = messageUsage;
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    /**
     * Adds the options to this parser, do not add duplicate options
     */
    public CommandParser addOptions(Option<?>... options) {
        for (Option<?> option : options) {
            this.options.add(option);
        }
        return this;
    }

    /**
     * Adds the option to this parser, do not add duplicate options
     */
    public CommandParser addOption(Option<?> option) {
        options.add(option);
        return this;
    }

    /**
     * Enables validation that only one preamble can have a value
     */
    public CommandParser enforceOnePreamble() {
        this.enforceOnePreamble = true;
        return this;
    }

    /**
     * Adds a group of mutually exclusive options
     */
    public CommandParser addExclusiveOptions(Option<?>... options) {
        exclusiveGroups.add(options);
        return this;
    }

    /**
     * Does most of the parsing, finds which arguments are associated with which
     * options and parses them accordingly. Also makes sure the user input obeys the
     * correct format as defined by the options
     *
     * @param arguments
     *            The argument portion of the user input.
     * @returns A {@link ArgumentParseResult} if successfully parsed
     * @throws ParseException
     *             If the user input does not obey the format defined by the
     *             options. This usually falls under one of three cases: <br/>
     *             1. There are duplicate prefixes when there should not be <br/>
     *             2. Required options are not present <br/>
     *             3. The input is not formatted correctly as defined by the
     *             {@link ArgumentParser} <br/>
     *             4. More than one identifier or flag is provided when only one is
     *             allowed
     */
    public ArgumentParseResult parse(String arguments) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(arguments, getPrefixOptions());

        if (!arePrefixesPresent(argMultimap, getRequiredOptions())) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(getNoDuplicateOptions());

        Map<Option<?>, List<?>> optionArgumentToResult = new HashMap<>();
        for (Option<?> option : options) {
            List<Object> result = new ArrayList<>();
            if (option instanceof OneOrMorePreambleOption) {
                for (String preamble : argMultimap.getPreamble().split("\\s+")) {
                    result.add(option.parseOptionArgument(preamble));
                }
            } else if (option instanceof SinglePreambleOption) {
                result.add(option.parseOptionArgument(argMultimap.getPreamble()));
            } else if (option instanceof OptionalSinglePreambleOption) {
                String preamble = argMultimap.getPreamble();
                if (!preamble.isEmpty()) {
                    try {
                        result.add(option.parseOptionArgument(preamble));
                    } catch (ParseException e) {
                        // Ignore empty preamble options
                    }
                }
            } else {
                for (String arg : argMultimap.getAllValues(option.getPrefix())) {
                    result.add(option.parseOptionArgument(arg));
                }
            }
            optionArgumentToResult.put(option, result);
        }

        // Perform validations if configured
        if (enforceOnePreamble) {
            verifyAtMostOneSinglePreamble(optionArgumentToResult);
        }
        if (!exclusiveGroups.isEmpty()) {
            verifyExclusiveGroups(optionArgumentToResult);
        }

        return new ArgumentParseResult(command, optionArgumentToResult);
    }

    /**
     * Validates that at most one single preamble option is provided. StudentID
     * prefix option is also counted as a single preamble option.
     *
     * @param optionResults
     *            the parsed options and their results
     * @throws ParseException
     *             if zero or more than one single preamble is present
     */
    private void verifyAtMostOneSinglePreamble(Map<Option<?>, List<?>> optionResults) throws ParseException {
        long identifierCount = options.stream().filter(opt -> opt instanceof OptionalSinglePreambleOption)
                .map(optionResults::get)
                .filter(list -> list != null && !list.isEmpty() && list.stream().anyMatch(Objects::nonNull)).count();

        if (identifierCount != 1) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage));
        }
    }

    /**
     * Validates mutually exclusive option groups.
     *
     * @param optionResults
     *            the parsed options and their results
     * @throws ParseException
     *             if more than one option in a group is present
     */
    private void verifyExclusiveGroups(Map<Option<?>, List<?>> optionResults) throws ParseException {
        for (Option<?>[] group : exclusiveGroups) {
            long presentCount = Stream.of(group).map(optionResults::get)
                    .filter(list -> list != null && !list.isEmpty() && list.stream().anyMatch(Objects::nonNull))
                    .count();
            if (presentCount > 1) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage));
            }
        }
    }

    private Prefix[] getPrefixOptions() {
        return filterOptionsByInstance(PrefixOption.class);
    }

    private Prefix[] getRequiredOptions() {
        return filterOptionsByInstance(RequiredOption.class);
    }

    private Prefix[] getNoDuplicateOptions() {
        return filterOptionsByInstance(NoDuplicateOption.class);
    }

    private Prefix[] filterOptionsByInstance(Class<?> cls) {
        return options.stream().filter(option -> cls.isInstance(option)).map(Option::getPrefix).toList()
                .toArray(new Prefix[0]);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values
     * in the given {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
