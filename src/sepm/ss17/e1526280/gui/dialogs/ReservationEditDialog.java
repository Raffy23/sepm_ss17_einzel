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

    private static final String fxml = GlobalSettings.FXML_ROOT + "/" + "reservation_editview.fxml";
    private static final String header = "Reservierung bearbeiten";

    /**
     * Creates the Reservation-Detail Dialog
     *
     * @param owner the owner stage of the Dialog
     */
    public ReservationEditDialog(Stage owner) {
        super(owner, header , fxml);
    }
}
