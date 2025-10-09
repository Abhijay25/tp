package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import seedu.address.model.booking.Booking;
import seedu.address.model.person.Person;

/** An UI component that displays information of a {@code Person}. */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX. As
     * a consequence, UI elements' variable names cannot be set to such keywords or an exception
     * will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on
     *     AddressBook level 4</a>
     */

    // Array of color styles for tags
    private static final String[] TAG_COLORS = {
        "-fx-background-color: #FF6B6B; -fx-text-fill: white;", // Red
        "-fx-background-color: #4ECDC4; -fx-text-fill: white;", // Teal
        "-fx-background-color: #45B7D1; -fx-text-fill: white;", // Blue
        "-fx-background-color: #FFA07A; -fx-text-fill: white;", // Light Salmon
        "-fx-background-color: #98D8C8; -fx-text-fill: white;", // Mint
        "-fx-background-color: #F7DC6F; -fx-text-fill: black;", // Yellow
        "-fx-background-color: #BB8FCE; -fx-text-fill: white;", // Purple
        "-fx-background-color: #85C1E2; -fx-text-fill: white;", // Sky Blue
        "-fx-background-color: #F8B88B; -fx-text-fill: black;", // Peach
        "-fx-background-color: #52BE80; -fx-text-fill: white;", // Green
        "-fx-background-color: #EC7063; -fx-text-fill: white;", // Coral
        "-fx-background-color: #5DADE2; -fx-text-fill: white;", // Ocean Blue
        "-fx-background-color: #AF7AC5; -fx-text-fill: white;", // Lavender
        "-fx-background-color: #48C9B0; -fx-text-fill: white;", // Turquoise
        "-fx-background-color: #F5B041; -fx-text-fill: black;" // Orange
    };

    public final Person person;

    @FXML private HBox cardPane;
    @FXML private Label name;
    @FXML private Label id;
    @FXML private Label phone;
    @FXML private Label email;
    @FXML private FlowPane tags;
    @FXML private VBox bookings;

    /** Creates a {@code PersonCode} with the given {@code Person} and index to display. */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        email.setText(person.getEmail().value);
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(
                        tag -> {
                            Label tagLabel = new Label(tag.tagName);
                            tagLabel.setStyle(getColorForTag(tag.tagName));
                            tags.getChildren().add(tagLabel);
                        });

        // Display bookings
        person.getBookings().stream()
                .sorted(Comparator.comparing(Booking::getDateTime))
                .forEach(
                        booking -> {
                            Label bookingLabel =
                                    new Label(
                                            String.format(
                                                    "📅 %s with %s - %s",
                                                    booking.getDateTimeString(),
                                                    booking.getClientName(),
                                                    booking.getDescription()));
                            bookingLabel.setWrapText(true);
                            bookingLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #3e7b91;");
                            bookings.getChildren().add(bookingLabel);
                        });
    }

    /**
     * Returns a color style for a tag based on its name. The same tag name will always get the same
     * color.
     */
    private String getColorForTag(String tagName) {
        int hash = Math.abs(tagName.hashCode());
        int colorIndex = hash % TAG_COLORS.length;
        return TAG_COLORS[colorIndex];
    }
}
