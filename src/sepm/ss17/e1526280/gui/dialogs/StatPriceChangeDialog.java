package sepm.ss17.e1526280.gui.dialogs;

import javafx.stage.Stage;
import sepm.ss17.e1526280.gui.controller.StatisticEditController;
import sepm.ss17.e1526280.util.GlobalSettings;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 21.03.17
 */
public class StatPriceChangeDialog extends CustomDialog<StatisticEditController> {

    private static final String FXML = GlobalSettings.FXML_ROOT + "/statistic_changeprice.fxml";
    private static final String TITLE = GlobalSettings.APP_TITLE + ": Statistik-Details";

    public StatPriceChangeDialog(Stage owner) {
        super(owner, TITLE, FXML);
    }
}
