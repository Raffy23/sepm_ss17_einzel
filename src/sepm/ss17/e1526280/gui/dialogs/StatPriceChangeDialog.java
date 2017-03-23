package sepm.ss17.e1526280.gui.dialogs;

import javafx.stage.Stage;
import sepm.ss17.e1526280.gui.controller.StatisticEditController;
import sepm.ss17.e1526280.util.GlobalSettings;

/**
 * A Class which does represent the Dialog for the Pricechange Window in the
 * Statisticview
 *
 * @author Raphael Ludwig
 * @version 21.03.17
 */
public class StatPriceChangeDialog extends CustomDialog<StatisticEditController> {

    /** The path to the fxml file **/
    private static final String FXML = GlobalSettings.FXML_ROOT + "/statistic_changeprice.fxml";

    /** The Title of the Dialog **/
    private static final String TITLE = GlobalSettings.APP_TITLE + ": Statistik-Details";

    /**
     * Create a new StatPriceChangeDialog
     * @param owner the owner window of the Dialog
     */
    public StatPriceChangeDialog(Stage owner) {
        super(owner, TITLE, FXML);
    }
}
