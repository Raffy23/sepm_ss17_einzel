package sepm.ss17.e1526280.gui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dto.StatisticRow;
import sepm.ss17.e1526280.gui.components.StatChartCell;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.service.BoxDataService;
import sepm.ss17.e1526280.service.StatisticalService;
import sepm.ss17.e1526280.util.DataServiceManager;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 16.03.17
 */

public class StatisticController {

    /** Logger for logging **/
    private static final Logger LOG = LoggerFactory.getLogger(StatisticController.class);

    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private TableView<StatisticRow> tableView;
    @FXML private TableColumn<StatisticRow, Integer> boxCol;
    @FXML private TableColumn<StatisticRow, Integer> resCountCol;
    @FXML private TableColumn<StatisticRow, Integer> dayMonCol;
    @FXML private TableColumn<StatisticRow, Integer> dayDiCol;
    @FXML private TableColumn<StatisticRow, Integer> dayMiCol;
    @FXML private TableColumn<StatisticRow, Integer> dayDoCol;
    @FXML private TableColumn<StatisticRow, Integer> dayFrCol;
    @FXML private TableColumn<StatisticRow, Integer> daySaCol;
    @FXML private TableColumn<StatisticRow, Integer> daySoCol;
    @FXML private TableColumn<StatisticRow, Void> graphCol;
    @FXML private TableColumn<StatisticRow, Void> checkCol;
    @FXML private TableColumn<StatisticRow, Void> detailsCol;

    private final StatisticalService service = DataServiceManager.getService().getStatisticalService();
    private final BoxDataService boxService = DataServiceManager.getService().getBoxDataService();
    private final ObservableList<StatisticRow> dataList = FXCollections.observableList(new ArrayList<>());

    /**
     * Initializes all the Data which is needed by the Controller
     */
    @FXML
    public void initialize() {
        boxCol.setCellValueFactory(      new PropertyValueFactory<>("boxID"));
        resCountCol.setCellValueFactory( new PropertyValueFactory<>("count"));
        dayMonCol.setCellValueFactory(   new PropertyValueFactory<>("monday"));
        dayDiCol.setCellValueFactory(    new PropertyValueFactory<>("tuesday"));
        dayMiCol.setCellValueFactory(    new PropertyValueFactory<>("wednesday"));
        dayDoCol.setCellValueFactory(    new PropertyValueFactory<>("thursday"));
        dayFrCol.setCellValueFactory(    new PropertyValueFactory<>("friday"));
        daySaCol.setCellValueFactory(    new PropertyValueFactory<>("saturday"));
        daySoCol.setCellValueFactory(    new PropertyValueFactory<>("sunday"));
        graphCol.setCellValueFactory(    new PropertyValueFactory<>("DUMMY"));
        checkCol.setCellValueFactory(    new PropertyValueFactory<>("DUMMY"));
        detailsCol.setCellValueFactory(  new PropertyValueFactory<>("DUMMY"));

        graphCol.setCellFactory((TableColumn<StatisticRow, Void> boxStringTableColumn) -> new StatChartCell());

        startDate.setValue(LocalDate.now());
        endDate.setValue(LocalDate.now());

        loadData();
        tableView.setItems(dataList);
        tableView.getStylesheets().add(GlobalSettings.FXML_ROOT+"/custom_table.css");
    }

    @FXML
    public void onChangePrice(ActionEvent event) {

    }

    @FXML
    public void onGlobalStat(ActionEvent event) {

    }

    @FXML
    public void onReload(ActionEvent event) {
        loadData();
    }


    private void loadData() {
        if( !Platform.isFxApplicationThread() )
            Platform.runLater(dataList::clear);
        else
            dataList.clear();

        boxService.queryAll()
                .thenAccept(boxes -> {
                    final List<StatisticRow> data = service.query(boxes
                            , Date.from(this.startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                            , Date.from(this.endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                            .join(); //We want the Data in this Thread, no need to query the Data in another Future

                    Platform.runLater(() -> dataList.addAll(data));
                }).exceptionally(DialogUtil::onError);
    }


}
