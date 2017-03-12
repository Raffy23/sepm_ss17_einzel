package sepm.ss17.e1526280.gui.dialogs;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.util.GlobalSettings;

/**
 * A simple Utility class which can spawn different dialogs with pre-defined Text messages
 *
 * @author Raphael Ludwig
 * @version 11.03.17
 */
public class DialogUtil {

    /** Logger for logging ... duh **/
    private static final Logger LOG = LoggerFactory.getLogger(DialogUtil.class);

    /**
     * Displays an ExceptionAlert Dialog
     * @param err the Exception which should be displayed
     * @return null
     */
    public static Void onError(Throwable err) {
        LOG.error("There was an Error: " + err.getMessage());

        Platform.runLater(() -> {
            Alert alert = new ExceptionAlert(err);

            alert.setTitle("Error: " + GlobalSettings.APP_TITLE);
            alert.setHeaderText("Es ist ein Fehler aufgetreten:");
            alert.setContentText("Bei der letzten Aktion ist ein Fehler aufgetreten.\nIn den Details finden Sie weitere Informationen zu dem Fehler.");
            alert.show();
        });

        return null;
    }

    /**
     * Displays an Exception Dialog and waits for the Dialog to close, after
     * that the Application shutdown is encored
     * @param err the Exception which should be displayed
     * @return null
     */
    public static Void onFatal(Throwable err) {
        LOG.error("There was a Fatal Error: " + err.getMessage());

        Platform.runLater(() -> {
            Alert alert = new ExceptionAlert(err);

            alert.setTitle("Fatal Error: " + GlobalSettings.APP_TITLE);
            alert.setHeaderText("Es ist ein Fehler aufgetreten:");
            alert.setContentText("Das Program muss beendet werden");
            alert.showAndWait();

            Platform.exit();
        });

        return null;
    }
}
