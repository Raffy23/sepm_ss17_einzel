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
