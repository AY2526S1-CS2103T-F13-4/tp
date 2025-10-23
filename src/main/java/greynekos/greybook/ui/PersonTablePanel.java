package greynekos.greybook.ui;

import java.util.logging.Logger;

import greynekos.greybook.commons.core.LogsCenter;
import greynekos.greybook.model.person.AttendanceStatus;
import greynekos.greybook.model.person.Person;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;

/**
 * Panel containing the list of persons.
 */
public class PersonTablePanel extends UiPart<Region> {
    private static final String FXML = "PersonTablePanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonTablePanel.class);

    @FXML
    private TableView<Person> personTableView;

    @FXML
    private TableColumn<Person, String> indexColumn;

    @FXML
    private TableColumn<Person, String> nameColumn;

    @FXML
    private TableColumn<Person, String> studentIdColumn;

    @FXML
    private TableColumn<Person, String> emailColumn;

    @FXML
    private TableColumn<Person, String> phoneColumn;

    @FXML
    private TableColumn<Person, String> tagsColumn;

    @FXML
    private TableColumn<Person, String> attendanceStatusColumn;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonTablePanel(ObservableList<Person> personList) {
        super(FXML);
        indexColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateIndex(int index) {
                super.updateIndex(index);
                if (isEmpty() || index < 0) {
                    setText(null);
                } else {
                    setText(Integer.toString(index + 1)); // +1 to make it 1-based index
                }
            }
        });

        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName().toString()));
        studentIdColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getStudentID().toString()));
        emailColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail().toString()));
        phoneColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone().toString()));
        tagsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTags().stream().map(Object::toString).reduce((x, y) -> x + "," + y).orElse("")));
        attendanceStatusColumn.setCellValueFactory(cellData -> {
            AttendanceStatus status = cellData.getValue().getAttendance();
            String display = (status.value == AttendanceStatus.Status.NONE) ? "" : status.toString();
            return new SimpleStringProperty(display);
        });

        personTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        personTableView.setItems(personList);
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using
     * a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1).getRoot());
            }
        }
    }

}
