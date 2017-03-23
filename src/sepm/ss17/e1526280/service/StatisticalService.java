package sepm.ss17.e1526280.service;

import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.StatisticRow;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This interface does Provide the basic functionality for the statistical methods
 *
 * @author Raphael Ludwig
 * @version 16.03.17
 */
public interface StatisticalService {

    /**
     * Queries for the list of Boxes all statistic rows form the start to the end date
     * @param boxes list of boxes which should be queried
     * @param start start date
     * @param end end date
     * @return a future with the list of the results
     */
    CompletableFuture<List<StatisticRow>> query(List<Box> boxes, Date start, Date end);

}
