package sepm.ss17.e1526280.gui.components;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import org.jetbrains.annotations.NotNull;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 18.03.17
 */
public abstract class TableButtonCell<Item,Cell> extends TableCell<Item, Cell> {
    protected final Button tableCellButton;

    public TableButtonCell(String buttonText) {
        this.tableCellButton = new Button(buttonText);
    }

    /**
     * {@inheritDoc}
     *
     * If the Cell is Empty nothing is drawn otherwise the Button
     * is drawn and the onActiveItemAction is invoked
     */
    @Override
    protected void updateItem(Cell item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            final Item curObj = getTableView().getItems().get(getIndex());
            onActiveItemAction(curObj);

            setGraphic(this.tableCellButton);
            setText(null);
        }
    }

    /**
     * This function is called every Time the Cell does have an Item to Display
     * @param item the Item which is in the Current Table row
     */
    protected abstract void onActiveItemAction(@NotNull Item item);
}