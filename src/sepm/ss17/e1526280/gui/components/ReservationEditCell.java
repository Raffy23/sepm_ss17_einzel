package sepm.ss17.e1526280.gui.components;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.stage.Stage;
import sepm.ss17.e1526280.dto.Reservation;
import sepm.ss17.e1526280.gui.controller.ReservationEditController;
import sepm.ss17.e1526280.gui.controller.ReservationTableViewController;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationEntryWrapper;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationWrapper;
import sepm.ss17.e1526280.gui.dialogs.CustomDialog;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.service.ReservationDataService;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 14.03.17
 */
public class ReservationEditCell extends TableCell<ReservationWrapper, Void> {
    private final Button editBtn = new Button("Bearbeiten");

    private final ReservationDataService dataService;
    private final ReservationTableViewController controller;

    public ReservationEditCell(ReservationDataService dataService, ReservationTableViewController controller) {
        this.dataService = dataService;
        this.controller = controller;
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            setGraphic(editBtn);
            setText(null);

            final ReservationWrapper curObj = getTableView().getItems().get(getIndex());
            editBtn.setOnAction(event -> {
                final Stage parentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                final String fxml = GlobalSettings.FXML_ROOT + "/" + "reservation_editview.fxml";

                final CustomDialog<ReservationEditController> dialog = new CustomDialog<ReservationEditController>(parentStage, GlobalSettings.APP_TITLE + " Reservierung bearbeiten", fxml) {};
                final ReservationEditController controller = dialog.getController();

                controller.init(curObj);
                controller.setOkBtnEventHandler(event1 -> {
                    if( !controller.validate() )
                        return; //TODO: Tell the user that eh can't save

                    System.out.println("edit reservation");
                    final List<Reservation> deleteData = controller.getToDelete().stream().map(ReservationEntryWrapper::toReservation).collect(Collectors.toList());
                    final List<Reservation> updateData = controller.getToUpdate().stream().map(ReservationEntryWrapper::toReservation).collect(Collectors.toList());

                    final String name = controller.getName();
                    final Date start = controller.getStartDate();
                    final Date end = controller.getEndDate();

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
    }

    private Void onError(Throwable err) {
        this.controller.loadData();
        Platform.runLater(() -> DialogUtil.onError(err));

        return null;
    }
}
