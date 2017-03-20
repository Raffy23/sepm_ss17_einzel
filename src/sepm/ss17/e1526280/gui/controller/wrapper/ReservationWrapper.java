package sepm.ss17.e1526280.gui.controller.wrapper;

import sepm.ss17.e1526280.dto.Reservation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Helper class for the Controller of the Reservations
 *
 * @author Raphael Ludwig
 * @version 14.03.17
 */
public class ReservationWrapper  {

    private static final SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");

    private final String name;
    private final List<Reservation> boxes;
    private final Date start;
    private final Date end;

    public ReservationWrapper(List<Reservation> boxes) {
        this.boxes = boxes;
        this.name = boxes.get(0).getCustomer();
        this.start = boxes.get(0).getStart();
        this.end = boxes.get(0).getEnd();
    }

    public float getPrice() {
        return (float) boxes.stream().mapToDouble(reservation -> reservation.getPrice() * getDays()).sum();
    }

    public int getDays() {
        return (int) (TimeUnit.DAYS.convert(end.getTime()-start.getTime(),TimeUnit.MILLISECONDS) + 1);
    }

    public int getID() {
        return boxes.get(0).getId();
    }

    public int getCount() {
        return boxes.size();
    }

    public String getName() {
        return name;
    }

    public String getStartString() {
        return fmt.format(start);
    }

    public String getEndString() {
        return fmt.format(end);
    }

    public List<Reservation> getBoxes() {
        return boxes;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }
}
