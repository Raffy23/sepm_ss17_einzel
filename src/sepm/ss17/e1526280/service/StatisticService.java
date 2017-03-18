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
 * Created by
 *
 * @author Raphael Ludwig
 * @version 16.03.17
 */
public class StatisticService implements StatisticalService {

    private static final int DAYS = 7;
    private final ReservationDataService service;


    public StatisticService(ReservationDataService service) {
        this.service = service;
    }

    @Override
    public CompletableFuture<List<StatisticRow>> query(List<Box> boxes, Date start, Date end) {
        return CompletableFuture.supplyAsync(() -> {
            final List<StatisticRow> outData = new ArrayList<>();

            boxes.forEach(box -> {
                final List<Reservation> boxOut = service.queryFor(box,start,end).join();

                if( !boxOut.isEmpty() ) {
                    final int[] weekDayData = compileWeekdays(boxOut, start, end);
                    outData.add(new StatisticRow(box, weekDayData));
                }
            });

            return outData;
        });
    }


    private static int getDayDiff(Date start, Date end) {
        return (int) (TimeUnit.DAYS.convert(end.getTime()-start.getTime(),TimeUnit.MILLISECONDS) + 1);
    }

    private static Date getMinDate(Date d1, Date d2) {
        return d1.getTime() < d2.getTime() ? d1 : d2;
    }

    private static Date getMaxDate(Date d1, Date d2) {
        return d1.getTime() > d2.getTime() ? d1 : d2;
    }

    private static int[] compileWeekdays(List<Reservation> reservations, Date start, Date end) {
        final int[] data = new int[DAYS+1];

        reservations.forEach(reservation -> {
            final Date realStart = getMaxDate(start, reservation.getStart());
            final Date realEnd   = getMinDate(end  , reservation.getEnd());
            final int dayDiff    = getDayDiff(realStart, realEnd);
            final int daysSpendOnWeekday = dayDiff / DAYS;

            if( dayDiff < DAYS ) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime(realStart);

                final int startDay = cal.get(Calendar.DAY_OF_WEEK);
                for(int i=startDay; i<startDay+dayDiff; i++) {
                    data[i%(DAYS+1)]++;
                }

            } else
                for(int i=1;i<=DAYS; i++)
                    data[i] += daysSpendOnWeekday;

            if( dayDiff >= DAYS ) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime(realStart);

                for(int i=cal.get(Calendar.DAY_OF_WEEK); i<daysSpendOnWeekday%DAYS; i++) {
                    data[i]++;
                }
            }
        });

        return data;
    }

}
