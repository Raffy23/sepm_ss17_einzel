package sepm.ss17.e1526280.gui.components;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sepm.ss17.e1526280.dto.Reservation;
import sepm.ss17.e1526280.gui.controller.ReservationEditController;
import sepm.ss17.e1526280.gui.controller.ReservationTableViewController;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationEntryWrapper;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationWrapper;
import sepm.ss17.e1526280.gui.dialogs.CustomDialog;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.gui.dialogs.ReservationEditDialog;
import sepm.ss17.e1526280.service.ReservationDataService;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This TableCell does Show a Edit button in the Cell, if clicked
 * the ReservationDetailDialog is created and the Details of the
 * Reservation can be changed
 *
 * @author Raphael Ludwig
 * @version 14.03.17
 */
public class ReservationEditCell extends TableButtonCell<ReservationWrapper, Void> {

    private final ReservationDataService dataService;
    private final ReservationTableViewController controller;

    public ReservationEditCell(ReservationDataService dataService, ReservationTableViewController controller) {
        super("Bearbeiten");

        this.dataService = dataService;
        this.controller = controller;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActiveItemAction(@NotNull ReservationWrapper curObj) {
        final LocalDate date = curObj.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Disable if the Element should not be edited
        if( LocalDate.now().isAfter(date) )     super.tableCellButton.setDisable(true);
        else                                    super.tableCellButton.setDisable(false);

        super.tableCellButton.setOnAction(event -> {
            final Stage parentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            final CustomDialog<ReservationEditController> dialog = new ReservationEditDialog(parentStage);
            final ReservationEditController controller = dialog.getController();

            // Initialize controller with Data
            controller.init(curObj);
            controller.setOkBtnEventHandler(event1 -> {

                // The validate Function of the Controller is not safe to
                // call without catching any RuntimeException since it uses
                // Future.join() which does throw a Exception and can potentially
                // crash the Application in a non-safe way
                try {
                    if (!controller.validate()) {
                        showDialog();
                        return;
                    }
                } catch( RuntimeException e ) {
                    DialogUtil.onError(e);
                    return;
                }

                // Convert the lists to the right type
                final List<Reservation> deleteData = controller.getToDelete().stream().map(ReservationEntryWrapper::toReservation).collect(Collectors.toList());
                final List<Reservation> updateData = controller.getToUpdate().stream().map(ReservationEntryWrapper::toReservation).collect(Collectors.toList());

                // Extract data from the dialog
                final String name = controller.getName();
                final Date start = controller.getStartDate();
                final Date end = controller.getEndDate();

                // Do some Service stuff
                dataService.delete(deleteData)
                        .thenRun(() -> {
                            updateData.forEach(reservation -> {
                                reservation.setCustomer(name);
                                reservation.setStart(start);
                                reservation.setEnd(end);
                            });

                            dataService.update(updateData).join();
                            this.controller.loadData();
                        })
                        .exceptionally(this::onError);

                final Button btn = (Button) event1.getSource();
                ((Stage) btn.getScene().getWindow()).close();
            });

            dialog.show();
        });
    }

    /**
     * Refreshes the Data and calls the DialogUtil.onError Method
     * @param err throwable which caused the error
     * @return null
     */
    @Nullable
    private Void onError(@NotNull Throwable err) {
        Platform.runLater(this.controller::loadData);
        Platform.runLater(() -> DialogUtil.onError(err));

        return null;
    }

    /**
     * Shows the Warning Dialog if the Dialog Controller fails to validate the Data
     */
    private static void showDialog() {
        Alert warning = new Alert(Alert.AlertType.WARNING);
        warning.setTitle(GlobalSettings.APP_TITLE+": Warnung");
        warning.setHeaderText("Achtung: Es konnte keine Änderungen übernommen werden!");
        warning.setContentText("Es ist ein Validierungsfehler ausgetreten,\ndeswegen konnten keine Änderungen übernommen werden.\nBitte überprüfen Sie die eingegeben Daten und versuchen es erneut.");
        warning.show();
    }
}
