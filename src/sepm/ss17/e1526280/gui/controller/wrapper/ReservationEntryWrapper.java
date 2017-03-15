package sepm.ss17.e1526280.gui.controller.wrapper;

import sepm.ss17.e1526280.dto.Reservation;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 14.03.17
 */
public class ReservationEntryWrapper extends Reservation {

    private final int days;

    public ReservationEntryWrapper(Reservation res, int days) {
        super(res.getId(), res.getBox(), res.getStart(), res.getEnd(), res.getCustomer(), res.getHorse(), res.getPrice(), res.isAlreadyInvoice());
        this.days = days;
    }

    public float getPriceSum() {
        return days * this.getPrice();
    }

    public Reservation toReservation() {
        return this;
    }

}
