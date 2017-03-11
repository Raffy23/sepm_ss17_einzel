package sepm.ss17.e1526280.gui.components;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.service.DataService;

import java.util.Optional;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 09.03.17
 */
public class BoxDeleteCell extends TableCell<Box, Void> {

    private final DataService dataService;
    private final ObservableList<Box> boxObservableList;
    private final Button deleteBtn = new Button("Löschen");

    public BoxDeleteCell(DataService dataService, ObservableList<Box> boxObservableList) {
        this.dataService = dataService;
        this.boxObservableList = boxObservableList;
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

            final Box curObj = getTableView().getItems().get(getIndex());

            deleteBtn.setOnAction(event -> {
                if (askForDeletion().orElseGet(() -> ButtonType.NO) == ButtonType.OK) {
                    dataService.delete(curObj);
                    boxObservableList.remove(curObj);
                }
            });
        }
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
