package sepm.ss17.e1526280;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.gui.dialogs.ExceptionAlert;
import sepm.ss17.e1526280.service.provider.ServiceProvider;
import sepm.ss17.e1526280.service.provider.SimpleServiceProvider;
import sepm.ss17.e1526280.util.DataServiceManager;
import sepm.ss17.e1526280.util.DatabaseService;
import sepm.ss17.e1526280.util.GlobalSettings;
import sepm.ss17.e1526280.util.JavaVersionChecker;
import sepm.ss17.e1526280.util.datasource.DataSource;
import sepm.ss17.e1526280.util.datasource.DatabaseSource;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * This is the Main Class of the Project which is responsible to initialize every static
 * Service and the Logger Framework. Also some other things are done here like checking for
 * the minimal Java Version loading the Database and starting the UI
 *
 * @author Raphael Ludwig
 * @version 08.03.17
 */
public class Main extends Application {

    /** The File for the main Window **/
    private static final String FXML_MAIN = "main_window.fxml";
    private static final String FXML_SPLASH = "splash_screen.fxml";


    public static void main(String[] args) {
        //First load and parse the App config files
        GlobalSettings.initialize();

        //Honor System set config File Path for Logback (useful for debugging)
        if( System.getProperty("logback.configurationFile") == null )
            if( GlobalSettings.getConfig().getProperty("logback.config") != null )
                System.setProperty("logback.configurationFile", GlobalSettings.getConfig().getProperty("logback.config"));

        //Start JavaFX and display the GUI
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        final String FXML_PATH = GlobalSettings.FXML_ROOT + FXML_MAIN;

        //Now we can use the Logger ...
        final Logger LOG = LoggerFactory.getLogger(Main.class);
        LOG.debug("Loading UI");

        //Load the UI from the Scene Builder fxml generated file
        final Pane root = FXMLLoader.load(getClass().getResource(GlobalSettings.FXML_ROOT + FXML_SPLASH));
        final Scene scene = new Scene(root);

        //Can't load real gui here because the controller needs already the Database
        LOG.trace("Setting loading Scene");
        stage.setScene(scene);
        stage.setTitle(GlobalSettings.APP_TITLE);
        stage.show();

        //Check the Java Version
        checkJavaVersion(LOG, stage);

        //Also make sure we hook up the Database to the onCloseRequest ...
        stage.setOnCloseRequest(windowEvent -> DatabaseService.destroyService());

        //Now we might initialize the Database Service
        startDataService(LOG).exceptionally(throwable -> Main.onBackendError(throwable,LOG,stage))
                             .thenAccept(aBoolean -> {
            //Database is now up and usable so jump to real GUI
            if( aBoolean )
            Platform.runLater(() -> {
                //Now we jump into the real GUI:
                try {

                    final Pane mainRoot = FXMLLoader.load(getClass().getResource(FXML_PATH));
                    final Scene mainScene = new Scene(mainRoot);

                    LOG.debug("Sanity check done, jumping to real GUI");
                    stage.setScene(mainScene);
                    stage.centerOnScreen();
                    stage.show();

                } catch (IOException e) {
                    LOG.error("Unable to launch real UI, check FXML_PATH!");
                    DialogUtil.onFatal(e);
                }
            });

        });
    }

    private static void checkJavaVersion(Logger LOG, Stage stage) {
        LOG.debug("Checking Java Version for 1.8._60");

        if( !JavaVersionChecker.checkJavaVersion(8,60)) {
            LOG.error("Unable to launch Application, a Java Version newer than 1.8.0_60 is required!");
            LOG.error("Found Java version: " + System.getProperty("java.version"));

            final Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error: " + GlobalSettings.APP_TITLE);
            a.setHeaderText("JVM Version zu alt!");
            a.setContentText("Diese Anwendung benötigt eine Java version\nneuer als 1.8.0_60, es wurde aber \n" + System.getProperty("java.version") + " erkannt!");
            a.show();

            stage.close();

            LOG.trace("Invoking Platform.exit()");
            Platform.exit();
        }
    }

    private static CompletableFuture<Boolean> startDataService(Logger LOG) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                //To change backend change the data source
                final DataSource backend = new DatabaseSource();
                //To change used services change this line:
                final ServiceProvider serviceProvider = new SimpleServiceProvider();

                //Construct data backend service
                DataServiceManager.initialize(backend, serviceProvider);

                return true;

            } catch (Exception e) {
                LOG.error("Unable to initialize Database refusing Start of Application!");
                DialogUtil.onFatal(e);

                return false;
            }
        });
    }

    private static boolean onBackendError(Throwable err, Logger LOG, Stage stage) {
        Platform.runLater(() -> {
            final ExceptionAlert a = new ExceptionAlert(err);
            a.setTitle("Error: " + GlobalSettings.APP_TITLE);
            a.setHeaderText("Datenbankfehler:");
            a.setContentText("Es konnte keine Verbdinung zur Datenbank hergestellt werden!");
            a.showAndWait();

            stage.close();
            LOG.trace("Invoking Platform.exit()");
            Platform.exit();
        });

        return false;
    }
}
