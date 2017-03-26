package sepm.ss17.e1526280.dao;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sepm.ss17.e1526280.dao.exceptions.CheckedDatabaseException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.LitterType;
import sepm.ss17.e1526280.dto.Range;
import sepm.ss17.e1526280.dto.Reservation;
import sepm.ss17.e1526280.dto.StatisticRow;
import sepm.ss17.e1526280.service.StatisticalService;
import sepm.ss17.e1526280.service.provider.SimpleServiceProvider;
import sepm.ss17.e1526280.util.DataServiceManager;
import sepm.ss17.e1526280.util.DatabaseService;
import sepm.ss17.e1526280.util.datasource.DatabaseSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 26.03.17
 */
public class StatisticServiceTest {

    private static StatisticalService service;
    private static final Box bb = new Box(99, 23.0f, 23.0f, LitterType.Sawdust,true,false,null,false);
    private static final Box wb = new Box(100, 1.0f, 3.0f, LitterType.Sawdust,true,false,null,false);

    @Test
    public void testGetBest() throws ObjectDoesNotExistException, ExecutionException, InterruptedException {
        final List<Reservation> bbR = generateReservations(7, bb);
        final List<Reservation> wbR = generateReservations(1, wb);

        for(Reservation r:bbR) {
            DataServiceManager.getService().getReservationDataService().persist(r).join();
        }
        for(Reservation r:wbR) {
            DataServiceManager.getService().getReservationDataService().persist(r).join();
        }

        final List<Box> sData = new ArrayList<>();
        sData.add(bb);
        sData.add(wb);

        final List<StatisticRow> statData = service.query(sData).join();
        Assert.assertEquals(bb, service.getBest(statData, Range.Gesamt));
        Assert.assertEquals(bb, service.getBest(statData, Range.Montag));
        Assert.assertEquals(bb, service.getBest(statData, Range.Dienstag));
        Assert.assertEquals(bb, service.getBest(statData, Range.Mittwoch));
        Assert.assertEquals(bb, service.getBest(statData, Range.Donnerstag));
        Assert.assertEquals(bb, service.getBest(statData, Range.Freitag));
    }

    @Test
    public void testGetWorst() throws ObjectDoesNotExistException, ExecutionException, InterruptedException {
        final List<Reservation> bbR = generateReservations(8, bb);
        final List<Reservation> wbR = generateReservations(1, wb);

        for(Reservation r:bbR) {
            DataServiceManager.getService().getReservationDataService().persist(r).join();
        }
        for(Reservation r:wbR) {
            DataServiceManager.getService().getReservationDataService().persist(r).join();
        }

        final List<Box> sData = new ArrayList<>();
        sData.add(bb);
        sData.add(wb);

        final List<StatisticRow> statData = service.query(sData).get();
        Assert.assertEquals(wb, service.getWorst(statData, Range.Gesamt));
        Assert.assertEquals(wb, service.getWorst(statData, Range.Montag));
        Assert.assertEquals(wb, service.getWorst(statData, Range.Dienstag));
        Assert.assertEquals(wb, service.getWorst(statData, Range.Mittwoch));
        Assert.assertEquals(wb, service.getWorst(statData, Range.Donnerstag));
        Assert.assertEquals(wb, service.getWorst(statData, Range.Freitag));
    }

    @Before
    public void isolateTest() throws SQLException {
        DatabaseService.getManager().getConnection().setAutoCommit(false);
    }

    @After
    public void removeDataAfterTest() throws SQLException {
        DatabaseService.getManager().getConnection().rollback();
    }

    @BeforeClass
    public static void setUp() throws CheckedDatabaseException {
        DatabaseTestDataProvider.setUp();
        DataServiceManager.initialize(new DatabaseSource(), new SimpleServiceProvider());
        service = DataServiceManager.getService().getStatisticalService();

        // Seed some Data:
        DataServiceManager.getService().getBoxDataService().persist(bb).join();
        DataServiceManager.getService().getBoxDataService().persist(wb).join();
    }

    @AfterClass
    public static void turnDown() {
        DatabaseTestDataProvider.turnDown();
    }

    private static List<Reservation> generateReservations(int count, Box box) throws ObjectDoesNotExistException {
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

            data.add(new Reservation(box, start1, end1, "Customer " + count, "Horse " + i, 250.0f));
        }

        return data;
    }
}
