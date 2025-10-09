package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;
import java.util.List;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ClientContainsKeywordsPredicate;

/** Parses input arguments and creates a new FindCommand object */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand and returns a
     * FindCommand object for execution. * @throws ParseException if the user input does not conform
     * the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        String[] tokens = trimmedArgs.split("\\s+", 2);
        String prefix = tokens[0];
        if (tokens.length < 2) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        String argument = tokens[1].trim();
        List<String> keywords = Arrays.asList(argument.split("\\s+"));

        switch (prefix) {
        case "/n":
            return new FindCommand(
                    new ClientContainsKeywordsPredicate(
                            ClientContainsKeywordsPredicate.SearchType.NAME, keywords));
        case "/t":
            return new FindCommand(
                    new ClientContainsKeywordsPredicate(
                            ClientContainsKeywordsPredicate.SearchType.TAG, keywords));
        default:
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }
}
