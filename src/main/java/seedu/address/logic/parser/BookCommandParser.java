package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLIENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import seedu.address.logic.commands.BookCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.booking.Booking;
import seedu.address.model.person.Name;

/** Parses input arguments and creates a new BookCommand object */
public class BookCommandParser implements Parser<BookCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the BookCommand and returns a
     * BookCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public BookCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(
                        args, PREFIX_DATETIME, PREFIX_CLIENT, PREFIX_NAME, PREFIX_DESCRIPTION);

        if (!arePrefixesPresent(argMultimap, PREFIX_DATETIME, PREFIX_CLIENT, PREFIX_NAME)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, BookCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(
                PREFIX_DATETIME, PREFIX_CLIENT, PREFIX_NAME, PREFIX_DESCRIPTION);

        // Parse datetime
        String datetimeStr = argMultimap.getValue(PREFIX_DATETIME).get().trim();
        LocalDateTime datetime = Booking.parseDateTime(datetimeStr);
        if (datetime == null) {
            throw new ParseException(Booking.MESSAGE_CONSTRAINTS_DATETIME);
        }
        if (!Booking.isFutureDateTime(datetime)) {
            throw new ParseException(Booking.MESSAGE_CONSTRAINTS_DATETIME);
        }

        // Parse client name
        String clientName = argMultimap.getValue(PREFIX_CLIENT).get().trim();
        if (!Booking.isValidClientName(clientName)) {
            throw new ParseException(Booking.MESSAGE_CONSTRAINTS_CLIENT);
        }

        // Parse person name
        Name personName = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());

        // Parse description (optional)
        String description = argMultimap.getValue(PREFIX_DESCRIPTION).orElse("").trim();
        if (description.isEmpty()) {
            description = "No description provided";
        }
        if (!Booking.isValidDescription(description)) {
            throw new ParseException(Booking.MESSAGE_CONSTRAINTS_DESCRIPTION);
        }

        return new BookCommand(personName, clientName, datetime, description);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(
            ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes)
                .allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
