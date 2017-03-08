package sepm.ss17.e1526280.util;

/**
 * This Class provides some utility function to check the Version of the
 * current JVM
 *
 * @author Raphael Ludwig
 * @version 08.03.17
 */
public class JavaVersionChecker {

    /**
     * Checks the Version if it is newer equal to the major, minor numbers
     * @param major major version of java (e.g. 7 or 8)
     * @param minor minor version of java, which means the update
     * @return true if it is greater equal false otherwise
     */
    public static boolean checkJavaVersion(int major, int minor) {
        final String[] version = System.getProperty("java.version").split("\\.");
        final String[] patchset = version[2].split("_");

        return Integer.valueOf(version[1]) >= major && Integer.valueOf(patchset[1]) >= minor;
    }

}
