package sepm.ss17.e1526280.gui.components;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.gui.controller.BoxCreationController;
import sepm.ss17.e1526280.gui.dialogs.BoxDetailDialog;
import sepm.ss17.e1526280.gui.dialogs.CustomDialog;
import sepm.ss17.e1526280.gui.dialogs.ExceptionAlert;
import sepm.ss17.e1526280.service.BoxDataService;
import sepm.ss17.e1526280.util.GlobalSettings;

/**
 * Table Cell Renderer for the Edit Cell
 * A Button is rendered in the cell and the
 * dialog and onClick events are also handheld
 *
 * @author Raphael Ludwig
 * @version 11.03.17
 */
public class BoxViewEditCell extends TableButtonCell<Box, Void> {

    /** Logger for logging **/
    private static final Logger LOG = LoggerFactory.getLogger(BoxViewEditCell.class);

    /** Box Service for data backend communication **/
    private final BoxDataService dataService;

    /** This is the element which should be refreshed after data changes **/
    private final TableView<Box> boxTable;

    /**
     * @param dataService service which is used to persist the changes
     * @param boxTable the table which should be refreshed if any data change occurs
     */
    public BoxViewEditCell(BoxDataService dataService, TableView<Box> boxTable) {
        super("Bearbeiten");

        this.dataService = dataService;
        this.boxTable = boxTable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActiveItemAction(@NotNull Box curObj) {
        super.tableCellButton.setOnAction((ActionEvent event) -> {
            LOG.trace("Set Edit Button Action");
            final Stage parentStage = (Stage) super.tableCellButton.getScene().getWindow();

            //Open a new Dialog for the Box editing
            final CustomDialog<BoxCreationController> dialog = new BoxDetailDialog(parentStage, GlobalSettings.APP_TITLE+": " + "Box bearbeiten");
            final BoxCreationController controller = dialog.getController();

            //Init the Dialog with some Data
            controller.init(curObj);
            //Set the Listener in the Dialog
            controller.setOkBtnEventHandler(event1 -> {
                LOG.debug("Update Box " + curObj + " with Dialog Data");

                //Check for Input validation
                if (!controller.validateInput()) {
                    LOG.info("Input validation failed");
                    return;
                }

                //Update the Data in the Box and Backend
                curObj.updateDataFrom(controller.generateBox());
                dataService.update(curObj)
                        .thenAccept(box -> Platform.runLater(boxTable::refresh))
                        .exceptionally(BoxViewEditCell::onError);

                final Button btn = (Button) event1.getSource();
                ((Stage) btn.getScene().getWindow()).close();
            });

            dialog.show();
        });
    }

    /**
     * Displays the Error Dialog which is needed if there was some error while editing the Box
     * @param err exception which was thrown
     * @return null
     */
    private static Void onError(@NotNull Throwable err) {
        Alert alert = new ExceptionAlert(err);
        alert.setTitle("Error: " + GlobalSettings.APP_TITLE);
        alert.setHeaderText("Box konnte nicht bearbeitet werden!");
        alert.setContentText("Es ist ein Problem beim bearbeiten der Box aufgetreten, drücken Sie auf Details anzeigen um mehr von dem Fehler zu erfahren.");
        alert.show();

        return null;
    }
}
