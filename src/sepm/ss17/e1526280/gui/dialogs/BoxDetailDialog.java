package sepm.ss17.e1526280.gui.dialogs;

import javafx.stage.Stage;
import sepm.ss17.e1526280.gui.controller.BoxCreationController;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.io.IOException;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 05.03.17
 */
public class BoxDetailDialog extends CustomDialog<BoxCreationController> {

    private static final String FXML_PATH = GlobalSettings.FXML_ROOT+"box_detail_view.fxml";
    private static final String FXML_ERROR_PATH = GlobalSettings.FXML_ROOT+"custom_textinput_error.css";

    public BoxDetailDialog(Stage owner, String title) throws IOException {
        super(owner,title,FXML_PATH);
        scene.getStylesheets().add(getClass().getResource(FXML_ERROR_PATH).toString());
    }

}
