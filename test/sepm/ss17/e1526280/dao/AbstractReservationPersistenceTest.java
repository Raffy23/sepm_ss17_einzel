package sepm.ss17.e1526280.dao;

import org.junit.Assert;
import org.junit.Test;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesAlreadyExistException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.Reservation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Basic class which does perform some tests on the Reservation Persistence functions.
 *
 * @author Raphael Ludwig
 * @version 19.03.17
 */
public abstract class AbstractReservationPersistenceTest {

    protected final ReservationPersistenceDAO dao;
    private final BoxPersistenceDAO boxPersistenceDAO;
    private final Box dummyBox;

    protected AbstractReservationPersistenceTest(ReservationPersistenceDAO dao, BoxPersistenceDAO boxPersistenceDAO) throws ObjectDoesNotExistException, ObjectDoesAlreadyExistException {
        this.dao = dao;
        this.boxPersistenceDAO = boxPersistenceDAO;
        dummyBox = boxPersistenceDAO.query(1);
    }

    @Test
    public void insertOnly() throws ObjectDoesAlreadyExistException {
        dao.persist(new Reservation(1, dummyBox, new Date(), new Date(), "Customer 1", "Horse 1", 250.0f, false));
    }

    @Test(expected = ObjectDoesAlreadyExistException.class)
    public void insertDuplicate() throws ObjectDoesAlreadyExistException {
        dao.persist(new Reservation(1, dummyBox, new Date(), new Date(), "Customer 1", "Horse 1", 250.0f, false));
        dao.persist(new Reservation(1, dummyBox, new Date(), new Date(), "Customer 1", "Horse 1", 250.0f, false));
    }

    @Test(expected = ObjectDoesAlreadyExistException.class)
    public void insertOverlapingReservation() throws ObjectDoesAlreadyExistException {
        dao.persist(new Reservation(dummyBox, new Date(), new Date(), "Customer 1", "Horse 1", 250.0f));
        dao.persist(new Reservation(dummyBox, new Date(), new Date(), "Customer 3", "Horse 1", 250.0f));
    }

    @Test
    public void insertAutogenerateKey() throws ObjectDoesAlreadyExistException, ObjectDoesNotExistException {
        final Box dummy2 = boxPersistenceDAO.query(2);

        dao.persist(new Reservation(dummyBox, new Date(), new Date(), "Customer 1", "Horse 1", 250.0f));
        dao.persist(new Reservation(dummy2, new Date(), new Date(), "Customer 3", "Horse 1", 250.0f));
    }

    @Test
    public void checkInsertedValues() throws ObjectDoesAlreadyExistException {
        final Date curDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        final Reservation data = new Reservation(1, dummyBox, curDate, curDate, "Customer 1", "Horse 1", 250.0f, false);
        dao.persist(data);

        final List<Reservation> readBack = dao.query(new HashMap<String,Object>(){
            {
                put(ReservationPersistenceDAO.QUERY_PARAM_ID, 1);
            }
        });

        Assert.assertTrue(readBack.size() == 1);
        Assert.assertEquals(data, readBack.get(0));
    }

    @Test
    public void insertMultipleOverTimeSpan() throws ObjectDoesAlreadyExistException {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        final Date start1 = cal.getTime();
        cal.add(Calendar.DATE, 1);
        final Date end1   = cal.getTime();
        cal.add(Calendar.DATE, 1);

        final Date start2 = cal.getTime();
        cal.add(Calendar.DATE, 1);
        final Date end2   = cal.getTime();

        dao.persist(new Reservation(dummyBox, start1, end1, "Customer 1", "Horse 1", 250.0f));
        dao.persist(new Reservation(dummyBox, start2, end2, "Customer 2", "Horse 2", 250.0f));
    }

    @Test
    public void testRemoveSingle() throws ObjectDoesNotExistException, ObjectDoesAlreadyExistException {
        final Reservation data = new Reservation(1, dummyBox, new Date(), new Date(), "Customer 1", "Horse 1", 250.0f, false);
        dao.persist(data);
        dao.remove(data);
    }

    @Test(expected = ObjectDoesNotExistException.class)
    public void testRemoveFailSingle() throws ObjectDoesNotExistException {
        final Reservation data = new Reservation(1, dummyBox, new Date(), new Date(), "Customer 1", "Horse 1", 250.0f, false);
        dao.remove(data);
    }

    private List<Reservation> generateReservations(int count) throws ObjectDoesNotExistException {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);


        final List<Reservation> data = new ArrayList<>();
        for(int i=0;i <count; i++) {
            final Date start1 = cal.getTime();
            cal.add(Calendar.DATE, 1);
            final Date end1   = cal.getTime();
            cal.add(Calendar.DATE, 1);

            final Box box = boxPersistenceDAO.query(i+1);

            System.out.println(box);
            data.add(new Reservation(box, start1, end1, "Customer " + i, "Horse " + i, 250.0f));
        }

        return data;
    }

    @Test
    public void insertMultiple() throws ObjectDoesNotExistException, ObjectDoesAlreadyExistException {
        System.out.println(dao.queryAll());

        final List<Reservation> data = generateReservations(25);
        dao.persist(data);
    }

    @Test
    public void testRemoveMultiple() throws ObjectDoesNotExistException, ObjectDoesAlreadyExistException {
        final List<Reservation> data = generateReservations(20);
        for(Reservation reservation:data)
            dao.persist(reservation);

        dao.remove(data);
        Assert.assertTrue(dao.queryAll().size() == 0);
    }

    @Test
    public void testBoxReservedPositive() throws ObjectDoesNotExistException, ObjectDoesAlreadyExistException {
        final List<Reservation> data = generateReservations(5);
        dao.persist(data);

        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        final Date start = cal.getTime();
        cal.add(Calendar.DATE, 3);

        Assert.assertTrue(dao.isBoxReserved(dummyBox, start, cal.getTime()));

        cal.add(Calendar.DATE, 8);
        final Date end = cal.getTime();
        Assert.assertTrue(dao.isBoxReserved(dummyBox, start, end));

        cal.add(Calendar.DATE, -10);
        final Date before = cal.getTime();
        Assert.assertTrue(dao.isBoxReserved(dummyBox, before, end));
    }

    @Test
    public void testBoxReservedNegative() throws ObjectDoesNotExistException, ObjectDoesAlreadyExistException {
        final List<Reservation> data = generateReservations(2);
        dao.persist(data);

        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.DATE, -8);
        final Date start = cal.getTime();
        cal.add(Calendar.DATE, 2);
        final Date end = cal.getTime();

        Assert.assertFalse(dao.isBoxReserved(dummyBox, start, end));
        Assert.assertFalse(dao.isBoxReserved(boxPersistenceDAO.query(200),start, end));
    }
}
