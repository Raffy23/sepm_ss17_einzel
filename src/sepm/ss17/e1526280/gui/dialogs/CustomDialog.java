package sepm.ss17.e1526280.gui.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class is a Base for other Dialogs which can be constructed by the
 * Program
 *
 * @author Raphael Ludwig
 * @version 09.03.17
 */
public abstract class CustomDialog<T> {

    private final Stage dialog = new Stage();
    protected Scene scene;
    protected T controller;

    protected CustomDialog(Stage owner, String title, String fxml) {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));

        try {
            final Pane root = loader.load();
            scene  = new Scene(root);

            dialog.setScene(scene);
            dialog.setTitle(title);
            dialog.initOwner(owner);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setResizable(false);

            controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.onFatal(e);

            scene = null;
            controller = null;
        }

    }

    /**
     * Shows the Dialog on the Screen
     */
    public void show() {
        dialog.show();
    }

    /**
     * @return the controller responsible for the Dialog
     */
    public T getController() {
        return controller;
    }
}
