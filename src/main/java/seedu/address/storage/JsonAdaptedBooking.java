package seedu.address.storage;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.booking.Booking;

/** Jackson-friendly version of {@link Booking}. */
class JsonAdaptedBooking {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Booking's %s field is missing!";

    private final String id;
    private final String clientName;
    private final String datetime;
    private final String description;

    /** Constructs a {@code JsonAdaptedBooking} with the given booking details. */
    @JsonCreator
    public JsonAdaptedBooking(
            @JsonProperty("id") String id,
            @JsonProperty("clientName") String clientName,
            @JsonProperty("datetime") String datetime,
            @JsonProperty("description") String description) {
        this.id = id;
        this.clientName = clientName;
        this.datetime = datetime;
        this.description = description;
    }

    /** Converts a given {@code Booking} into this class for Jackson use. */
    public JsonAdaptedBooking(Booking source) {
        id = source.getId();
        clientName = source.getClientName();
        datetime = source.getDateTimeString();
        description = source.getDescription();
    }

    /**
     * Converts this Jackson-friendly adapted booking object into the model's {@code Booking}
     * object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted
     *     booking.
     */
    public Booking toModelType() throws IllegalValueException {
        if (id == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "id"));
        }

        if (clientName == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, "clientName"));
        }
        if (!Booking.isValidClientName(clientName)) {
            throw new IllegalValueException(Booking.MESSAGE_CONSTRAINTS_CLIENT);
        }
        final String modelClientName = clientName;

        if (datetime == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, "datetime"));
        }
        final LocalDateTime modelDatetime = Booking.parseDateTime(datetime);
        if (modelDatetime == null) {
            throw new IllegalValueException(Booking.MESSAGE_CONSTRAINTS_DATETIME);
        }

        if (description == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, "description"));
        }
        if (!Booking.isValidDescription(description)) {
            throw new IllegalValueException(Booking.MESSAGE_CONSTRAINTS_DESCRIPTION);
        }
        final String modelDescription = description;

        return new Booking(id, modelClientName, modelDatetime, modelDescription);
    }
}
