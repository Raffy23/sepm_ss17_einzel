package sepm.ss17.e1526280.dto;

import java.util.Calendar;
import java.util.List;

/**
 * This DTO does contain some Data for the Statistics
 *
 * @author Raphael Ludwig
 * @version 16.03.17
 */
public class StatisticRow {

    public final static int DAYS = 8;


    private final Box box;
    private final List<Reservation> reservationList;
    private final int[] dayCount;

    public StatisticRow(Box box, List<Reservation> reservationList, int[] dayCount) {
        this.box = box;
        this.reservationList = reservationList;
        this.dayCount = dayCount;
    }

    public int getCount() {
        int cnt = 0;
        for(int i=1;i<DAYS;i++)
            cnt += dayCount[i];

        return cnt;
    }

    public int getMonday() {
        return dayCount[Calendar.MONDAY];
    }

    public int getTuesday() {
        return dayCount[Calendar.TUESDAY];
    }

    public int getWednesday() {
        return dayCount[Calendar.WEDNESDAY];
    }

    public int getThursday() {
        return dayCount[Calendar.THURSDAY];
    }

    public int getFriday() {
        return dayCount[Calendar.FRIDAY];
    }

    public int getSaturday() {
        return dayCount[Calendar.SATURDAY];
    }

    public int getSunday() {
        return dayCount[Calendar.SUNDAY];
    }

    public Box getBox() {
        return this.box;
    }

    public int getBoxID() {
        return box.getBoxID();
    }

    public int[] getData() {
        return dayCount;
    }

    public List<Reservation> getReservationList() {
        return reservationList;
    }
}
