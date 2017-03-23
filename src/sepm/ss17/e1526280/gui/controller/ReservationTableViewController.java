package sepm.ss17.e1526280.gui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dto.Reservation;
import sepm.ss17.e1526280.gui.components.ReservationDeleteCell;
import sepm.ss17.e1526280.gui.components.ReservationDetailCell;
import sepm.ss17.e1526280.gui.components.ReservationEditCell;
import sepm.ss17.e1526280.gui.components.ReservationInvoiceCell;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationWrapper;
import sepm.ss17.e1526280.gui.dialogs.CustomDialog;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.service.ReservationDataService;
import sepm.ss17.e1526280.util.DataServiceManager;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 11.03.17
 */
public class ReservationTableViewController {

    /** Logger for logging **/
    private static final Logger LOG = LoggerFactory.getLogger(ReservationTableViewController.class);

    private final ReservationDataService dataService = DataServiceManager.getService().getReservationDataService();
    private final ObservableList<ReservationWrapper> dataList = FXCollections.observableList(new ArrayList<>());

    @FXML private TableView<ReservationWrapper> tableView;
    @FXML private TableColumn<ReservationWrapper,String> customerCol;
    @FXML private TableColumn<ReservationWrapper,String> boxCountColl;
    @FXML private TableColumn<ReservationWrapper,Void> boxDetailCol;
    @FXML private TableColumn<ReservationWrapper,String> priceCol;
    @FXML private TableColumn<ReservationWrapper,Void> deleteCol;
    @FXML private TableColumn<ReservationWrapper,Void> invoiceCol;
    @FXML private TableColumn<ReservationWrapper,String> startCol;
    @FXML private TableColumn<ReservationWrapper,String> endCol;
    @FXML private TableColumn<ReservationWrapper,Void> editCol;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private CheckBox timeChecker;

    /**
     * Initializes all the Data which is needed by the Controller
     */
    @FXML
    public void initialize() {
        customerCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        boxCountColl.setCellValueFactory(new PropertyValueFactory<>("count"));
        boxDetailCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        deleteCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        invoiceCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startString"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endString"));

        boxDetailCol.setCellFactory((TableColumn<ReservationWrapper, Void> boxStringTableColumn) -> new ReservationDetailCell());
        deleteCol.setCellFactory((TableColumn<ReservationWrapper, Void> boxStringTableColumn) -> new ReservationDeleteCell(dataService,dataList));
        invoiceCol.setCellFactory((TableColumn<ReservationWrapper, Void> boxStringTableColumn) -> new ReservationInvoiceCell(dataService,dataList));
        editCol.setCellFactory((TableColumn<ReservationWrapper, Void> boxStringTableColumn) -> new ReservationEditCell(dataService, this));

        startDate.setValue(LocalDate.now());
        endDate.setValue(LocalDate.now());

        //Validators:
        startDate.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if( newValue.isAfter(endDate.getValue()) )
                startDate.setValue(oldValue);
        });

        endDate.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if( newValue.isBefore(startDate.getValue()) )
                endDate.setValue(oldValue);
        });

        timeChecker.setOnAction(event -> {
            startDate.setDisable( !timeChecker.isSelected() );
            endDate.setDisable( !timeChecker.isSelected() );
        });

        loadData();
        tableView.setItems(dataList);
    }

    @FXML
    public void onNewReservation(ActionEvent event) {
        final Stage parentStage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        final String fxml = GlobalSettings.FXML_ROOT+"/"+"reservation_boxchooser.fxml";

        CustomDialog<ReservationChooserController> dialog = new CustomDialog<ReservationChooserController>(parentStage,GlobalSettings.APP_TITLE + ": Boxen auswählen",fxml) {};
        ReservationChooserController controller = dialog.getController();

        controller.setOkBtnEventHandler(event1 -> {
            if( !controller.validate() ) {
                showOnValidationError();
                return;
            }

            dataService.persist(controller.getReservations())
                       .thenAccept(this::addData)
                       .exceptionally(DialogUtil::onError);

            ((Stage) ((Button)event1.getSource()).getScene().getWindow()).close();
        });


        dialog.show();
    }

    public void loadData() {
        dataList.clear();

        if( !timeChecker.isSelected() )
            dataService.queryGrouped(false)
                       .thenApply(this::asyncUpdate)
                       .exceptionally(DialogUtil::onFatal);
        else {
            final Date start = Date.from(this.startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            final Date end = Date.from(this.endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

            dataService.queryAllIn(start, end)
                    .thenApply(this::asyncUpdate)
                    .exceptionally(DialogUtil::onFatal);
        }

    }

    @Nullable
    private Void asyncUpdate(Collection<List<Reservation>> lists) {
        Platform.runLater(() ->
                lists.forEach(reservations ->
                        dataList.add(new ReservationWrapper(reservations))
                )
        );

        return null;
    }

    private void addData(List<Reservation> rs) {
        Platform.runLater(() -> dataList.add(new ReservationWrapper(rs)));
    }

    private static void showOnValidationError() {
        final Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Warning: " + GlobalSettings.APP_TITLE);
        a.setHeaderText("Es ist ein Fehler bei der Verarbeitung aufgetreten!");
        a.setContentText("Überprüfen Sie alle eingegeben Werte!");
        a.show();
    }

    @FXML
    public void onRefresh(ActionEvent event) {
        loadData();
    }
}
