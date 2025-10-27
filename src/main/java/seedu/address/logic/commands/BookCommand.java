package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLIENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.ErrorMessage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.booking.Booking;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

/**
 * Books an appointment for a team member with a client.
 */
public class BookCommand extends Command {

    public static final String COMMAND_WORD = "book";

    public static final ErrorMessage MESSAGE_USAGE = new ErrorMessage(
            "Books an appointment for a team member.",
            PREFIX_DATETIME + "DATETIME (YYYY-MM-DD HH:MM) "
                    + PREFIX_CLIENT + "CLIENT_NAME "
                    + PREFIX_NAME + "PERSON_NAME "
                    + "[" + PREFIX_DESCRIPTION + "DESCRIPTION]",
            COMMAND_WORD + " "
                    + PREFIX_DATETIME + "2025-09-18 14:00 "
                    + PREFIX_CLIENT + "Mr Lim "
                    + PREFIX_NAME + "Alice Tan "
                    + PREFIX_DESCRIPTION + "first consultation"
    );

    public static final String MESSAGE_SUCCESS = "Booked: %1$s with client '%2$s' at %3$s [%4$s]";
    public static final String MESSAGE_PERSON_NOT_FOUND = "No team member '%1$s'. Please use an existing team member "
            + "or add them first.";
    public static final String MESSAGE_DOUBLE_BOOKING = "%1$s is already booked at %2$s with client '%3$s' for [%4$s].";

    private static final Logger logger = LogsCenter.getLogger(BookCommand.class);

    private final Name personName;
    private final String clientName;
    private final LocalDateTime datetime;
    private final String description;

    /**
     * Creates a BookCommand to add the specified booking.
     */
    public BookCommand(Name personName, String clientName, LocalDateTime datetime, String description) {
        requireNonNull(personName);
        requireNonNull(clientName);
        requireNonNull(datetime);
        requireNonNull(description);

        this.personName = personName;
        this.clientName = clientName;
        this.datetime = datetime;
        this.description = description;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        logger.info(String.format("Executing BookCommand for person: %s with client: %s at %s",
                personName, clientName, datetime));

        // Find the person by name
        Person personToBook = null;
        for (Person person : model.getAddressBook().getPersonList()) {
            if (person.getName().equals(personName)) {
                personToBook = person;
                break;
            }
        }

        if (personToBook == null) {
            logger.warning(String.format("Person not found for booking: %s", personName));
            throw new CommandException(String.format(MESSAGE_PERSON_NOT_FOUND, personName));
        }

        // Check for double booking
        Booking newBooking = new Booking(clientName, datetime, description);
        for (Booking existingBooking : personToBook.getBookings()) {
            if (existingBooking.conflictsWith(newBooking)) {
                logger.warning(String.format("Double booking detected for %s at %s", personName, datetime));
                throw new CommandException(String.format(MESSAGE_DOUBLE_BOOKING,
                        personName,
                        existingBooking.getDateTimeString(),
                        existingBooking.getClientName(),
                        existingBooking.getDescription()));
            }
        }

        // Create updated person with new booking
        List<Booking> updatedBookings = new ArrayList<>(personToBook.getBookings());
        updatedBookings.add(newBooking);

        Person updatedPerson = new Person(
                personToBook.getName(),
                personToBook.getPhone(),
                personToBook.getEmail(),
                personToBook.getTags(),
                updatedBookings
        );

        model.setPerson(personToBook, updatedPerson);

        logger.info(String.format("Successfully booked appointment for %s with %s at %s",
                personName, clientName, datetime));

        // Check if booking is in the past
        boolean isPastDate = !Booking.isFutureDateTime(datetime);
        String resultMessage = String.format(MESSAGE_SUCCESS,
                personName,
                clientName,
                newBooking.getDateTimeString(),
                description);

        if (isPastDate) {
            resultMessage += "\nNote that this is a Booking that is in the past!";
        }

        return new CommandResult(resultMessage);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof BookCommand)) {
            return false;
        }

        BookCommand otherBookCommand = (BookCommand) other;
        return personName.equals(otherBookCommand.personName)
                && clientName.equals(otherBookCommand.clientName)
                && datetime.equals(otherBookCommand.datetime)
                && description.equals(otherBookCommand.description);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(personName, clientName, datetime, description);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("personName", personName)
                .add("clientName", clientName)
                .add("datetime", datetime)
                .add("description", description)
                .toString();
    }
}
