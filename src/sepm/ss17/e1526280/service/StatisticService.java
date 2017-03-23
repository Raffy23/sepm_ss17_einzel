package sepm.ss17.e1526280.service;

import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.Reservation;
import sepm.ss17.e1526280.dto.StatisticRow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * This class represents a concrete implementation of the StatisticalService
 *
 * @author Raphael Ludwig
 * @version 16.03.17
 */
public class StatisticService implements StatisticalService {

    /** Constant of how many Days a week has **/
    private static final int DAYS = 7;

    /** The Service which is used to query data **/
    private final ReservationDataService service;

    /**
     * Creates the service which does query all the data from the service given as parameter
     * @param service which should be used to query the statistical data
     */
    public StatisticService(ReservationDataService service) {
        this.service = service;
    }

    @Override
    public CompletableFuture<List<StatisticRow>> query(List<Box> boxes, Date start, Date end) {
        return CompletableFuture.supplyAsync(() -> {
            final List<StatisticRow> outData = new ArrayList<>();

            // Loop though all boxes
            boxes.forEach(box -> {
                // Query data
                final List<Reservation> boxOut = service.queryFor(box,start,end).join();

                // If the box has reservations it is added to the Output data
                if( !boxOut.isEmpty() ) {
                    final int[] weekDayData = compileWeekdays(boxOut, start, end);
                    outData.add(new StatisticRow(box, boxOut, weekDayData));
                }

                //TODO: add also empty stat rows so we have also boxes there which do not have any data
            });

            return outData;
        });
    }

    /**
     * This function does calculate the difference from two dates in Days
     *
     * @param start start date which should be used
     * @param end end date which should be used
     * @return the number of days as integer
     */
    private static int getDayDiff(Date start, Date end) {
        return (int) (TimeUnit.DAYS.convert(end.getTime()-start.getTime(),TimeUnit.MILLISECONDS) + 1);
    }

    /**
     * Calculates the minimal date from the given two and does return it.
     *
     * @param d1 a date
     * @param d2 a date
     * @return the smaller date
     */
    private static Date getMinDate(Date d1, Date d2) {
        return d1.getTime() < d2.getTime() ? d1 : d2;
    }

    /**
     * Calculates the maximal date from the given two and does return it.
     *
     * @param d1 a date
     * @param d2 a date
     * @return the bigger date
     */
    private static Date getMaxDate(Date d1, Date d2) {
        return d1.getTime() > d2.getTime() ? d1 : d2;
    }

    /**
     * Calculates the number of days sorted in Bins (1 to DAYS) in which the box in the reservations was
     * reserved. All Reservations in the List must only contain one Box otherwise this function does not
     * provide a usefull output
     *
     * @param reservations a list of reservations of one Box
     * @param start start date
     * @param end end date
     * @return a integer array with the size of 8
     */
    private static int[] compileWeekdays(List<Reservation> reservations, Date start, Date end) {
        final int[] data = new int[DAYS+1];

        reservations.forEach(reservation -> {
            final Date realStart = getMaxDate(start, reservation.getStart());       // Get valid start date to count
            final Date realEnd   = getMinDate(end  , reservation.getEnd());         // Get valid end date to count
            final int dayDiff    = getDayDiff(realStart, realEnd);                  // Calc the day diff between start and end
            final int daysSpendOnWeekday = dayDiff / DAYS;                          // Calc some other useful stuff

            // If we haven't a whole week we have to add some days
            if( dayDiff < DAYS ) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime(realStart);

                // Walk from start date to end date and count the bins up
                final int startDay = cal.get(Calendar.DAY_OF_WEEK);
                for(int i=startDay; i<startDay+dayDiff; i++) {
                    data[i%(DAYS+1)]++;
                }

            } else {
                // Othweise add the days spend on weekday to every bin
                for (int i = 1; i <= DAYS; i++)
                    data[i] += daysSpendOnWeekday;

                final Calendar cal = Calendar.getInstance();
                cal.setTime(realStart);

                // Add the other days which are not in a whole week to the bins
                for(int i=cal.get(Calendar.DAY_OF_WEEK); i<daysSpendOnWeekday%DAYS; i++) {
                    data[i]++;
                }
            }
        });

        return data;
    }

}
