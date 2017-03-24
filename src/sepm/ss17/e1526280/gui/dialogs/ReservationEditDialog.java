package sepm.ss17.e1526280.gui.dialogs;

import javafx.stage.Stage;
import sepm.ss17.e1526280.gui.controller.ReservationEditController;
import sepm.ss17.e1526280.util.GlobalSettings;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 18.03.17
 */
public class ReservationEditDialog extends CustomDialog<ReservationEditController> {

    /** The path to the fxml file **/
    private static final String FXML_PATH = GlobalSettings.FXML_ROOT + "/" + "reservation_editview.fxml";

    /** The Title of the Dialog **/
    private static final String TITLE = GlobalSettings.APP_TITLE + ": Reservierung bearbeiten";

    /**
     * Creates the Reservation-Detail Dialog
     *
     * @param owner the owner stage of the Dialog
     */
    public ReservationEditDialog(Stage owner) {
        super(owner, TITLE, FXML_PATH);
    }
}
