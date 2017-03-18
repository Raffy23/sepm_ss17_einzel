package sepm.ss17.e1526280.gui.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.LitterType;
import sepm.ss17.e1526280.util.DataServiceManager;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Controller responsible for the Box Creation / Editing Dialog
 *
 * @author Raphael Ludwig
 * @version 11.03.17
 */
public class BoxCreationController {

    /** Logger for logging **/
    private static final Logger LOG = LoggerFactory.getLogger(BoxCreationController.class);

    /** This Number Format Instance is used to validate the TextInput Fields **/
    private final NumberFormat fmt = NumberFormat.getNumberInstance();

    //FXML stuff
    @FXML private TextField priceTextField;
    @FXML private TextField sizeTextField;
    @FXML private ComboBox<LitterType> litterComboBox;
    @FXML private CheckBox windowChecker;
    @FXML private CheckBox indoorChecker;
    @FXML private TextField imagePathTextField;
    @FXML private Button okBtn;

    //File Data for the Photo
    private File photo;

    /**
     * Initializes all the Data which is needed by the Controller
     */
    @FXML
    public void initialize() {
        LOG.trace("initialize");

        //Only allow numbers
        priceTextField.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        sizeTextField.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));

        //Set Data
        litterComboBox.getItems().addAll(LitterType.Sawdust,LitterType.Straw);

        LOG.trace("Finished initialization");
    }

    @FXML
    public void openFileChooser(ActionEvent event) {
        LOG.trace("open FileChooser Window");

        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images","*.png","*.jpg","*.jpeg"));

        photo = fileChooser.showOpenDialog(stage);
        if( photo != null ) {
            imagePathTextField.setText(photo.getAbsolutePath());
        }

        //validateInput();
    }


    @FXML
    @SuppressWarnings("MethodMayBeStatic") // If static -> FXML can not bind to it
    public void onCancle(ActionEvent event) {
        LOG.debug("on Cancel Event received");

        final Button source = (Button) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();

        stage.close();
    }

    private boolean validateTextField(TextField tf) {
        LOG.trace("Validate Text input for " + tf);

        if( tf.getText().length() == 0 || !canParseNumber(tf) || parseField(tf) <= 0 ) {
            if( !tf.getStyleClass().contains("error") )
                tf.getStyleClass().add("error");

            return false;
        } else {
            tf.getStyleClass().remove("error");
            return true;
        }

    }

    public boolean validateInput() {
        boolean success;

        success = validateTextField(priceTextField);
        success = validateTextField(sizeTextField) && success;

        if( litterComboBox.getValue() == null ) {

            if( !litterComboBox.getStyleClass().contains("error") )
                litterComboBox.getStyleClass().add("error");

            success = false;
        } else {
            litterComboBox.getStyleClass().remove("error");
        }

        LOG.trace("Input Validation State: " + success);
        return success;
    }

    private boolean canParseNumber(TextField textField) {
        try {
            fmt.parse(textField.getText());
        } catch (ParseException e) {
           return false;
        }

        return true;
    }

    private double parseField(TextField textField) {
        try {
            return fmt.parse(textField.getText()).doubleValue();
        } catch (ParseException e) {
            return Double.NaN;
        }
    }

    public float getPrice() {
       return (float) parseField(priceTextField);
    }

    public float getSize() {
        return (float) parseField(sizeTextField);
    }

    public boolean hasWindow() {
        return windowChecker.isSelected();
    }

    public boolean isIndoor() {
        return indoorChecker.isSelected();
    }

    /**
     * Generates a new Box from the Content of the Dialog
     * @return the Box with the Data in the Dialog
     */
    public Box generateBox() {
        return new Box(getPrice(),getSize(),litterComboBox.getValue(),hasWindow(),isIndoor(), photo!=null?photo.getAbsolutePath():null);
    }

    public void setOkBtnEventHandler(EventHandler<ActionEvent> okBtnEventHandler) {
        okBtn.setOnAction(okBtnEventHandler);
    }

    /**
     * Initializes the Controller with the Box, all Elements do get set by the values of the Box
     * @param box which should be displayed in the Controller
     */
    public void init(Box box) {
        LOG.debug("init: " + box);

        priceTextField.setText(fmt.format(box.getPrice()));
        sizeTextField.setText(fmt.format(box.getSize()));
        litterComboBox.setValue(box.getLitter());
        windowChecker.setSelected(box.isWindow());
        indoorChecker.setSelected(box.isIndoor());

        if( box.getPhoto() != null ) {
            photo = DataServiceManager.getService().getBoxDataService().resolveImage(box);
            imagePathTextField.setText(photo.getAbsolutePath());
        } else {
            photo = null;
            imagePathTextField.setText("");
        }
    }

    /**
     * Clears all the Data in the Dialog
     */
    public void clear() {
        LOG.trace("clear dialog");

        priceTextField.setText("");
        sizeTextField.setText("");
        litterComboBox.setValue(null);
        windowChecker.setSelected(false);
        indoorChecker.setSelected(false);
    }
}
