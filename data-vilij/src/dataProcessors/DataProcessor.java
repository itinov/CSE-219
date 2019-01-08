package dataProcessors;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import settings.AppPropertyTypes;
import vilij.propertymanager.PropertyManager;

import java.util.HashMap;
import java.util.HashSet;
/**
 *
 * @author Weixin Tan
 */
public interface DataProcessor{
    default void toChartData(Data InputData, XYChart<Number, Number> chart){
        HashMap<String, String> dataLabels = InputData.getDataLabels();
        HashMap<String, Point2D> dataPoints =InputData.getDataPoints();
        HashSet<String> labels =InputData.getLabels();
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);

            dataLabels.entrySet().stream().filter(entry -> entry.getValue()!=null&& entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY(),entry.getKey()));
            });
            chart.getData().add(series);
            series.getNode().setId(PropertyManager.getManager().getPropertyValue(AppPropertyTypes.CSS_LINE_ID.name()));
            for(XYChart.Data<Number,Number> data : series.getData()){
                Tooltip.install(data.getNode(),new Tooltip(data.getExtraValue().toString()));
                data.getNode().setCursor(Cursor.CROSSHAIR);
            }
        }
        XYChart.Series<Number,Number> series = new XYChart.Series<>();
        dataLabels.entrySet().stream().filter(entry-> entry.getValue()==null).forEach(entry -> {
            Point2D point = dataPoints.get(entry.getKey());
            series.getData().add(new XYChart.Data<>(point.getX(), point.getY(),entry.getKey()));
        });

        if(!series.getData().isEmpty()){
            chart.getData().add(series);
            series.setName(PropertyManager.getManager().getPropertyValue(AppPropertyTypes.NULL_LABEL.name()));
            series.getNode().setId(PropertyManager.getManager().getPropertyValue(AppPropertyTypes.CSS_LINE_ID.name()));
            for(XYChart.Data<Number,Number> data : series.getData()){
                Tooltip.install(data.getNode(),new Tooltip(data.getExtraValue().toString()));
                data.getNode().setCursor(Cursor.CROSSHAIR);
            }
        }
    }
    void update();

    boolean CheckState();

    void terminate();

}
