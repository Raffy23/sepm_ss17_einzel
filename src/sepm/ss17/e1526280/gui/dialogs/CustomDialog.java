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

    /** The stage in which the dialog content is shown **/
    private final Stage dialog = new Stage();

    /** The "Window" of the Dialog **/
    protected Scene scene;

    /** The generic Controller which can control the Dialog (received from FXML)**/
    protected T controller;

    /**
     * Creates the Basic Dialog with the title and the fxml
     * The IOException from the FXMLLoader is automatically caught and
     * the DialogUtil.onFatal function is invoked
     *
     * @param owner the owner stage of the Dialog
     * @param title the title of the Dialog
     * @param fxml the fxml path of the dialog content
     */
    protected CustomDialog(Stage owner, String title, String fxml) {
        // Load the fxml resource
        final FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));

        try {
            // Load the stuff and init the dialog
            final Pane root = loader.load();
            scene  = new Scene(root);

            dialog.setScene(scene);
            dialog.setTitle(title);
            dialog.initOwner(owner);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setResizable(false);

            // We also want to export the controller to the outside
            controller = loader.getController();
        } catch (IOException e) {
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

    public void onClose() {
        dialog.close();
    }
}
