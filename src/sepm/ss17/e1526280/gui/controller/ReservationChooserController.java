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
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.service.DataService;

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

    private final DataService dataService = DataService.getService();

    @FXML
    public void initialize() {
        boxPriceCol.setCellValueFactory(new PropertyValueFactory<BoxUIWrapper, Float>("price"));
        boxSizeCol.setCellValueFactory(new PropertyValueFactory<BoxUIWrapper, Float>("size"));
        boxLitterCol.setCellValueFactory(new PropertyValueFactory<BoxUIWrapper, String>("litter"));
        boxWindowCol.setCellValueFactory(new PropertyValueFactory<BoxUIWrapper, Boolean>("window"));
        boxIndoorCol.setCellValueFactory(new PropertyValueFactory<BoxUIWrapper, Boolean>("indoor"));
        boxImageCol.setCellValueFactory(new PropertyValueFactory<BoxUIWrapper, String>("photo"));
        resTabCol.setCellValueFactory(new PropertyValueFactory<BoxUIWrapper, CheckBox>("checkedBox"));
        horseTabCol.setCellValueFactory(new PropertyValueFactory<BoxUIWrapper, TextField>("horseName"));

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

        dataService.getAllBoxes().thenAccept(boxes -> {
            final List<BoxUIWrapper> dataList = boxes.stream().map(BoxUIWrapper::new).collect(Collectors.toList());
            final ObservableList<BoxUIWrapper> tableContent = FXCollections.observableArrayList(dataList);
            Platform.runLater(() -> boxTable.setItems(tableContent));
        }).exceptionally(DialogUtil::onFatal);
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

        if(cnt == 0)
            return false;

        return true;
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
    public void onCancel(ActionEvent event) {
        ((Stage) ((Button)event.getSource()).getScene().getWindow()).close();
    }

}
