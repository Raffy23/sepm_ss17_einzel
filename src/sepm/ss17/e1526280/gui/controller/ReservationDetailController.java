package sepm.ss17.e1526280.gui.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationEntryWrapper;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationWrapper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 11.03.17
 */
public class ReservationDetailController {

    @FXML private Label customerName;
    @FXML private TableView<ReservationEntryWrapper> resTable;
    @FXML private TableColumn<ReservationEntryWrapper,Integer> boxCol;
    @FXML private TableColumn<ReservationEntryWrapper,String> horseCol;
    @FXML private TableColumn<ReservationEntryWrapper,Float> priceCol;
    @FXML private TableColumn<ReservationEntryWrapper,Float> sumPriceCol;
    @FXML private Label priceLabel;

    @FXML
    public void initialize() {
        boxCol.setCellValueFactory(new PropertyValueFactory<>("boxId"));
        horseCol.setCellValueFactory(new PropertyValueFactory<>("horse"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        sumPriceCol.setCellValueFactory(new PropertyValueFactory<>("priceSum"));
    }

    public void init(ReservationWrapper wrapper) {
        final List<ReservationEntryWrapper> convData = new ArrayList<>();
        wrapper.getBoxes().forEach(reservation -> convData.add(new ReservationEntryWrapper(reservation,wrapper.getDays())));

        customerName.setText(wrapper.getName());
        priceLabel.setText(NumberFormat.getInstance().format(wrapper.getPrice()));
        resTable.setItems(FXCollections.observableArrayList(convData));
    }

    @FXML
    public void onClose(ActionEvent event) {
        ((Stage) ((Button)event.getSource()).getScene().getWindow()).close();
    }

}
