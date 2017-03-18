package sepm.ss17.e1526280.gui.components;

import javafx.scene.Node;
import javafx.scene.control.TableCell;

/**
 * A Simple TableCell which does draw the Component into Cell which is given as Cell
 * generic Parameter
 *
 * @author Raphael Ludwig
 * @version 18.03.17
 */
public class TableComponentCell<Item,Cell extends Node> extends TableCell<Item,Cell> {
    /**
     * {@inheritDoc}
     *
     * This Method does draw the Item as Graphic in the Table
     */
    @Override
    protected void updateItem(Cell item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            setGraphic(item);
            setText(null);
        }
    }
}
