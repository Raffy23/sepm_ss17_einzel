package sepm.ss17.e1526280.gui.components;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import org.jetbrains.annotations.NotNull;
import sepm.ss17.e1526280.gui.controller.wrapper.ReservationEntryWrapper;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.util.List;
import java.util.Optional;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 18.03.17
 */
public class ReservationEntryDeleteCell extends TableButtonCell<ReservationEntryWrapper, Void> {

    private final List<ReservationEntryWrapper> toDelete;
    private final TableView<ReservationEntryWrapper> resTable;

    public ReservationEntryDeleteCell(List<ReservationEntryWrapper> toDelete, TableView<ReservationEntryWrapper> resTable) {
        super("Löschen");

        this.toDelete = toDelete;
        this.resTable = resTable;
    }

    @Override
    protected void onActiveItemAction(@NotNull ReservationEntryWrapper curObj) {
        super.tableCellButton.setOnAction(event -> {
            if (showQuestion().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                toDelete.add(curObj);
                resTable.getItems().remove(curObj);
                resTable.refresh();
            }
        });
    }

    private static Optional<ButtonType> showQuestion() {
        final Alert question = new Alert(Alert.AlertType.CONFIRMATION);
        question.setTitle(GlobalSettings.APP_TITLE +": " + "Bestätigung");
        question.setHeaderText("Reservierung Löschen");
        question.setContentText("Soll dieser teil der Reservierung gelöscht werden?");

        return question.showAndWait();
    }
}
