package sepm.ss17.e1526280.dao;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import sepm.ss17.e1526280.dao.exceptions.CheckedDatabaseException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesAlreadyExistException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
import sepm.ss17.e1526280.dao.h2.H2BoxDatabaseDAO;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.LitterType;
import sepm.ss17.e1526280.util.DatabaseService;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 04.03.17
 */
public class BoxPersistenceTest {

    private static BoxPersistenceDAO dao;

    @BeforeClass
    public static void setUp()  {
        GlobalSettings.initialize("./config.junit.properties");
        try {
            DatabaseService.initialize();
        } catch (CheckedDatabaseException e) {
            throw new RuntimeException(e);
        }

        dao = new H2BoxDatabaseDAO();
    }

    @Test
    public void insertNewBox() throws ObjectDoesAlreadyExistException {
        dao.persist(new Box(23f,23f, LitterType.Sawdust,true,false,null));
    }

    @Test(expected = ObjectDoesAlreadyExistException.class)
    public void insertCollision() throws ObjectDoesAlreadyExistException {
        dao.persist(new Box(99,23f,23f, LitterType.Sawdust,true,false,null,false));
        dao.persist(new Box(99,23f,23f, LitterType.Sawdust,true,false,null,false));
        Assert.fail();
    }

    @Test
    public void insertAndRead() throws ObjectDoesAlreadyExistException {
        final Box target = new Box(23f,23f, LitterType.Sawdust,true,false,null);
        dao.persist(target);
        List<Box> box = dao.query(new HashMap<String,Object>(){
            {this.put(H2BoxDatabaseDAO.QUERY_PARAM_BOX_ID,target.getBoxID());}
        });

        Assert.assertEquals(1,box.size());
        Assert.assertEquals(box.get(0),target);
    }

    @Test
    public void insertAndMerge() throws ObjectDoesAlreadyExistException, ObjectDoesNotExistException {
        final Box target = new Box(23.0f,23.0f, LitterType.Sawdust,true,false,null);
        dao.persist(target);

        target.setPrice(1000.0f);
        target.setSize(10.0f);

        dao.merge(target);
        final Box box = dao.query(target.getBoxID());

        Assert.assertEquals(box,target);
    }

    @Test
    public void persistMultiple() throws ObjectDoesAlreadyExistException, ObjectDoesNotExistException {
        final List<Box> boxes = new ArrayList<>();
        final Random rand = ThreadLocalRandom.current();
        for(int i=0; i<=10; i++) {
            boxes.add(new Box(rand.nextFloat(),rand.nextFloat(), LitterType.Sawdust,rand.nextBoolean(),rand.nextBoolean(),null));
        }

        dao.persist(boxes);
    }

    @Test(expected = ObjectDoesAlreadyExistException.class)
    public void persistMultipleFail() throws ObjectDoesAlreadyExistException, ObjectDoesNotExistException {
        final List<Box> boxes = new ArrayList<>();
        final Random rand = ThreadLocalRandom.current();
        for(int i=0; i<=10; i++) {
            boxes.add(new Box(rand.nextFloat(),rand.nextFloat(), LitterType.Sawdust,rand.nextBoolean(),rand.nextBoolean(),null));
        }

        dao.persist(boxes);
        dao.persist(boxes);
    }

    @Test
    public void testRemove() throws ObjectDoesAlreadyExistException, ObjectDoesNotExistException {
        final Box target = new Box(23.0f,23.0f, LitterType.Sawdust,true,false,null);
        dao.persist(target);
        dao.remove(target);

        List<Box> box = dao.query(new HashMap<String,Object>(){
            {this.put(H2BoxDatabaseDAO.QUERY_PARAM_BOX_ID,target.getBoxID());}
        });

        Assert.assertEquals(0,box.size());
    }

    @Test(expected = ObjectDoesNotExistException.class)
    public void tryRemove() throws ObjectDoesAlreadyExistException, ObjectDoesNotExistException {
        final Box target = new Box(23.0f,23.0f, LitterType.Sawdust,true,false,null);
        dao.persist(target);

        final int oldID = target.getBoxID();
        target.setBoxID(10000);
        dao.remove(target);
    }

    @Test(expected = ObjectDoesNotExistException.class)
    public void mergeFailTest() throws ObjectDoesAlreadyExistException, ObjectDoesNotExistException {
        final Box target = new Box(23.0f,23.0f, LitterType.Sawdust,true,false,null);
        dao.persist(target);

        target.setBoxID(10000);
        target.setPrice(1000.0f);
        target.setSize(10.0f);

        dao.merge(target);
        Assert.fail();
    }




    @AfterClass
    public static void turnDown() {
        try {
            DatabaseService.getManager().executeSQLFile("sql/drop.sql");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        DatabaseService.destroyService();
        try {
            DatabaseService.deleteDatabaseFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
