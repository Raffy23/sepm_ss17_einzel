package sepm.ss17.e1526280.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * This Class provides the global Application configuration after the initialize function was called.
 * There is also other Stuff provided by this class like unique settings like the FXML_ROOT path and so on.
 *
 * @author Raphael Ludwig
 * @version 08.03.17
 */
public class GlobalSettings {

    /** Logger for logging ... duh **/
    private static final Logger LOG = LoggerFactory.getLogger(GlobalSettings.class);

    /** The Standard configuration file **/
    private static final String CONFIG_FILE_PATH = "./config.properties";

    /** Config is going the be here ... **/
    private static final Properties config = new Properties();

    /** The Path where every FXML Document is saved **/
    public static String FXML_ROOT = "/sepm/ss17/e1526280/gui/resources/";

    /**
     * Initialises the Config with the file in the parameter
     * @param targetConfig path to a valid properties file
     */
    public static void initialize(String targetConfig) {
        LOG.debug("Loading configuration file from: " + targetConfig);

        try {
            config.load(new FileReader(new File(targetConfig)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the Service with the Standard configuration file (./config.properties)
     */
    public static void initialize() {
        initialize(CONFIG_FILE_PATH);
    }

    /**
     * Exposes the config to other classes
     * @return the configuration of the Application, might be empty if initialized was not called
     */
    public static Properties getConfig() {
        return config;
    }

    /**
     * Empty private constructor so we don't have useless Object in Heap in case of missuses
     */
    private GlobalSettings() {}

}
