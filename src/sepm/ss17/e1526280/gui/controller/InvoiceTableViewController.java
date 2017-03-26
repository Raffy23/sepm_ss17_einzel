package sepm.ss17.e1526280.gui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.gui.components.ReservationDetailCell;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationWrapper;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.service.ReservationDataService;
import sepm.ss17.e1526280.util.DataServiceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 13.03.17
 */
public class InvoiceTableViewController {

    /** Logger for logging **/
    private static final Logger LOG = LoggerFactory.getLogger(InvoiceTableViewController.class);

    private final ReservationDataService dataService = DataServiceManager.getService().getReservationDataService();
    private final ObservableList<ReservationWrapper> dataList = FXCollections.observableList(new ArrayList<>());

    @FXML private TableView<ReservationWrapper> tableView;
    @FXML private TableColumn<ReservationWrapper,Void> detailCol;
    @FXML private TableColumn<ReservationWrapper,String> customerCol;
    @FXML private TableColumn<ReservationWrapper,Integer> boxCountCol;
    @FXML private TableColumn<ReservationWrapper,Float> priceCol;
    @FXML private TableColumn<ReservationWrapper,String> startCol;
    @FXML private TableColumn<ReservationWrapper,String> endCol;

    /**
     * Initializes all the Data which is needed by the Controller
     */
    @FXML
    public void initialize() {
        LOG.trace("initialize");

        customerCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        boxCountCol.setCellValueFactory(new PropertyValueFactory<>("count"));
        detailCol.setCellFactory((TableColumn<ReservationWrapper, Void> boxStringTableColumn) -> new ReservationDetailCell());
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startString"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endString"));

        this.loadData();
        tableView.setItems(dataList);
    }

    /**
     * Loads all the invoices from the data service
     */
    public void loadData() {
        LOG.trace("loadData");
        dataService.queryGrouped(true)
                .thenApply(lists -> {
                    final List<ReservationWrapper> data = lists.stream().map(ReservationWrapper::new).collect(Collectors.toList());

                    Platform.runLater(() -> dataList.setAll(data));
                    return null;
                })
                .exceptionally(DialogUtil::onFatal);
    }

    @FXML
    public void onRefreshAction(ActionEvent event) {
        LOG.trace("onRefreshAction");
        this.loadData();
    }
}
