package sepm.ss17.e1526280.gui.components;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.gui.dialogs.ImageDialog;
import sepm.ss17.e1526280.service.BoxDataService;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 09.03.17
 */
public class BoxImageCell extends TableCell<Box, String> {
    private final BoxDataService dataService;
    private final Button viewImage = new Button("Bild");

    public BoxImageCell(BoxDataService dataService) {
        this.dataService = dataService;
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            final Box curObj = getTableView().getItems().get(getIndex());

            if (curObj.getPhoto() != null && curObj.getPhoto().length() > 0) {
                viewImage.setOnAction((ActionEvent event) -> new ImageDialog(curObj, dataService));
                setGraphic(viewImage);
                setText(null);
            } else {
                setGraphic(null);
                setText(null);
            }
        }
    }
}
