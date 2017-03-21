package sepm.ss17.e1526280.gui.controller.wrapper;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import sepm.ss17.e1526280.dto.StatisticRow;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 21.03.17
 */
public class StatisticRowWrapper extends StatisticRow {


    private final CheckBox checkBox = new CheckBox("");
    private final Button details = new Button("Details");

    public StatisticRowWrapper(StatisticRow row) {
        super(row.getBox(), row.getReservationList(), row.getData());
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public Button getDetails() {
        return details;
    }
}
