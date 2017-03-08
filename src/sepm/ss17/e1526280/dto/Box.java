package sepm.ss17.e1526280.dto;

import java.io.Serializable;

/**
 * TODO: Comments
 * @author Raphael Ludwig
 * @version 08.03.17
 */
public class Box implements Serializable {

    private int boxID;

    private float price;
    private float size;
    private LitterType litter;
    private boolean window;
    private boolean indoor;
    private String photo;
    private boolean deleted = false;

    public Box(int id, float price, float size, LitterType litter, boolean window, boolean indoor, String photo) {
        boxID = id;
        this.price = price;
        this.size = size;
        this.litter = litter;
        this.window = window;
        this.indoor = indoor;
        this.photo = photo;
    }

    public Box(int id, float price, float size, LitterType litter, boolean window, boolean indoor, String photo, boolean deleted) {
        boxID = id;
        this.price = price;
        this.size = size;
        this.litter = litter;
        this.window = window;
        this.indoor = indoor;
        this.photo = photo;
        this.deleted = deleted;
    }

    public Box(Box other) {
        this.boxID = other.boxID;
        this.price = other.price;
        this.size = other.size;
        this.litter = other.litter;
        this.window = other.window;
        this.indoor = other.indoor;
        this.photo = other.photo;
        this.deleted = other.deleted;
    }

    public void updateDataFrom(Box other) {
        this.price = other.price;
        this.size = other.size;
        this.litter = other.litter;
        this.window = other.window;
        this.indoor = other.indoor;
        this.photo = other.photo;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public LitterType getLitter() {
        return litter;
    }

    public void setLitter(LitterType litter) {
        this.litter = litter;
    }

    public boolean isWindow() {
        return window;
    }

    public void setWindow(boolean window) {
        this.window = window;
    }

    public boolean isIndoor() {
        return indoor;
    }

    public void setIndoor(boolean indoor) {
        this.indoor = indoor;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getBoxID() {
        return boxID;
    }

    public void setBoxID(int boxID) {
        this.boxID = boxID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Box box = (Box) o;

        if (boxID != box.boxID) return false;
        if (Float.compare(box.price, price) != 0) return false;
        if (Float.compare(box.size, size) != 0) return false;
        if (window != box.window) return false;
        if (indoor != box.indoor) return false;
        if (litter != box.litter) return false;
        return photo != null ? photo.equals(box.photo) : box.photo == null;
    }

    @Override
    public int hashCode() {
        int result = boxID;
        result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
        result = 31 * result + (size != +0.0f ? Float.floatToIntBits(size) : 0);
        result = 31 * result + (litter != null ? litter.hashCode() : 0);
        result = 31 * result + (window ? 1 : 0);
        result = 31 * result + (indoor ? 1 : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Box{" +
                "boxID=" + boxID +
                ", price=" + price +
                ", size=" + size +
                ", litter=" + litter +
                ", window=" + window +
                ", indoor=" + indoor +
                ", photo='" + photo + '\'' +
                '}';
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
