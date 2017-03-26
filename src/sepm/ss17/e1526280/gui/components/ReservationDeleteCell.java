package sepm.ss17.e1526280.gui.components;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jetbrains.annotations.NotNull;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationWrapper;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.service.ReservationDataService;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

/**
 * This TableCell displays a delete Button and shows a Question
 * Dialog if the Button is clicked. Also the Data is deleted if
 * the User does select yes in the Dialog
 *
 * @author Raphael Ludwig
 * @version 14.03.17
 */
public class ReservationDeleteCell extends TableButtonCell<ReservationWrapper, Void> {

    /** The Service for the Data changes **/
    private final ReservationDataService dataService;

    /** The List of Data which can be changed **/
    private final ObservableList<ReservationWrapper> dataList;

    /**
     * @param dataService the data Service which should be used by the TableCell
     * @param dataList a global List with all Data which is affected by the changes
     */
    public ReservationDeleteCell(ReservationDataService dataService, ObservableList<ReservationWrapper> dataList) {
        super("Löschen");

        this.dataService = dataService;
        this.dataList = dataList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActiveItemAction(@NotNull ReservationWrapper curObj) {
        final LocalDate date = curObj.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Disable if the Box should not be edited
        if( LocalDate.now().isAfter(date) ) super.tableCellButton.setDisable(true);
        else                                super.tableCellButton.setDisable(false);

        // Register Button Listener
        super.tableCellButton.setOnAction(event -> {
            if (showQuestionDialog().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                dataService.delete(curObj.getBoxes())
                        .thenRun(() -> dataList.remove(curObj))
                        .exceptionally(DialogUtil::onError);
            }
        });
    }

    /**
     * Shows the Question dialog if the User is sure to delete the Element
     * @return the ButtonType from the Dialog
     */
    private static Optional<ButtonType> showQuestionDialog() {
        final Alert question = new Alert(Alert.AlertType.CONFIRMATION);
        question.setTitle(GlobalSettings.APP_TITLE + ": " + "Bestätigung");
        question.setHeaderText("Löschen einer Reservierung");
        question.setContentText("Die Reservierung wird dauerhaft gelöscht.\nSind Sie sicher?");

        return question.showAndWait();
    }
}
