package sepm.ss17.e1526280.gui.controller;

/**
 * Controller for the Edit Dialog 
 *
 * @author Raphael Ludwig
 * @version 20.03.17
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.gui.dialogs.ImageDialog;
import sepm.ss17.e1526280.service.BoxDataService;
import sepm.ss17.e1526280.util.DataServiceManager;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

public class StatisticEditController {

    /** Logger for logging **/
    private static final Logger LOG = LoggerFactory.getLogger(StatisticEditController.class);

    // FXML part
    @FXML private Label boxId;
    @FXML private Label boxSize;
    @FXML private Label boxPrice;
    @FXML private Label boxWindow;
    @FXML private Label boxIndoor;
    @FXML private TextField priceField;
    @FXML private RadioButton absPrice;
    @FXML private RadioButton perPrice;
    @FXML private Label boxCntLabel;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;
    @FXML private Button imgBtn;

    // Toggle group for the Radio buttons (absPrice and perPrice)
    private final ToggleGroup toggleGroup = new ToggleGroup();

    // Data which is injected by the init method
    private List<Box> data;
    private int curElement = 0;

    @FXML
    public void initialize() {
        LOG.debug("initialize");

        absPrice.setToggleGroup(toggleGroup);
        perPrice.setToggleGroup(toggleGroup);
        absPrice.setSelected(true);

        priceField.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
    }


    private void setElementData() {
        final Box e = data.get(curElement);
        LOG.trace("setElementData: " + e);

        boxId.setText(String.valueOf(e.getBoxID()));
        boxSize.setText(String.valueOf(e.getSize()));
        boxPrice.setText(String.valueOf(e.getPrice()));
        boxWindow.setText(String.valueOf(e.isWindow()));
        boxIndoor.setText(String.valueOf(e.isIndoor()));

        // Set the Image button state
        if( e.getPhoto() == null || e.getPhoto().length() > 5)  imgBtn.setDisable(true);
        else                                                    imgBtn.setDisable(false);
    }

    @FXML
    public void onBoxImage(ActionEvent event) {
        LOG.trace("onBoxImage Event");
        new ImageDialog(data.get(curElement), DataServiceManager.getService().getBoxDataService());
    }

    @FXML
    public void onNextBox(ActionEvent event) {
        LOG.trace("onNextBox Event");
        LOG.trace("Current Element = " + curElement);

        curElement++;
        setElementData();

        if( curElement >= data.size()-1 )
            ((Button)event.getSource()).setDisable(true);

        prevBtn.setDisable(false);
    }


    @FXML
    public void onPrevBox(ActionEvent event) {
        LOG.trace("onPrevBox Event");

        curElement--;
        setElementData();

        if( curElement <= 0 )
            ((Button)event.getSource()).setDisable(true);

        nextBtn.setDisable(false);
    }


    @FXML
    public void onSave(ActionEvent event) {
        LOG.trace("onSave Event");

        final BoxDataService service = DataServiceManager.getService().getBoxDataService();
        final double minPrice = data.stream().mapToDouble(Box::getPrice).min().orElse(0.0D);

        try {
            final float price = NumberFormat.getInstance().parse(priceField.getText()).floatValue();

            // Check data and Update stuff
            if( absPrice.isSelected() ) {
                if( price < 0 && -price > minPrice ) {
                    LOG.warn("Price is not valid, must be greater than " + minPrice);
                    showDialog("Der Preis muss höher als " + minPrice + " sein!");
                    return;
                }

                data.forEach(box -> box.setPrice(box.getPrice()+price));
            } else {
                if( price < -100 ) {
                    LOG.warn("Percent must be greater than -100%!");
                    showDialog("Prozentsatz muss größer als -100% sein!");
                    return;
                }

                data.forEach(box -> box.setPrice(box.getPrice()*(price/100)));
            }

            // Persist data
            data.forEach(box ->
                    service.update(box).thenRun(() -> {/* Log stuff */}).exceptionally(DialogUtil::onError)
            );

            ((Stage)((Button)event.getSource()).getScene().getWindow()).close();
        } catch (ParseException e) {
            DialogUtil.onError(e);
        }
    }

    /**
     * Sets the Data in the Controller
     * @param data the data which should be set
     */
    public void setData(List<Box> data) {
        LOG.trace("setData: " + data);

        this.data = data;

        boxCntLabel.setText(String.valueOf(data.size()));
        setElementData();

        if( data.size() == 1 )
            nextBtn.setDisable(true);
    }

    /**
     * Shows an Warning dialog with the reason given
     * @param reason the reason of the warning
     */
    private static void showDialog(String reason) {
        final Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText("Fehler bei der validierung");
        a.setTitle(GlobalSettings.APP_TITLE + ": Validierung");
        a.setContentText("Es ist ein Fehler bei der Validierung aufgetrenten:\n" + reason);
    }
}
