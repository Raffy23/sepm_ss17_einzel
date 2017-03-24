package sepm.ss17.e1526280.gui.dialogs;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.util.DatabaseService;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A simple Utility class which can spawn different dialogs with pre-defined Text messages
 *
 * @author Raphael Ludwig
 * @version 11.03.17
 */
public class DialogUtil {

    /** Logger for logging ... duh **/
    private static final Logger LOG = LoggerFactory.getLogger(DialogUtil.class);

    /** Simple Lock for shutdown even, can be triggered more than once and let the app crash very hard **/
    private static final AtomicBoolean lock = new AtomicBoolean(true);

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
     * that the Application shutdown is forced, only one dialog may be created
     * @param err the Exception which should be displayed
     * @return null
     */
    public static Void onFatal(Throwable err) {
        LOG.error("There was a Fatal Error: " + err.getMessage());

        if( lock.getAndSet(false) )
            Platform.runLater(() -> {
                Alert alert = new ExceptionAlert(err);

                alert.setTitle("Fatal Error: " + GlobalSettings.APP_TITLE);
                alert.setHeaderText("Es ist ein Fehler aufgetreten:");
                alert.setContentText("Das Program muss beendet werden");
                alert.showAndWait();

                DatabaseService.destroyService();
                Platform.exit();
            });
        else
            LOG.warn("Tried to spawn multiple fatal dialogs!");

        return null;
    }
}
