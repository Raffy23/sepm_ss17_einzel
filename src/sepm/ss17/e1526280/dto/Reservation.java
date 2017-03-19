package sepm.ss17.e1526280.dto;

import java.util.Date;

/**
 * Reservations DTO which contains only auto-generated methods
 *
 * @author Raphael Ludwig
 * @version 04.03.17
 */
public class Reservation {

    //Primary Key in Database
    private int id = -1;

    private Box box;
    private Date start;
    private Date end;
    private String customer;
    private String horse;
    private float price;
    private boolean alreadyInvoice = false;

    public Reservation(int id, Box box, Date start, Date end, String customer, String horse, float price, boolean alreadyInvoice) {
        this.id = id;
        this.box = box;
        this.start = start;
        this.end = end;
        this.customer = customer;
        this.horse = horse;
        this.alreadyInvoice = alreadyInvoice;
        this.price = price;
    }

    public Reservation(Box box, Date start, Date end, String customer, String horse, float price) {
        this.box = box;
        this.start = start;
        this.end = end;
        this.customer = customer;
        this.horse = horse;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }

    public int getBoxId() {
        return box.getBoxID();
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getHorse() {
        return horse;
    }

    public void setHorse(String horse) {
        this.horse = horse;
    }

    public boolean isAlreadyInvoice() {
        return alreadyInvoice;
    }

    public void setAlreadyInvoice(boolean alreadyInvoice) {
        this.alreadyInvoice = alreadyInvoice;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reservation that = (Reservation) o;

        if (id != that.id) return false;
        if (Float.compare(that.price, price) != 0) return false;
        if (alreadyInvoice != that.alreadyInvoice) return false;
        if (box != null ? !box.equals(that.box) : that.box != null) return false;
        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        if (end != null ? !end.equals(that.end) : that.end != null) return false;
        if (customer != null ? !customer.equals(that.customer) : that.customer != null) return false;
        return horse != null ? horse.equals(that.horse) : that.horse == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (box != null ? box.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (customer != null ? customer.hashCode() : 0);
        result = 31 * result + (horse != null ? horse.hashCode() : 0);
        result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
        result = 31 * result + (alreadyInvoice ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", box=" + box +
                ", start=" + start +
                ", end=" + end +
                ", customer='" + customer + '\'' +
                ", horse='" + horse + '\'' +
                ", price=" + price +
                ", alreadyInvoice=" + alreadyInvoice +
                '}';
    }
}
