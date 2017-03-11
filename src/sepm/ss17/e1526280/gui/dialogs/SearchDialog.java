package sepm.ss17.e1526280.gui.dialogs;

import javafx.stage.Stage;
import sepm.ss17.e1526280.gui.controller.BoxSearchController;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.io.IOException;

/**
 * A Simple search Dialog for Boxes
 *
 * @author Raphael Ludwig
 * @version 11.03.17
 */
public class SearchDialog extends CustomDialog<BoxSearchController> {
    private static final String FXML_PATH = GlobalSettings.FXML_ROOT+"box_detail_searchview.fxml";

    public SearchDialog(Stage owner) throws IOException {
        super(owner,"Search for Boxes",FXML_PATH);
    }
}
