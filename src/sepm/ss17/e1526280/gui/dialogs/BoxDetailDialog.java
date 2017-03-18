package sepm.ss17.e1526280.gui.dialogs;

import javafx.stage.Stage;
import sepm.ss17.e1526280.gui.controller.BoxCreationController;
import sepm.ss17.e1526280.util.GlobalSettings;

/**
 * A Dialog for the Box Detail view, in this View the details of the
 * Box can be changed.
 *
 * @author Raphael Ludwig
 * @version 12.03.17
 */
public class BoxDetailDialog extends CustomDialog<BoxCreationController> {

    private static final String FXML_PATH = GlobalSettings.FXML_ROOT+"box_detail_view.fxml";
    private static final String FXML_ERROR_PATH = GlobalSettings.FXML_ROOT+"custom_textinput_error.css";

    public BoxDetailDialog(Stage owner, String title) {
        super(owner,title,FXML_PATH);
        scene.getStylesheets().add(getClass().getResource(FXML_ERROR_PATH).toString());
    }

}
