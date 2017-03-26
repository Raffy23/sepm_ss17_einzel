package sepm.ss17.e1526280.gui.controller.wrapper;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import sepm.ss17.e1526280.dto.Box;

/**
 * This class wraps a Box and does provide more functionality
 * for the UI
 *
 * @author Raphael Ludwig
 * @version 18.03.17
 */
public class BoxUIWrapper extends Box {
    public final CheckBox checkBox = new CheckBox("");
    public final TextField textField = new TextField();

    public BoxUIWrapper(Box box) {
        super(box);

        /* Init the Fields with some default stuff */
        textField.setDisable(true);
        checkBox.setOnAction(event -> textField.setDisable(!checkBox.isSelected()));

        // Add a value change listener to the text field
        textField.disabledProperty().addListener((observableValue, aBoolean, t1) -> {
            if( t1 ) {
                textField.setText("");
                textField.setPromptText("");
            } else {
                textField.setPromptText("Geben Sie einen Namen ein");
            }
        });

        // Some custom Css properties
        textField.setOnKeyTyped(keyEvent -> {
            if( textField.getStyleClass().contains("error") )
                textField.getStyleClass().remove("error");
        });
    }

    /**
     * @return the Checkbox-JavaFX Element for the Table (Checked if should be used)
     */
    public CheckBox getCheckedBox() {
        return checkBox;
    }

    /**
     * @return the TextField-JavaFX Element for the Table (Horsename)
     */
    public TextField getHorseName() {
        return textField;
    }
}
