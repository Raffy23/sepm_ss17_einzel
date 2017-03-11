package sepm.ss17.e1526280.dao;

import sepm.ss17.e1526280.dao.exceptions.ObjectDoesAlreadyExistException;
import sepm.ss17.e1526280.dto.Reservation;

import java.util.List;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 11.03.17
 */
public interface ReservationPersistenceDAO extends PersistenceDAO<Reservation> {

    void persist(List<Reservation> o) throws ObjectDoesAlreadyExistException;


}
