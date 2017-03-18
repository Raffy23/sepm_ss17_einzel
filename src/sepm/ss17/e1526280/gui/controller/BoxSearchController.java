package sepm.ss17.e1526280.gui.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dto.LitterType;
import sepm.ss17.e1526280.dto.TriStateBoolean;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Controller which is responsible for the Search Dialog
 *
 * @author Raphael Ludwig
 * @version 11.03.17
 */
public class BoxSearchController {

    /** Logger for logging ... duh **/
    private static final Logger LOG = LoggerFactory.getLogger(BoxSearchController.class);

    private final NumberFormat fmt = NumberFormat.getNumberInstance();

    @FXML private TextField priceTextField;
    @FXML private TextField sizeTextField;
    @FXML private ComboBox<LitterType> litterComboBox;
    @FXML private ComboBox<TriStateBoolean> windowBox;
    @FXML private ComboBox<TriStateBoolean> indoorBox;
    @FXML private Button searchBtn;

    /**
     * Initializes all the Data which is needed by the Controller
     */
    @FXML
    public void initialize() {
        LOG.trace("initialize");

        litterComboBox.getItems().addAll(LitterType.Sawdust,LitterType.Straw);
        windowBox.getItems().addAll(TriStateBoolean.False,TriStateBoolean.True,TriStateBoolean.Ignore);
        indoorBox.getItems().addAll(TriStateBoolean.False,TriStateBoolean.True,TriStateBoolean.Ignore);
    }

    /**
     * @return the parsed price, might be null if value couldn't be parsed
     */
    public @Nullable Float getPrice() {
        try {
            return fmt.parse(priceTextField.getText()).floatValue();
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * @return the parsed size value, might be null if value couldn't be parsed
     */
    public @Nullable Float getSize() {
        try {
            return fmt.parse(sizeTextField.getText()).floatValue();
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * @return the Litter type, might be null if user didn't choose one
     */
    public @Nullable LitterType getLitter() {
        return litterComboBox.getValue();
    }

    /**
     * @return true if the user has selected the window property otherwise false. If the user did
     *          not select anything null is returned
     */
    public @Nullable Boolean hasWindow() {
        if( windowBox.getValue() == null || windowBox.getValue() == TriStateBoolean.Ignore )
            return null;

        return windowBox.getValue() == TriStateBoolean.True;
    }

    /**
     * @return true if the user has selected the indoor property otherwise false. If the user did
     *          not select anything null is returned
     */
    public @Nullable Boolean isIndoor() {
        if( indoorBox.getValue() == null || indoorBox.getValue() == TriStateBoolean.Ignore )
            return null;

        return indoorBox.getValue() == TriStateBoolean.True;
    }

    /**
     * Sets the Search Button action Listener
     * @param handler listener which should listen to button changes
     */
    public void setSearchActionListener(EventHandler<ActionEvent> handler) {
        searchBtn.setOnAction(handler);
    }

    @FXML
    @SuppressWarnings("MethodMayBeStatic") // If static -> FXML can not bind to it
    public void onCancel(ActionEvent event) {
        final Button source = (Button) event.getSource();
        ((Stage)source.getScene().getWindow()).close();
    }

}
