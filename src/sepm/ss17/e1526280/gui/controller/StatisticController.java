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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.StatisticRow;
import sepm.ss17.e1526280.gui.components.StatChartCell;
import sepm.ss17.e1526280.gui.controller.wrapper.StatisticRowWrapper;
import sepm.ss17.e1526280.gui.dialogs.CustomDialog;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.gui.dialogs.StatPriceChangeDialog;
import sepm.ss17.e1526280.service.BoxDataService;
import sepm.ss17.e1526280.service.StatisticalService;
import sepm.ss17.e1526280.util.DataServiceManager;
import sepm.ss17.e1526280.util.DateUtil;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller which does manage the main View of the Statistics
 *
 * @author Raphael Ludwig
 * @version 16.03.17
 */

public class StatisticController {

    /** Logger for logging **/
    private static final Logger LOG = LoggerFactory.getLogger(StatisticController.class);

    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private TableView<StatisticRowWrapper> tableView;
    @FXML private TableColumn<StatisticRowWrapper, Integer> boxCol;
    @FXML private TableColumn<StatisticRowWrapper, Integer> resCountCol;
    @FXML private TableColumn<StatisticRowWrapper, Integer> dayMonCol;
    @FXML private TableColumn<StatisticRowWrapper, Integer> dayDiCol;
    @FXML private TableColumn<StatisticRowWrapper, Integer> dayMiCol;
    @FXML private TableColumn<StatisticRowWrapper, Integer> dayDoCol;
    @FXML private TableColumn<StatisticRowWrapper, Integer> dayFrCol;
    @FXML private TableColumn<StatisticRowWrapper, Integer> daySaCol;
    @FXML private TableColumn<StatisticRowWrapper, Integer> daySoCol;
    @FXML private TableColumn<StatisticRowWrapper, Void> graphCol;
    @FXML private TableColumn<StatisticRowWrapper, CheckBox> checkCol;
    @FXML private TableColumn<StatisticRowWrapper, Button> detailsCol;

    private final StatisticalService service = DataServiceManager.getService().getStatisticalService();
    private final BoxDataService boxService = DataServiceManager.getService().getBoxDataService();
    private final ObservableList<StatisticRowWrapper> dataList = FXCollections.observableList(new ArrayList<>());

    /**
     * Initializes all the Data which is needed by the Controller
     */
    @FXML
    public void initialize() {
        LOG.debug("initialize");

        boxCol.setCellValueFactory(      new PropertyValueFactory<>("boxID"));
        resCountCol.setCellValueFactory( new PropertyValueFactory<>("count"));
        dayMonCol.setCellValueFactory(   new PropertyValueFactory<>("monday"));
        dayDiCol.setCellValueFactory(    new PropertyValueFactory<>("tuesday"));
        dayMiCol.setCellValueFactory(    new PropertyValueFactory<>("wednesday"));
        dayDoCol.setCellValueFactory(    new PropertyValueFactory<>("thursday"));
        dayFrCol.setCellValueFactory(    new PropertyValueFactory<>("friday"));
        daySaCol.setCellValueFactory(    new PropertyValueFactory<>("saturday"));
        daySoCol.setCellValueFactory(    new PropertyValueFactory<>("sunday"));
        graphCol.setCellFactory((TableColumn<StatisticRowWrapper, Void> boxStringTableColumn) -> new StatChartCell());
        checkCol.setCellValueFactory(    new PropertyValueFactory<>("checkBox"));
        detailsCol.setCellValueFactory(  new PropertyValueFactory<>("details"));

        // Set initial data
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

        // Load Data
        loadData();
        tableView.setItems(dataList);
        tableView.getStylesheets().add(GlobalSettings.FXML_ROOT+"/custom_table.css");
    }

    @FXML
    public void onChangePrice(ActionEvent event) {
        LOG.trace("onChangePrice Event");

        final List<Box> boxes = dataList.stream()
                                        .filter(statisticRowWrapper -> statisticRowWrapper.getCheckBox().isSelected())
                                        .map(StatisticRow::getBox)
                                        .collect(Collectors.toList());

        if( boxes.size() > 0 ) {
            LOG.debug("Selected " + boxes.size() + " boxes");
            final CustomDialog<StatisticEditController> dialog = new StatPriceChangeDialog((Stage) ((Button)event.getSource()).getScene().getWindow());
            final StatisticEditController controller = dialog.getController();

            controller.setData(boxes);
            dialog.show();
        } else {
            LOG.warn("No Buttons selected!");
            showNoSelectionDialog();
        }

    }

    @FXML
    public void onGlobalStat(ActionEvent event) {

    }

    @FXML
    public void onReload(ActionEvent event) {
        LOG.trace("onReload Event");

        loadData();
    }

    /**
     *  Loads the Data into the TableView
     */
    private void loadData() {
        LOG.trace("LoadData into View");

        boxService.queryAll()
                .thenAccept(boxes -> {
                    final List<StatisticRow> data = service.query(boxes
                            , DateUtil.fromLocalDate(this.startDate.getValue())
                            , DateUtil.fromLocalDate(this.endDate.getValue())
                    ).join(); //We want the Data in this Thread, no need to query the Data in another Future

                    Platform.runLater(() -> dataList.setAll(registerDetailEvent(convert(data))));
                }).exceptionally(DialogUtil::onError);
    }

    /**
     * This function does register an event on each Detail button in the wrapper
     * @param in the List of StatisticRowWrapper where the Listener should be appended
     * @return the list which is received as in
     */
    private static List<StatisticRowWrapper> registerDetailEvent(List<StatisticRowWrapper> in) {
        in.forEach(statisticRowWrapper -> statisticRowWrapper.getDetails().setOnAction(event -> {
            final CustomDialog<StatisticEditController> dialog = new StatPriceChangeDialog((Stage) ((Button)event.getSource()).getScene().getWindow());
            dialog.show();
        }));

        return in;
    }

    /**
     * Converts a List of StatisticRow Objects into a List of StatisticRowWrapper Objects
     * @param in the list of Object which should be converted
     * @return the converted List
     */
    private static List<StatisticRowWrapper> convert(List<StatisticRow> in) {
        return in.stream().map(StatisticRowWrapper::new).collect(Collectors.toList());
    }

    /**
     * Shows the Dialog with a Warning
     */
    private static void showNoSelectionDialog() {
        final Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(GlobalSettings.APP_TITLE+": Warnung");
        a.setHeaderText("Keine Box ausgewählt");
        a.setContentText("Um den Preis von Boxen ändern zu können müssen Boxen ausgewählt werden!");
        a.showAndWait();
    }
}
