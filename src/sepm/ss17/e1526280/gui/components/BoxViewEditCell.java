package sepm.ss17.e1526280.gui.components;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.gui.controller.BoxCreationController;
import sepm.ss17.e1526280.gui.dialogs.BoxDetailDialog;
import sepm.ss17.e1526280.gui.dialogs.CustomDialog;
import sepm.ss17.e1526280.gui.dialogs.ExceptionAlert;
import sepm.ss17.e1526280.service.DataService;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.io.IOException;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 09.03.17
 */
public class BoxViewEditCell extends TableCell<Box, Void> {

    private final DataService dataService;
    private final TableView<Box> boxTable;
    private final Button editBtn = new Button("Edit");

    public BoxViewEditCell(DataService dataService, TableView<Box> boxTable) {
        this.dataService = dataService;
        this.boxTable = boxTable;
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            final Box curObj = getTableView().getItems().get(getIndex());

            editBtn.setOnAction((ActionEvent event) -> {

                final Stage parentStage = (Stage) editBtn.getScene().getWindow();
                try {
                    final CustomDialog<BoxCreationController> dialog = new BoxDetailDialog(parentStage, "Edit Box data");
                    final BoxCreationController controller = dialog.getController();

                    controller.init(curObj);
                    controller.setOkBtnEventHandler(event1 -> {

                        if (!controller.validateInput())
                            return;

                        curObj.updateDataFrom(controller.generateBox());
                        dataService.update(curObj)
                                .thenAccept(box -> Platform.runLater(boxTable::refresh))
                                .exceptionally(BoxViewEditCell::onError);

                        final Button btn = (Button) event1.getSource();
                        ((Stage) btn.getScene().getWindow()).close();
                    });

                    dialog.show();
                } catch (IOException e) {
                    System.err.println("Fatal: Unable to create BoxDetailDialog!");
                    e.printStackTrace();
                }
            });

            setGraphic(editBtn);
            setText(null);
        }
    }


    private static Void onError(Throwable err) {
        Alert alert = new ExceptionAlert(err);
        alert.setTitle("Error: " + GlobalSettings.APP_TITLE);
        alert.setHeaderText("Could not edit the new Box");
        alert.setContentText("While editing a Box an error occurred and the Box could not been saved the the Disk, please contact the support personal with the error message below the resolve the problem.");
        alert.show();

        return null;
    }
}
