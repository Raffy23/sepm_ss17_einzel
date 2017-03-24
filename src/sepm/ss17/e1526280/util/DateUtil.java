package sepm.ss17.e1526280.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * A Util which does handle some Date conversations
 *
 * @author Raphael Ludwig
 * @version 24.03.17
 */
public class DateUtil {

    /**
     * Converts a LocalDate to java.util.date
     * @param date local date
     * @return java.util.Date with the current time zone
     */
    public static Date fromLocalDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
