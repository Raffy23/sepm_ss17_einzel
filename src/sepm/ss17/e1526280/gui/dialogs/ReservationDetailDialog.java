package sepm.ss17.e1526280.gui.dialogs;

import javafx.stage.Stage;
import sepm.ss17.e1526280.gui.controller.ReservationDetailController;
import sepm.ss17.e1526280.util.GlobalSettings;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 18.03.17
 */
public class ReservationDetailDialog extends CustomDialog<ReservationDetailController> {

    /** The path to the fxml file **/
    private static final String fxml = GlobalSettings.FXML_ROOT + "/" + "reservation_detailview.fxml";

    /** The Title of the Dialog **/
    private static final String header = GlobalSettings.APP_TITLE + ": Details";

    /**
     * Creates the Reservation-Detail Dialog
     *
     * @param owner the owner stage of the Dialog
     */
    public ReservationDetailDialog(Stage owner) {
        super(owner, header , fxml);
    }
}
