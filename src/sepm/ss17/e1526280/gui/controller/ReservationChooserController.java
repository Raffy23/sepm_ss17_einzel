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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.Reservation;
import sepm.ss17.e1526280.gui.dialogs.CustomDialog;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.gui.dialogs.SearchDialog;
import sepm.ss17.e1526280.service.BoxDataService;
import sepm.ss17.e1526280.service.ReservationDataService;
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
 * @version 11.03.17
 */
public class ReservationChooserController {

    public static class BoxUIWrapper extends Box {
        final CheckBox checkBox = new CheckBox("");
        final TextField textField = new TextField();

        BoxUIWrapper(Box box) {
            super(box);

            textField.setDisable(true);
            checkBox.setOnAction(event -> textField.setDisable(!checkBox.isSelected()));

            textField.disabledProperty().addListener((observableValue, aBoolean, t1) -> {
                if( t1 ) {
                    textField.setText("");
                    textField.setPromptText("");
                } else {
                    textField.setPromptText("Geben Sie einen Namen ein");
                }
            });

            textField.setOnKeyTyped(keyEvent -> {
                if( textField.getStyleClass().contains("error") )
                    textField.getStyleClass().remove("error");
            });
        }

        //Used by JavaFX Trampoline!
        public CheckBox getCheckedBox() {
            return checkBox;
        }

        //Used by JavaFX Trampoline!
        public TextField getHorseName() {
            return textField;
        }
    }

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

    @FXML
    public void initialize() {
        boxPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        boxSizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        boxLitterCol.setCellValueFactory(new PropertyValueFactory<>("litter"));
        boxWindowCol.setCellValueFactory(new PropertyValueFactory<>("window"));
        boxIndoorCol.setCellValueFactory(new PropertyValueFactory<>("indoor"));
        boxImageCol.setCellValueFactory(new PropertyValueFactory<>("photo"));
        resTabCol.setCellValueFactory(new PropertyValueFactory<>("checkedBox"));
        horseTabCol.setCellValueFactory(new PropertyValueFactory<>("horseName"));

        resTabCol.setCellFactory((TableColumn<BoxUIWrapper, CheckBox> boxStringTableColumn) -> new TableCell<BoxUIWrapper, CheckBox>() {
            @Override
            protected void updateItem(CheckBox item, boolean empty) {
                super.updateItem(item, empty);

                if( !empty ) {
                    setGraphic(item);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(null);
                }
            }
        });

        horseTabCol.setCellFactory((TableColumn<BoxUIWrapper, TextField> boxStringTableColumn) -> new TableCell<BoxUIWrapper, TextField>() {
            @Override
            protected void updateItem(TextField item, boolean empty) {
                super.updateItem(item, empty);

                if( !empty ) {
                    setGraphic(item);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(null);
                }
            }


        });

        startDate.setValue(LocalDate.now());
        endDate.setValue(LocalDate.now());

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

        boxService.queryAll().thenAccept(this::setData).exceptionally(DialogUtil::onFatal);
    }

    public boolean validate() {
        if ( customerName.getText().length() <= 0 ) {
            System.err.println("Customername");
            return false;
        }

        if( startDate.getValue().isAfter(endDate.getValue()) ) {
            System.err.println("Start after End");
            return false;
        }

        int cnt = 0;
        for(BoxUIWrapper box:boxTable.getItems()) {
            if(box.checkBox.isSelected()&&box.textField.getText().length() > 0)
                cnt++;
        }

        System.err.println("Boxes: " + cnt);

        return cnt != 0;
    }

    public void setOkBtnEventHandler(EventHandler<ActionEvent> okBtnEventHandler) {
        continueBtn.setOnAction(okBtnEventHandler);
    }

    public List<Reservation> getReservations() {
        final List<Reservation> reservationList = new ArrayList<>();

        boxTable.getItems().forEach(boxUIWrapper -> {
            if( boxUIWrapper.checkBox.isSelected() ) {
                final Date startDate = Date.from(this.startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                final Date endDate = Date.from(this.endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

                reservationList.add(new Reservation(boxUIWrapper,startDate,endDate,customerName.getText(),boxUIWrapper.getHorseName().getText(),boxUIWrapper.getPrice()));
            }
        });


        return reservationList;
    }

    @FXML
    public void onSearchBox(ActionEvent event) {
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
        boxTable.getItems().clear();
        boxService.queryAll().thenAccept(this::setData).exceptionally(DialogUtil::onFatal);
    }

    @FXML
    public void onCancel(ActionEvent event) {
        ((Stage) ((Button)event.getSource()).getScene().getWindow()).close();
    }

    private void setData(List<Box> boxes) {
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
        final List<BoxUIWrapper> dataList = boxTable.getItems().stream().collect(Collectors.toList());
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
                                    .ifPresent(boxUIWrapper -> {
                                        boxUIWrapper.checkBox.setSelected(false);
                                        boxUIWrapper.textField.setDisable(true);
                                        boxUIWrapper.checkBox.setDisable(true);
                                    })
                        );
                    })
                    .exceptionally(DialogUtil::onError);
        });
    }

}
