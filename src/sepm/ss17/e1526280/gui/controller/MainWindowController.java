package sepm.ss17.e1526280.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.io.IOException;

/**
 * TODO: Comments
 *
 * @author Raphael Ludwig
 * @version 09.03.17
 */
public class MainWindowController {

    /** Logger for logging **/
    private static final Logger LOG = LoggerFactory.getLogger(MainWindowController.class);

    private static final String BOX_FRAGMENT = GlobalSettings.FXML_ROOT+"box_tableview.fxml";
    private static final String RES_FRAGMENT = GlobalSettings.FXML_ROOT+"reservation_tableview.fxml";
    private static final String INV_FRAGMENT = GlobalSettings.FXML_ROOT+"invoice_tableview.fxml";
    private static final String STA_FRAGMENT = GlobalSettings.FXML_ROOT+"statistic_view.fxml";

    @FXML private Tab boxesTab;
    @FXML private Tab reservationsTab;
    @FXML private Tab invoiceTab;
    @FXML private Tab statisticTab;

    /**
     * Initializes all the Data which is needed by the Controller
     */
    @FXML
    public void initialize() {
        //Init Fragments for Display:
        final FXMLLoader boxFragment = new FXMLLoader(getClass().getResource(BOX_FRAGMENT));
        final FXMLLoader resFragment = new FXMLLoader(getClass().getResource(RES_FRAGMENT));
        final FXMLLoader invFragment = new FXMLLoader(getClass().getResource(INV_FRAGMENT));
        final FXMLLoader staFragment = new FXMLLoader(getClass().getResource(STA_FRAGMENT));

        try {
            final Pane boxRoot = boxFragment.load();
            boxesTab.setContent(boxRoot);

            final Pane resRoot = resFragment.load();
            reservationsTab.setContent(resRoot);

            final Pane invRoot = invFragment.load();
            invoiceTab.setContent(invRoot);

            final Pane staRoot = staFragment.load();
            statisticTab.setContent(staRoot);

        } catch (IOException e) {
            DialogUtil.onFatal(e);
        }

        // Set some change listeners
        boxesTab.setOnSelectionChanged(event -> ((BoxTableViewController)boxFragment.getController()).loadData());
        reservationsTab.setOnSelectionChanged(event -> ((ReservationTableViewController)resFragment.getController()).loadData());
        invoiceTab.setOnSelectionChanged(event -> ((InvoiceTableViewController)invFragment.getController()).loadData());
        statisticTab.setOnSelectionChanged(event -> ((StatisticController)staFragment.getController()).onReload(null));
    }

}
