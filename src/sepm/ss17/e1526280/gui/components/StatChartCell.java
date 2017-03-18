package sepm.ss17.e1526280.gui.components;

import javafx.geometry.Insets;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableCell;
import sepm.ss17.e1526280.dto.StatisticRow;

/**
 * This class does draw the Area Line chart into a Table cell.
 * The Day Data for the Chart is taken from the current Element
 * in the Table row
 *
 * @author Raphael Ludwig
 * @version 17.03.17
 */
public class StatChartCell extends TableCell<StatisticRow, Void> {
    private static final Double MAX_HEIGHT = 50.0D;

    private final NumberAxis yAxis = new NumberAxis();
    private final CategoryAxis xAxis = new CategoryAxis();
    private final AreaChart<String, Number> chart = new AreaChart<>(xAxis,yAxis);
    private final XYChart.Series<String, Number> series = new XYChart.Series<>();

    public StatChartCell() {
        yAxis.setStyle("-fx-padding: 0 0 0 0;");
        xAxis.setStyle("-fx-padding: 0 0 0 0;");
        chart.setStyle("-fx-padding: 0 0 0 -30px;");

        chart.setMaxHeight(MAX_HEIGHT);
        chart.setPrefHeight(MAX_HEIGHT);
        chart.setMinHeight(MAX_HEIGHT);

        chart.setPadding(Insets.EMPTY);
        chart.setAnimated(false);
        chart.setLegendVisible(false);

        chart.getYAxis().setTickLabelsVisible(false);
        chart.getYAxis().setTickMarkVisible(false);
        chart.getYAxis().setOpacity(0.0);
        chart.getXAxis().setTickLabelsVisible(false);
        chart.getXAxis().setTickMarkVisible(false);
        chart.getXAxis().setOpacity(0.0);

        chart.getData().add(series);
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        super.setMaxHeight(MAX_HEIGHT);

        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            final StatisticRow curObj = getTableView().getItems().get(getIndex());

            series.getData().clear();
            series.getData().add(new XYChart.Data<>("Mo",curObj.getMonday()));
            series.getData().add(new XYChart.Data<>("Di",curObj.getTuesday()));
            series.getData().add(new XYChart.Data<>("Mi",curObj.getWednesday()));
            series.getData().add(new XYChart.Data<>("Do",curObj.getThursday()));
            series.getData().add(new XYChart.Data<>("Fr",curObj.getFriday()));
            series.getData().add(new XYChart.Data<>("Sa",curObj.getSaturday()));
            series.getData().add(new XYChart.Data<>("So",curObj.getSunday()));

            setGraphic(chart);
            setText(null);
        }
    }
}
