package seedu.address.model.booking;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a Booking in the address book. Guarantees: immutable; fields are validated and not
 * null.
 */
public class Booking {
    public static final String MESSAGE_CONSTRAINTS_DATETIME =
            "Invalid date: must be in format YYYY-MM-DD HH:MM and in the future.";
    public static final String MESSAGE_CONSTRAINTS_CLIENT =
            "Invalid client name. Must be 1-100 characters with at least one letter. "
                    + "Only letters, numbers, spaces, hyphens, apostrophes, and periods are allowed.";
    public static final String MESSAGE_CONSTRAINTS_DESCRIPTION =
            "Description must be 1-500 characters.";

    public static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Validation regex for client name
    private static final String CLIENT_NAME_VALIDATION_REGEX = "^[a-zA-Z0-9 .'-]+$";

    private static final AtomicLong idCounter = new AtomicLong(1);

    private final String id;
    private final String clientName;
    private final LocalDateTime datetime;
    private final String description;

    /**
     * Constructs a {@code Booking} with auto-generated ID.
     *
     * @param clientName Client name for the booking.
     * @param datetime DateTime of the booking.
     * @param description Description of the booking.
     */
    public Booking(String clientName, LocalDateTime datetime, String description) {
        this(String.valueOf(idCounter.getAndIncrement()), clientName, datetime, description);
    }

    /**
     * Constructs a {@code Booking} with specified ID (for loading from storage).
     *
     * @param id Unique identifier for the booking.
     * @param clientName Client name for the booking.
     * @param datetime DateTime of the booking.
     * @param description Description of the booking.
     */
    public Booking(String id, String clientName, LocalDateTime datetime, String description) {
        requireNonNull(id);
        requireNonNull(clientName);
        requireNonNull(datetime);
        requireNonNull(description);

        checkArgument(isValidClientName(clientName), MESSAGE_CONSTRAINTS_CLIENT);
        checkArgument(isValidDescription(description), MESSAGE_CONSTRAINTS_DESCRIPTION);

        this.id = id;
        this.clientName = clientName.trim().replaceAll("\\s+", " "); // Normalize spaces
        this.datetime = datetime;
        this.description = description.trim();

        // Update counter if loading from storage
        try {
            long loadedId = Long.parseLong(id);
            idCounter.updateAndGet(current -> Math.max(current, loadedId + 1));
        } catch (NumberFormatException e) {
            // Non-numeric ID, ignore
        }
    }

    /** Returns true if a given string is a valid client name. */
    public static boolean isValidClientName(String test) {
        if (test == null || test.trim().isEmpty()) {
            return false;
        }
        String trimmed = test.trim();
        if (trimmed.length() < 1 || trimmed.length() > 100) {
            return false;
        }
        if (!trimmed.matches(CLIENT_NAME_VALIDATION_REGEX)) {
            return false;
        }
        // Must contain at least one letter
        return trimmed.matches(".*[a-zA-Z].*");
    }

    /** Returns true if a given string is a valid description. */
    public static boolean isValidDescription(String test) {
        if (test == null) {
            return false;
        }
        String trimmed = test.trim();
        return trimmed.length() >= 1 && trimmed.length() <= 500;
    }

    /** Parses a datetime string in the format YYYY-MM-DD HH:MM. Returns null if parsing fails. */
    public static LocalDateTime parseDateTime(String datetimeStr) {
        try {
            return LocalDateTime.parse(datetimeStr, DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** Returns true if the datetime is in the future. */
    public static boolean isFutureDateTime(LocalDateTime datetime) {
        return datetime.isAfter(LocalDateTime.now());
    }

    public String getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public LocalDateTime getDateTime() {
        return datetime;
    }

    public String getDescription() {
        return description;
    }

    public String getDateTimeString() {
        return datetime.format(DATETIME_FORMATTER);
    }

    /** Returns true if this booking conflicts with another booking (same datetime). */
    public boolean conflictsWith(Booking other) {
        return this.datetime.equals(other.datetime);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Booking)) {
            return false;
        }

        Booking otherBooking = (Booking) other;
        return id.equals(otherBooking.id)
                && clientName.equals(otherBooking.clientName)
                && datetime.equals(otherBooking.datetime)
                && description.equals(otherBooking.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientName, datetime, description);
    }

    @Override
    public String toString() {
        return String.format(
                "[ID: %s] Client: %s at %s - %s", id, clientName, getDateTimeString(), description);
    }
}
