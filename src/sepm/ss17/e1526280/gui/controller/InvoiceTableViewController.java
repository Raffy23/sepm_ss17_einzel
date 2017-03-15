package sepm.ss17.e1526280.gui.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sepm.ss17.e1526280.dto.Reservation;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 13.03.17
 */
public class InvoiceTableViewController {


    public TableView<Reservation> tableView;
    public TableColumn detailCol;
    public TableColumn customerCol;
    public TableColumn boxCountCol;
    public TableColumn priceCol;
    public TableColumn startCol;
    public TableColumn endCol;

    public void onRefreshAction(ActionEvent event) {

    }
}
