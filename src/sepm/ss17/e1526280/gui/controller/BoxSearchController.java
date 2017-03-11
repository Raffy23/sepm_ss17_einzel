package sepm.ss17.e1526280.gui.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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

    @FXML
    public void initialize() {
        LOG.trace("initialize");

        litterComboBox.getItems().addAll(LitterType.Sawdust,LitterType.Straw);
        windowBox.getItems().addAll(TriStateBoolean.False,TriStateBoolean.True,TriStateBoolean.Ignore);
        indoorBox.getItems().addAll(TriStateBoolean.False,TriStateBoolean.True,TriStateBoolean.Ignore);
    }

    public Float getPrice() {
        try {
            return fmt.parse(priceTextField.getText()).floatValue();
        } catch (ParseException e) {
            return null;
        }
    }

    public Float getSize() {
        try {
            return fmt.parse(sizeTextField.getText()).floatValue();
        } catch (ParseException e) {
            return null;
        }
    }

    public LitterType getLitter() {
        return litterComboBox.getValue();
    }

    public Boolean hasWindow() {
        if( windowBox.getValue() == null || windowBox.getValue() == TriStateBoolean.Ignore )
            return null;

        return windowBox.getValue() == TriStateBoolean.True;
    }

    public Boolean isIndoor() {
        if( indoorBox.getValue() == null || indoorBox.getValue() == TriStateBoolean.Ignore )
            return null;

        return indoorBox.getValue() == TriStateBoolean.True;
    }

    public void setSearchActionListener(EventHandler<ActionEvent> handler) {
        searchBtn.setOnAction(handler);
    }

    @FXML
    public void onCancel(ActionEvent event) {
        final Button source = (Button) event.getSource();
        ((Stage)source.getScene().getWindow()).close();
    }

}
