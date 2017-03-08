package sepm.ss17.e1526280;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dao.exceptions.CheckedDatabaseException;
import sepm.ss17.e1526280.service.DatabaseService;
import sepm.ss17.e1526280.util.GlobalSettings;
import sepm.ss17.e1526280.util.JavaVersionChecker;

import java.io.IOException;

/**
 * This is the Main Class of the Project which is responsible to initialize every static
 * Service and the Logger Framework. Also some other things are done here like checking for
 * the minimal Java Version loading the Database and starting the UI
 *
 * @author Raphael Ludwig
 * @version 08.03.17
 */
public class Main extends Application {

    /** The Title for the JavaFX Window **/
    private static final String APP_TITLE = "Wendy's Pferdepension";

    /** The File for the main Window **/
    private static final String FXML_FILE = "main_window.fxml";


    public static void main(String[] args) {
        //First load and parse the App config files
        GlobalSettings.initialize();

        //Honor System set config File Path for Logback (useful for debugging)
        if( System.getProperty("logback.configurationFile") == null )
            if( GlobalSettings.getConfig().getProperty("logback.config") != null )
                System.setProperty("logback.configurationFile", GlobalSettings.getConfig().getProperty("logback.config"));

        //Now we can use the Logger ...
        final Logger LOG = LoggerFactory.getLogger(Main.class);

        //Check the Java Version
        if( !JavaVersionChecker.checkJavaVersion(8,60)) {
            LOG.error("Unable to launch Application, a Java Version newer than 1.8.0_60 is required!");
            LOG.error("Found Java version: " + System.getProperty("java.version"));
            System.exit(-1);
        }

        //Now we might initialize the Database Service
        try {
            DatabaseService.initialize();
        } catch (CheckedDatabaseException e) {
            //TODO: Should there be already a GUI so we can signal the User or just fail silent ?

            LOG.error("Unable to initialize Database refusing Start of Application!");
            e.printStackTrace();

            try {
                LOG.info("Trying to clean up the Database files ...");
                DatabaseService.deleteDatabaseFiles();
            } catch (IOException e1) {
                LOG.error("Well that wasn't successful ...");
                e1.printStackTrace();
            }

            System.exit(-2);
        }

        //Start JavaFX and display the GUI
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        final String FXML_PATH = GlobalSettings.FXML_ROOT + FXML_FILE;

        //Load the UI from the Scene Builder fxml generated file
        final Pane root = FXMLLoader.load(getClass().getResource(FXML_PATH));
        final Scene scene = new Scene(root);

        //Init the scene and then show it to the sure
        stage.setScene(scene);
        stage.setTitle(APP_TITLE);
        stage.show();

        //Also make sure we hook up the Database to the onCloseRequest ...
        stage.setOnCloseRequest(windowEvent -> DatabaseService.destroyService());
    }
}
