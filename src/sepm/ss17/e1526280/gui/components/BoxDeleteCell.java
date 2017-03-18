package sepm.ss17.e1526280.gui.components;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jetbrains.annotations.NotNull;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.service.BoxDataService;

import java.util.Optional;

/**
 * A TableCell which does render the Delete Button in the Box View
 * This class also provides the Confirmation Dialog and the action
 * listeners for the actual removal of the Element in the DataSource
 * and the Observable list
 *
 * @author Raphael Ludwig
 * @version 09.03.17
 */
public class BoxDeleteCell extends TableButtonCell<Box, Void> {

    /**
     * Backend service for permanent deletion
     */
    private final BoxDataService dataService;

    /**
     * List of the Data in the fronted
     */
    private final ObservableList<Box> boxObservableList;

    public BoxDeleteCell(BoxDataService dataService, ObservableList<Box> boxObservableList) {
        super("Löschen");
        this.dataService = dataService;
        this.boxObservableList = boxObservableList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActiveItemAction(@NotNull Box box) {
        super.tableCellButton.setOnAction(event -> {
            if (askForDeletion().orElseGet(() -> ButtonType.NO) == ButtonType.OK) {
                dataService.remove(box)
                        .thenRun(() -> boxObservableList.remove(box))
                        .exceptionally(DialogUtil::onError);
            }
        });
    }

    /**
     * This function does display a Dialog to confirm the Deletion
     * @return the result of the Dialog
     */
    private static Optional<ButtonType> askForDeletion() {
        final Alert question = new Alert(Alert.AlertType.CONFIRMATION);
        question.setTitle("Confirmation Dialog");
        question.setHeaderText("Deletion of a Box");
        question.setContentText("You are going to delete a Box permanently.\nAre you sure you want to continue?");

        return question.showAndWait();
    }
}
