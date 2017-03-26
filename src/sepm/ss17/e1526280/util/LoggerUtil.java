package sepm.ss17.e1526280.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.LoggerFactory;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 26.03.17
 */
public class LoggerUtil {

    public static void initalizeLogger() {
        //Honor System set config File Path for Logback (useful for debugging)
        if( System.getProperty("logback.configurationFile") == null ) {
            if (GlobalSettings.getConfig().getProperty("logback.config") != null) {
                System.setProperty("logback.configurationFile", GlobalSettings.getConfig().getProperty("logback.config"));
                final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
                try {
                    JoranConfigurator configurator = new JoranConfigurator();
                    configurator.setContext(context);
                    context.reset();
                    configurator.doConfigure(GlobalSettings.getConfig().getProperty("logback.config"));
                } catch (JoranException je) {
                    je.printStackTrace();
                    System.err.println("Unable to change logger Context Framework ...");
                }
            }
        }
    }

}
