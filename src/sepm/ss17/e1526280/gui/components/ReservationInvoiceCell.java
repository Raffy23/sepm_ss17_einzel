package sepm.ss17.e1526280.gui.components;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationWrapper;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.service.ReservationDataService;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.util.Optional;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 14.03.17
 */
public class ReservationInvoiceCell extends TableCell<ReservationWrapper, Void> {
    private final Button invoiceBtn = new Button(">");

    private final ReservationDataService dataService;
    private final ObservableList<ReservationWrapper> dataList;

    public ReservationInvoiceCell(ReservationDataService dataService, ObservableList<ReservationWrapper> dataList) {
        this.dataService = dataService;
        this.dataList = dataList;
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            setGraphic(invoiceBtn);
            setText(null);

            final ReservationWrapper curObj = getTableView().getItems().get(getIndex());

            invoiceBtn.setOnAction(event -> {
                final Alert question = new Alert(Alert.AlertType.CONFIRMATION);
                question.setTitle(GlobalSettings.APP_TITLE +": " + "Bestätigung");
                question.setHeaderText("Rechnung anlegen");
                question.setContentText("Soll von dieser Reservierung eine Rechnung\nangelegt werden?");

                Optional<ButtonType> result = question.showAndWait();
                if (result.get() == ButtonType.OK) {
                    dataService.toInvoice(curObj.getBoxes())
                            .thenRun(() -> dataList.remove(curObj))
                            .exceptionally(DialogUtil::onError);
                }
            });
        }
    }
}
