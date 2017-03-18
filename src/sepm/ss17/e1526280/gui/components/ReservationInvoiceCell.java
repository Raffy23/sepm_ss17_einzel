package sepm.ss17.e1526280.gui.components;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jetbrains.annotations.NotNull;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationWrapper;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.service.ReservationDataService;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.util.Optional;

/**
 * Table Cell which does display a Button to create a Invoice and
 * does register all the needed Listeners for it
 *
 * @author Raphael Ludwig
 * @version 14.03.17
 */
public class ReservationInvoiceCell extends TableButtonCell<ReservationWrapper, Void> {
    private final ReservationDataService dataService;
    private final ObservableList<ReservationWrapper> dataList;

    /**
     * Create the table Cell
     * @param dataService data service which should be used
     * @param dataList the fronted list on which the data depends, it it changed if changes to the data are applied
     */
    public ReservationInvoiceCell(ReservationDataService dataService, ObservableList<ReservationWrapper> dataList) {
        super(">");

        this.dataService = dataService;
        this.dataList = dataList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActiveItemAction(@NotNull ReservationWrapper wrapper) {
        super.tableCellButton.setOnAction(event -> {
            if (showQuestion().orElseGet(() -> ButtonType.NO) == ButtonType.OK) {
                dataService.toInvoice(wrapper.getBoxes())
                        .thenRun(() -> Platform.runLater(() -> dataList.remove(wrapper)))
                        .exceptionally(DialogUtil::onError);
            }
        });
    }

    /**
     * Shows the "Are you sure" Dialog for creation an Invoice
     * @return the ButtonType of the Dialog if closed
     */
    private static Optional<ButtonType> showQuestion() {
        final Alert question = new Alert(Alert.AlertType.CONFIRMATION);
        question.setTitle(GlobalSettings.APP_TITLE +": " + "Bestätigung");
        question.setHeaderText("Rechnung anlegen");
        question.setContentText("Soll von dieser Reservierung eine Rechnung\nangelegt werden?");

        return question.showAndWait();
    }
}
