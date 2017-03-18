package sepm.ss17.e1526280.gui.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.Reservation;
import sepm.ss17.e1526280.gui.components.ReservationEntryDeleteCell;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationEntryWrapper;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationWrapper;
import sepm.ss17.e1526280.service.ReservationDataService;
import sepm.ss17.e1526280.service.exception.DataException;
import sepm.ss17.e1526280.util.DataServiceManager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 14.03.17
 */
public class ReservationEditController {

    /** Logger for logging **/
    private static final Logger LOG = LoggerFactory.getLogger(ReservationEditController.class);

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
    private ReservationWrapper coreData;

    /**
     * Initializes all the Data which is needed by the Controller
     */
    @FXML
    public void initialize() {
        LOG.trace("initialize");

        boxCol.setCellValueFactory(new PropertyValueFactory<>("boxId"));
        horseCol.setCellValueFactory(new PropertyValueFactory<>("horse"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        delCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        delCol.setCellFactory((TableColumn<ReservationEntryWrapper, Void> boxStringTableColumn) -> new ReservationEntryDeleteCell(toDelete, resTable));
    }

    /**
     * Lazy initializes the Controller with the Data
     * @param wrapper data for the initialization
     */
    public void init(ReservationWrapper wrapper) {
        LOG.debug("Init with data: " + wrapper);

        final List<ReservationEntryWrapper> convData = new ArrayList<>();
        wrapper.getBoxes().forEach(reservation -> convData.add(new ReservationEntryWrapper(reservation,wrapper.getDays())));
        this.coreData = wrapper;

        customer.setText(wrapper.getName());
        startDate.setValue(wrapper.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        endDate.setValue(wrapper.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        // Set Data to GUI
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
    public boolean validate() throws DataException {
        LOG.trace("Validate Input");

        if( customer.getText().length() <= 0 )
            return false;

        final ReservationDataService service = DataServiceManager.getService().getReservationDataService();
        final List<Box> currentBoxes = resTable.getItems().stream().map(Reservation::getBox).collect(Collectors.toList());
        final List<Integer> reservations = coreData.getBoxes()
                                                   .stream()
                                                   .mapToInt(Reservation::getId)
                                                   .boxed()
                                                   .collect(Collectors.toList());

        boolean noOverlap = true;
        for(Box box: currentBoxes) {
            noOverlap = service.queryFor(box, getStartDate(), getEndDate()).join()
                                                                            .stream()
                                                                            .mapToInt(Reservation::getId)
                                                                            .boxed()
                                                                            .allMatch(reservations::contains);
            if( !noOverlap )
                break;
        }

        LOG.trace("\tResult => " + noOverlap);
        return noOverlap;
    }

    public void setOkBtnEventHandler(EventHandler<ActionEvent> okBtnEventHandler) {
        okBtn.setOnAction(okBtnEventHandler);
    }

    @FXML
    @SuppressWarnings("MethodMayBeStatic") // If static -> FXML can not bind to it
    public void onClose(ActionEvent event) {
        LOG.trace("onClose");
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
