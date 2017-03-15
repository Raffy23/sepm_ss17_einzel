package sepm.ss17.e1526280.gui.components;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.stage.Stage;
import sepm.ss17.e1526280.gui.controller.ReservationDetailController;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationWrapper;
import sepm.ss17.e1526280.gui.dialogs.CustomDialog;
import sepm.ss17.e1526280.util.GlobalSettings;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 14.03.17
 */
public class ReservationDetailCell extends TableCell<ReservationWrapper, Void> {
    private final Button detailBtn = new Button("Details");

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            setGraphic(detailBtn);
            setText(null);

            final ReservationWrapper curObj = getTableView().getItems().get(getIndex());

            detailBtn.setOnAction(event -> {
                final Stage parentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                final String fxml = GlobalSettings.FXML_ROOT + "/" + "reservation_detailview.fxml";

                final CustomDialog<ReservationDetailController> dialog = new CustomDialog<ReservationDetailController>(parentStage, GlobalSettings.APP_TITLE + " Reservierungsdetails", fxml) {
                };
                final ReservationDetailController controller = dialog.getController();
                controller.init(curObj);
                dialog.show();
            });
        }
    }
}
