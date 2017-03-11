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
import sepm.ss17.e1526280.dto.Reservation;

import java.util.List;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 11.03.17
 */
public class ReservationDetailController {

    @FXML private Label customerName;
    @FXML private TableView<Reservation> resTable;
    @FXML private TableColumn<Reservation,Integer> boxCol;
    @FXML private TableColumn<Reservation,String> horseCol;
    @FXML private TableColumn<Reservation,Float> priceCol;

    @FXML
    public void initialize() {
        boxCol.setCellValueFactory(new PropertyValueFactory<>("boxId"));
        horseCol.setCellValueFactory(new PropertyValueFactory<>("horse"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    public void init(List<Reservation> reservation) {
        customerName.setText(reservation.get(0).getCustomer());
        resTable.setItems(FXCollections.observableArrayList(reservation));
    }

    @FXML
    public void onClose(ActionEvent event) {
        ((Stage) ((Button)event.getSource()).getScene().getWindow()).close();
    }

}
