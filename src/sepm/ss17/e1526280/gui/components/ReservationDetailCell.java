package sepm.ss17.e1526280.gui.components;

import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import sepm.ss17.e1526280.gui.controller.ReservationDetailController;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationWrapper;
import sepm.ss17.e1526280.gui.dialogs.CustomDialog;
import sepm.ss17.e1526280.gui.dialogs.ReservationDetailDialog;

/**
 * This TableCell does show a Detail Button in the Reservation Table.
 * The Data for the Reservation is received by the current Table row
 *
 * @author Raphael Ludwig
 * @version 14.03.17
 */
public class ReservationDetailCell extends TableButtonCell<ReservationWrapper, Void> {

    public ReservationDetailCell() {
        super("Details");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActiveItemAction(@NotNull ReservationWrapper wrapper) {
        super.tableCellButton.setOnAction(event -> {
            final Stage parentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            final CustomDialog<ReservationDetailController> dialog = new ReservationDetailDialog(parentStage);
            final ReservationDetailController controller = dialog.getController();

            // Init controller and show
            controller.init(wrapper);
            dialog.show();
        });
    }

}
