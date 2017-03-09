package sepm.ss17.e1526280.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.io.IOException;

/**
 * TODO: Comments
 *
 * @author Raphael Ludwig
 * @version 09.03.17
 */
public class MainWindowController {

    private static final String BOX_FRAGMENT = GlobalSettings.FXML_ROOT+"box_tableview.fxml";
    private static final String RES_FRAGMENT = GlobalSettings.FXML_ROOT+"reservation_tableview.fxml";

    @FXML private Tab boxesTab;
    @FXML private Tab reservationsTab;

    @FXML
    public void initialize() {
        //Init Fragments for Display:
        final FXMLLoader boxFragment = new FXMLLoader(getClass().getResource(BOX_FRAGMENT));
        final FXMLLoader resFragment = new FXMLLoader(getClass().getResource(RES_FRAGMENT));

        try {
            final Pane boxRoot = boxFragment.load();
            boxesTab.setContent(boxRoot);

            final Pane resRoot = resFragment.load();
            reservationsTab.setContent(resRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
