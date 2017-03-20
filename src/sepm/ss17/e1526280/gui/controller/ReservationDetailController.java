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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationEntryWrapper;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationWrapper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This Controller does manage the Ui for the Reservation Details.
 * It must be lazy initialized with the init method otherwise it has
 * no Data to show
 *
 * @author Raphael Ludwig
 * @version 18.03.17
 */
public class ReservationDetailController {

    /** Logger for logging **/
    private static final Logger LOG = LoggerFactory.getLogger(ReservationDetailController.class);

    @FXML private Label customerName;
    @FXML private TableView<ReservationEntryWrapper> resTable;
    @FXML private TableColumn<ReservationEntryWrapper,Integer> boxCol;
    @FXML private TableColumn<ReservationEntryWrapper,String> horseCol;
    @FXML private TableColumn<ReservationEntryWrapper,Float> priceCol;
    @FXML private TableColumn<ReservationEntryWrapper,Float> sumPriceCol;
    @FXML private Label priceLabel;
    @FXML private Label resIDLabel;

    /**
     * Initializes all the Data which is needed by the Controller
     */
    @FXML
    public void initialize() {
        LOG.trace("initialize");

        boxCol.setCellValueFactory(new PropertyValueFactory<>("boxId"));
        horseCol.setCellValueFactory(new PropertyValueFactory<>("horse"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        sumPriceCol.setCellValueFactory(new PropertyValueFactory<>("priceSum"));
    }

    /**
     * Lazy initializes the Dialog Data
     * @param wrapper data with which the dialog should be initialized
     */
    public void init(ReservationWrapper wrapper) {
        LOG.debug("Init with data: " + wrapper);
        final List<ReservationEntryWrapper> convData = new ArrayList<>();
        wrapper.getBoxes().forEach(reservation -> convData.add(new ReservationEntryWrapper(reservation,wrapper.getDays())));

        resIDLabel.setText(String.valueOf(wrapper.getID()));
        customerName.setText(wrapper.getName());

        priceLabel.setText(NumberFormat.getInstance().format(wrapper.getPrice()));
        resTable.setItems(FXCollections.observableArrayList(convData));
    }


    @FXML
    @SuppressWarnings("MethodMayBeStatic") // If static -> FXML can not bind to it
    public void onClose(ActionEvent event) {
        LOG.trace("Close");
        ((Stage) ((Button)event.getSource()).getScene().getWindow()).close();
    }

}
