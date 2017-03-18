package sepm.ss17.e1526280.service;

import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.StatisticRow;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 16.03.17
 */
public interface StatisticalService {

    CompletableFuture<List<StatisticRow>> query(List<Box> boxes, Date start, Date end);

}
