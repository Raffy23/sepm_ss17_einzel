package sepm.ss17.e1526280.gui.dialogs;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.util.GlobalSettings;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 09.03.17
 */
public class DialogUtil {

    /** Logger for logging ... duh **/
    private static final Logger LOG = LoggerFactory.getLogger(DialogUtil.class);

    public static Void onError(Throwable err) {
        LOG.error("There was an Error: " + err.getMessage());

        Platform.runLater(() -> {
            Alert alert = new ExceptionAlert(err);

            alert.setTitle("Error: " + GlobalSettings.APP_TITLE);
            alert.setHeaderText("Es ist ein Fehler aufgetreten:");
            //TODO: Text
            //alert.setContentText("");
            alert.show();
        });

        return null;
    }

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
