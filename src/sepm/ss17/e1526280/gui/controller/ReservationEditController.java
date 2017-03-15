package sepm.ss17.e1526280.gui.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.Reservation;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationEntryWrapper;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationWrapper;
import sepm.ss17.e1526280.service.ReservationDataService;
import sepm.ss17.e1526280.util.DataServiceManager;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 14.03.17
 */
public class ReservationEditController {

    @FXML private Button okBtn;
    @FXML private TableView<ReservationEntryWrapper> resTable;
    @FXML private TableColumn<ReservationEntryWrapper,Integer> boxCol;
    @FXML private TableColumn<ReservationEntryWrapper,String> horseCol;
    @FXML private TableColumn<ReservationEntryWrapper,Float> priceCol;
    @FXML private TableColumn<ReservationEntryWrapper,Void> delCol;
    @FXML private TextField customer;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;

    private final List<ReservationEntryWrapper> toDelete = new ArrayList<>();
    private ReservationWrapper coreData = null;

    @FXML
    public void initialize() {
        boxCol.setCellValueFactory(new PropertyValueFactory<>("boxId"));
        horseCol.setCellValueFactory(new PropertyValueFactory<>("horse"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        delCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        delCol.setCellFactory((TableColumn<ReservationEntryWrapper, Void> boxStringTableColumn) -> new TableCell<ReservationEntryWrapper, Void>() {
            final Button deleteBtn = new Button("Löschen");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(deleteBtn);
                    setText(null);

                    final ReservationEntryWrapper curObj = getTableView().getItems().get(getIndex());

                    deleteBtn.setOnAction(event -> {
                        final Alert question = new Alert(Alert.AlertType.CONFIRMATION);
                        question.setTitle(GlobalSettings.APP_TITLE +": " + "Bestätigung");
                        question.setHeaderText("Reservierung Löschen");
                        question.setContentText("Soll dieser teil der Reservierung gelöscht werden?");

                        Optional<ButtonType> result = question.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            toDelete.add(curObj);
                            resTable.getItems().remove(curObj);
                            resTable.refresh();
                        }
                    });
                }
            }
        });
    }

    public void init(ReservationWrapper wrapper) {
        final List<ReservationEntryWrapper> convData = new ArrayList<>();
        wrapper.getBoxes().forEach(reservation -> convData.add(new ReservationEntryWrapper(reservation,wrapper.getDays())));
        this.coreData = wrapper;


        customer.setText(wrapper.getName());
        startDate.setValue(wrapper.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        endDate.setValue(wrapper.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        resTable.setItems(FXCollections.observableArrayList(convData));

        //Validators:
        startDate.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if( newValue.isAfter(endDate.getValue()) || newValue.isBefore(LocalDate.now()) )
                startDate.setValue(oldValue);
        });

        endDate.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if( newValue.isBefore(startDate.getValue()) )
                endDate.setValue(oldValue);
        });
    }

    /**
     * This Method might trow a RuntimeException received from the Future in the Service
     * @return true if the data is valid otherwise false
     */
    public boolean validate() {
        if( customer.getText().length() <= 0 )
            return false;

        final ReservationDataService service = DataServiceManager.getService().getReservationDataService();
        final List<Box> currentBoxes = resTable.getItems().stream().map(Reservation::getBox).collect(Collectors.toList());
        final List<Box> blocked = service.queryBlocked(currentBoxes, getStartDate(), getEndDate()).join();

        return blocked.size() == currentBoxes.size();
    }

    public void setOkBtnEventHandler(EventHandler<ActionEvent> okBtnEventHandler) {
        okBtn.setOnAction(okBtnEventHandler);
    }

    public void onClose(ActionEvent event) {
        ((Stage) ((Button)event.getSource()).getScene().getWindow()).close();
    }

    public List<ReservationEntryWrapper> getToDelete() {
        return toDelete;
    }

    public List<ReservationEntryWrapper> getToUpdate() {
        return resTable.getItems().stream().collect(Collectors.toList());
    }

    public Date getStartDate() {
        return Date.from(this.startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Date getEndDate() {
        return Date.from(this.endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public String getName() {
        return customer.getText();
    }
}
