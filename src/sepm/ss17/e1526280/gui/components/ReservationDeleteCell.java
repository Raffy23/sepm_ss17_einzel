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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 14.03.17
 */
public class ReservationDeleteCell extends TableCell<ReservationWrapper, Void> {

    private final Button deleteBtn = new Button("Löschen");
    private final ReservationDataService dataService;
    private final ObservableList<ReservationWrapper> dataList;

    public ReservationDeleteCell(ReservationDataService dataService, ObservableList<ReservationWrapper> dataList) {
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
            setGraphic(deleteBtn);
            setText(null);

            final ReservationWrapper curObj = getTableView().getItems().get(getIndex());
            final LocalDate endDate = curObj.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if( LocalDate.now().isAfter(endDate) ) {
                deleteBtn.setDisable(true);
            } else {
                deleteBtn.setOnAction(event -> {
                    final Alert question = new Alert(Alert.AlertType.CONFIRMATION);
                    question.setTitle(GlobalSettings.APP_TITLE + ": " + "Bestätigung");
                    question.setHeaderText("Löschen einer Reservierung");
                    question.setContentText("Die Reservierung wird dauerhaft gelöscht.\nSind Sie sicher?");

                    Optional<ButtonType> result = question.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        dataService.delete(curObj.getBoxes())
                                .thenRun(() -> dataList.remove(curObj))
                                .exceptionally(DialogUtil::onError);
                    }
                });
            }
        }
    }
}
