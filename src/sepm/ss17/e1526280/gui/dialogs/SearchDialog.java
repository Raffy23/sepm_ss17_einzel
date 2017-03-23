package sepm.ss17.e1526280.gui.dialogs;

import javafx.stage.Stage;
import sepm.ss17.e1526280.gui.controller.BoxSearchController;
import sepm.ss17.e1526280.util.GlobalSettings;

/**
 * A Simple search Dialog for Boxes
 *
 * @author Raphael Ludwig
 * @version 11.03.17
 */
public class SearchDialog extends CustomDialog<BoxSearchController> {

    /** The path to the fxml file **/
    private static final String FXML_PATH = GlobalSettings.FXML_ROOT+"box_detail_searchview.fxml";

    /** The Title of the Dialog **/
    private static final String TITLE = GlobalSettings.APP_TITLE + ": Suche nach Boxen";

    public SearchDialog(Stage owner) {
        super(owner,TITLE,FXML_PATH);
    }
}
