package sepm.ss17.e1526280.gui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.gui.components.BoxDeleteCell;
import sepm.ss17.e1526280.gui.components.BoxImageCell;
import sepm.ss17.e1526280.gui.components.BoxViewEditCell;
import sepm.ss17.e1526280.gui.dialogs.BoxDetailDialog;
import sepm.ss17.e1526280.gui.dialogs.CustomDialog;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.gui.dialogs.SearchDialog;
import sepm.ss17.e1526280.service.DataService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Controller of the Box View,
 * this view is used in the Main window to list
 * the Boxes in the Table
 *
 * @author Raphael Ludwig
 * @version 11.03.17
 */
public class BoxTableViewController {

    /** Logger for logging ... duh **/
    private static final Logger LOG = LoggerFactory.getLogger(BoxTableViewController.class);

    //For Datasource etc ...
    private final DataService dataService = DataService.getService();

    //Data list for the Table View
    private final ObservableList<Box> boxObservableList;

    //Some FXML Stuff
    @FXML private TableView<Box> boxTable;
    @FXML private TableColumn<Box,Float> boxPriceCol;
    @FXML private TableColumn<Box,Float> boxSizeCol;
    @FXML private TableColumn<Box,String> boxLitterCol;
    @FXML private TableColumn<Box,Boolean> boxWindowCol;
    @FXML private TableColumn<Box,Boolean> boxIndoorCol;
    @FXML private TableColumn<Box,String> boxImageCol;
    @FXML private TableColumn<Box,Void> boxEditCol;
    @FXML private TableColumn<Box,Void> boxDeleteCol;

    public BoxTableViewController() {
        //Initialises the Data list for the Table
        boxObservableList = FXCollections.observableList(new ArrayList<>());
    }

    /**
     * FXML lazy initializer, is executed when the controller is loaded by the FXMLLoader
     */
    @FXML
    public void initialize() {
        LOG.trace("initialize");

        //Set the Factories for the Data extraction
        boxPriceCol.setCellValueFactory( new PropertyValueFactory<>("price"));
        boxSizeCol.setCellValueFactory(  new PropertyValueFactory<>("size"));
        boxLitterCol.setCellValueFactory(new PropertyValueFactory<>("litter"));
        boxWindowCol.setCellValueFactory(new PropertyValueFactory<>("window"));
        boxIndoorCol.setCellValueFactory(new PropertyValueFactory<>("indoor"));
        boxImageCol.setCellValueFactory( new PropertyValueFactory<>("photo"));
        boxEditCol.setCellValueFactory(  new PropertyValueFactory<>("DUMMY"));
        boxDeleteCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        //Now set custom Table cell renders:
        boxImageCol.setCellFactory((TableColumn<Box, String> boxStringTableColumn) -> new BoxImageCell(dataService));
        boxEditCol.setCellFactory((TableColumn<Box, Void> boxStringTableColumn) -> new BoxViewEditCell(dataService,boxTable));
        boxDeleteCol.setCellFactory((TableColumn<Box, Void> boxStringTableColumn) -> new BoxDeleteCell(dataService,boxObservableList));

        //Load all the Box Data into the UI:
        dataService.getAllBoxes().thenAcceptAsync(boxes -> {
            LOG.info("Data was loaded successfully");
            Platform.runLater(() -> boxObservableList.setAll(boxes));

            //Refresh the Box in the UI Thread after we are done ...
            //Platform.runLater(boxTable::refresh);
        }).exceptionally(DialogUtil::onError);

        //Set the Datasource for the BoxTable
        boxTable.setItems(boxObservableList);
        LOG.trace("Finished initialization");
    }


    /**
     * This function is executed after the Add new Box Button has been Clicked,
     * this will be done in a new Dialog
     *
     * @param event the JavaFX Action event of the Button
     */
    @FXML
    public void onNewBox(ActionEvent event) {
        LOG.info("onNewBox Event received");

        //Some variables to get the parent Window of the Event
        final Button source = (Button) event.getSource();
        final Stage parentStage = (Stage) source.getScene().getWindow();

        try {
            //Create new Dialog for the Box creation
            final CustomDialog<BoxCreationController> dialog = new BoxDetailDialog(parentStage,"Create a new Box");
            final BoxCreationController controller = dialog.getController();

            //register button handler to dialog
            controller.setOkBtnEventHandler(event1 -> {
                if( !controller.validateInput() )
                    return;

                //persist data from the dialog
                dataService.persist(controller.generateBox())
                        .thenAccept(this::addData)
                        .exceptionally(DialogUtil::onError);

                //close it
                final Button btn = (Button) event1.getSource();
                ((Stage)btn.getScene().getWindow()).close();
            });

            dialog.show();
        } catch (IOException e) {
            LOG.error("Unable to create BoxDetailDialog!");
            DialogUtil.onFatal(e);
        }

    }

    /**
     * This function is triggered by the Click of the Search Button in the UI
     * @param event FXML ActionEvent from the Search Button
     */
    @FXML
    public void onSearch(ActionEvent event) {
        LOG.debug("onSearch Event received");

        final Button source = (Button) event.getSource();
        final Stage parentStage = (Stage) source.getScene().getWindow();

        try {
            //Create new Dialog for the Box creation
            final CustomDialog<BoxSearchController> dialog = new SearchDialog(parentStage);
            final BoxSearchController controller = dialog.getController();

            //register button handler to dialog
            controller.setSearchActionListener(event1 -> {
                //Clear the List
                boxObservableList.clear();

                //Search for the Data asynchronously
                dataService.searchForBoxes( controller.getPrice()
                                          , controller.getSize()
                                          , controller.getLitter()
                                          , controller.hasWindow()
                                          , controller.isIndoor())
                        .thenAccept(this::setData)
                        .exceptionally(DialogUtil::onError);

                final Button btn = (Button) event1.getSource();
                ((Stage)btn.getScene().getWindow()).close();
            });

            dialog.show();
        } catch (IOException e) {
            System.err.println("Fatal: Unable to create BoxDetailDialog!");
            DialogUtil.onFatal(e);
        }

    }

    /**
     * This function is executed after the Search all Button is Clicked
     * @param event the JavaFX Action event of the Button
     */
    @FXML
    public void onQueryAll(ActionEvent event) {
        LOG.debug("onQueryAll Event received");

        boxObservableList.clear();
        dataService.getAllBoxes()
                   .thenAccept(this::setData)
                   .exceptionally(DialogUtil::onError);
    }

    /**
     * Utility function which adds one Box the the Data
     * This function is Thread-safe and the actual add is executed in the JavaFX Thread
     * @param data Box which should be added to the List
     */
    private void addData(Box data) {
        Platform.runLater(() -> boxObservableList.add(data));
    }

    /**
     * Utility function which adds all Boxes the the Data
     * This function is Thread-safe and the actual add is executed in the JavaFX Thread
     * @param boxList List of Boxes which should be added to the List
     */
    private void setData(List<Box> boxList) {
        Platform.runLater(() -> boxObservableList.addAll(boxList));
    }
}
