package sepm.ss17.e1526280.gui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.Reservation;
import sepm.ss17.e1526280.gui.components.TableComponentCell;
import sepm.ss17.e1526280.gui.controller.wrapper.BoxUIWrapper;
import sepm.ss17.e1526280.gui.dialogs.CustomDialog;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.gui.dialogs.SearchDialog;
import sepm.ss17.e1526280.service.BoxDataService;
import sepm.ss17.e1526280.service.ReservationDataService;
import sepm.ss17.e1526280.util.DataServiceManager;
import sepm.ss17.e1526280.util.DateUtil;

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
 * @version 11.03.17
 */
public class ReservationChooserController {

    /** Logger for logging **/
    private static final Logger LOG = LoggerFactory.getLogger(ReservationChooserController.class);

    @FXML private TableView<BoxUIWrapper> boxTable;
    @FXML private TableColumn<BoxUIWrapper,Float> boxPriceCol;
    @FXML private TableColumn<BoxUIWrapper,Float> boxSizeCol;
    @FXML private TableColumn<BoxUIWrapper,String> boxLitterCol;
    @FXML private TableColumn<BoxUIWrapper,Boolean> boxWindowCol;
    @FXML private TableColumn<BoxUIWrapper,Boolean> boxIndoorCol;
    @FXML private TableColumn<BoxUIWrapper,String> boxImageCol;
    @FXML private TableColumn<BoxUIWrapper,CheckBox> resTabCol;
    @FXML private TableColumn<BoxUIWrapper,TextField> horseTabCol;
    @FXML private Button continueBtn;
    @FXML private TextField customerName;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;

    private final BoxDataService boxService = DataServiceManager.getService().getBoxDataService();
    private final ReservationDataService reservationDataService = DataServiceManager.getService().getReservationDataService();

    /**
     * Initializes all the Data which is needed by the Controller
     */
    @FXML
    public void initialize() {
        LOG.trace("initialize");

        boxPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        boxSizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        boxLitterCol.setCellValueFactory(new PropertyValueFactory<>("litter"));
        boxWindowCol.setCellValueFactory(new PropertyValueFactory<>("window"));
        boxIndoorCol.setCellValueFactory(new PropertyValueFactory<>("indoor"));
        boxImageCol.setCellValueFactory(new PropertyValueFactory<>("photo"));
        resTabCol.setCellValueFactory(new PropertyValueFactory<>("checkedBox"));
        horseTabCol.setCellValueFactory(new PropertyValueFactory<>("horseName"));

        // Set some cell Factories
        resTabCol.setCellFactory((TableColumn<BoxUIWrapper, CheckBox> boxStringTableColumn) -> new TableComponentCell<>());
        horseTabCol.setCellFactory((TableColumn<BoxUIWrapper, TextField> boxStringTableColumn) -> new TableComponentCell<>());

        // Init Dates
        startDate.setValue(LocalDate.now());
        endDate.setValue(LocalDate.now());

        // Make it so that dates can only be some logical dates
        startDate.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if( newValue.isAfter(endDate.getValue()) || newValue.isBefore(LocalDate.now()) )
                startDate.setValue(oldValue);
            else
                updateData();
        });

        endDate.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if( newValue.isBefore(startDate.getValue()) )
                endDate.setValue(oldValue);
            else
                updateData();
        });

        // Query everything
        boxService.queryAll().thenAccept(this::setData).exceptionally(DialogUtil::onFatal);
    }

    /**
     * @return true if the data is valid otherwise false
     */
    public boolean validate() {
        LOG.trace("validate form input");

        if ( customerName.getText().length() <= 0 || customerName.getText().length() > 127) {
            LOG.warn("Name of the Customer is out of Bound!");
            return false;
        }

        if( startDate.getValue().isAfter(endDate.getValue()) ) {
            LOG.warn("Start Date if after End Date!");
            return false;
        }

        int cnt = 0;
        for(BoxUIWrapper box:boxTable.getItems()) {
            if(box.checkBox.isSelected()&& !box.textField.getText().isEmpty())
                cnt++;
        }

        LOG.trace("There are currently " + cnt + " Boxes checked");
        return cnt != 0;
    }

    public void setOkBtnEventHandler(EventHandler<ActionEvent> okBtnEventHandler) {
        continueBtn.setOnAction(okBtnEventHandler);
    }

    /**
     * @return all reservation objects generated from the dialog
     */
    public List<Reservation> getReservations() {
        LOG.trace("getReservations");
        final List<Reservation> reservationList = new ArrayList<>();

        boxTable.getItems().forEach(boxUIWrapper -> {
            if( boxUIWrapper.checkBox.isSelected() ) {
                reservationList.add(buildReservation(boxUIWrapper));
            }
        });

        return reservationList;
    }

    /**
     * Generates a Reservation from a BoxUiWrapper Object
     * @param boxUIWrapper ui object which should be converted
     * @return a Reservation from the BoxUIWrapper
     */
    @NotNull
    private Reservation buildReservation(BoxUIWrapper boxUIWrapper) {
        final Date startDate = DateUtil.fromLocalDate(this.startDate.getValue());
        final Date endDate = DateUtil.fromLocalDate(this.endDate.getValue());

        return new Reservation(boxUIWrapper,startDate,endDate,customerName.getText(),boxUIWrapper.getHorseName().getText(),boxUIWrapper.getPrice());
    }

    @FXML
    public void onSearchBox(ActionEvent event) {
        LOG.trace("onSearchBox Event");
        final Button source = (Button) event.getSource();
        final Stage parentStage = (Stage) source.getScene().getWindow();

        final CustomDialog<BoxSearchController> dialog = new SearchDialog(parentStage);
        final BoxSearchController controller = dialog.getController();

        boxTable.getItems().clear();

        //register button handler to dialog
        controller.setSearchActionListener(event1 -> {

            //Search for the Data asynchronously
            boxService.search(controller.getPrice()
                            , controller.getSize()
                            , controller.getLitter()
                            , controller.hasWindow()
                            , controller.isIndoor())
                    .thenAccept(this::setData)
                    .exceptionally(DialogUtil::onError);

            final Button btn = (Button) event1.getSource();
            ((Stage) btn.getScene().getWindow()).close();

        });

        dialog.show();
    }

    @FXML
    public void onViewAll(ActionEvent event) {
        LOG.trace("onViewAll Event");
        boxTable.getItems().clear();
        boxService.queryAll().thenAccept(this::setData).exceptionally(DialogUtil::onFatal);
    }

    @FXML
    @SuppressWarnings("MethodMayBeStatic") // If static -> FXML can not bind to it
    public void onCancel(ActionEvent event) {
        LOG.trace("onCancel Event");
        ((Stage) ((Button)event.getSource()).getScene().getWindow()).close();
    }

    private void setData(List<Box> boxes) {
        LOG.trace("setData");
        final List<BoxUIWrapper> dataList = boxes.stream().map(BoxUIWrapper::new).collect(Collectors.toList());

        Platform.runLater(() -> {
            final Date startDate = Date.from(this.startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            final Date endDate = Date.from(this.endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

            reservationDataService.queryBlocked(boxes,startDate,endDate)
                                  .thenAccept(boxes1 -> {
                                      boxes1.forEach(box ->
                                          dataList.stream()
                                                  .filter(boxUIWrapper -> boxUIWrapper.getBoxID() == box.getBoxID())
                                                  .findFirst()
                                                  .ifPresent(boxUIWrapper -> boxUIWrapper.checkBox.setDisable(true))
                                      );

                                      final ObservableList<BoxUIWrapper> tableContent = FXCollections.observableArrayList(dataList);
                                      Platform.runLater(() -> boxTable.setItems(tableContent));
                                  }).exceptionally(DialogUtil::onError);

        });
    }

    private void updateData() {
        LOG.trace("updateData");
        final List<BoxUIWrapper> dataList = new ArrayList<>(boxTable.getItems());
        final List<Box> boxList = new ArrayList<>(dataList);

        Platform.runLater(() -> {
            final Date startDate = Date.from(this.startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            final Date endDate = Date.from(this.endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

            reservationDataService.queryBlocked(boxList,startDate,endDate)
                    .thenAccept(boxes1 -> {
                        dataList.forEach(boxUIWrapper -> boxUIWrapper.checkBox.setDisable(false));

                        boxes1.forEach(box ->
                            dataList.stream()
                                    .filter(boxUIWrapper -> boxUIWrapper.getBoxID() == box.getBoxID())
                                    .findFirst()
                                    .ifPresent(boxUIWrapper ->
                                        Platform.runLater( () -> {
                                            boxUIWrapper.checkBox.setSelected(false);
                                            boxUIWrapper.textField.setDisable(true);
                                            boxUIWrapper.checkBox.setDisable(true);
                                        })
                                    )
                        );
                    })
                    .exceptionally(DialogUtil::onError);
        });
    }

}
