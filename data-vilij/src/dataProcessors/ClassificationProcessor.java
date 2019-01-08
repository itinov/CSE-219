package dataProcessors;


import Algorithm.ClassificationAlgorithm;
import Algorithm.Configuration;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import java.util.List;
/**
 *
 * @author Weixin Tan
 */
public class ClassificationProcessor extends Thread implements DataProcessor{
    private ClassificationAlgorithm classificationAlgorithm;
    private Configuration configuration;
    private ApplicationTemplate applicationTemplate;
    private int currentIteration;
    private XYChart<Number, Number> chart;
    private int chartOriginalDataSize;
    private double MaxX, MinX;
    private boolean running;
    private Data data;
    public ClassificationProcessor(ClassificationAlgorithm classificationAlgorithm,
                                   XYChart<Number,Number> chart,
                                   ApplicationTemplate applicationTemplate,
                                   double MaxX, double MinX, Data data){
        this.classificationAlgorithm=classificationAlgorithm;
        this.configuration=classificationAlgorithm.getConfiguration();
        this.applicationTemplate=applicationTemplate;
        this.chart=chart;
        running=true;
        this.data=data;
        this.MaxX= MaxX;
        this.MinX= MinX;
        if(MaxX==MinX){
            this.MinX=MinX-10;
            this.MaxX=MaxX+10;
        }
        currentIteration=1;
    }

    public void setChartOriginalDataSize(){
        chartOriginalDataSize=chart.getData().size();
    }
    @Override
    public void run() {
        update();
        ((AppUI)applicationTemplate.getUIComponent()).disableScrnShotButton(false);
    }

    @Override
    public void update() {
        if(configuration.continous){
            for(int i=1;i<=configuration.MaxInterval;i++){
                classificationAlgorithm.run();
                if(i%configuration.IterationInterval==0) {
                    currentIteration=i;
                    List<Integer> list=classificationAlgorithm.getOutput();
                    display(getLinePoints(list));
                    try {
                        sleep(2000);
                        if(!running) {
                            Platform.runLater(()-> ((AppUI)applicationTemplate.getUIComponent()).clearChart());
                            break;
                        }
                    } catch (InterruptedException e) {
                        //do nothing2
                    }
                }
            }
        }else{
            for(int i=1;i<=configuration.MaxInterval;i++){
                classificationAlgorithm.run();
                if(i%configuration.IterationInterval==0) {
                    currentIteration=i;
                    List<Integer> list=classificationAlgorithm.getOutput();
                    display(getLinePoints(list));
                    try {
                        synchronized (this) {
                            wait();
                        }
                    } catch (InterruptedException e) {
                        //If button is click it will resume
                        if(!running) {
                            Platform.runLater(()-> ((AppUI)applicationTemplate.getUIComponent()).clearChart());
                            break;
                        }
                    }
                }
            }
        }
    }
    private Point2D[] getLinePoints(List<Integer> list){
        Point2D[] point2DS = new Point2D[2];
        double denominator =-1.0*list.get(1);
        point2DS[0]=new Point2D(MinX,(MinX*list.get(0)+list.get(2))/denominator);
        point2DS[1]=new Point2D(MaxX,(MaxX*list.get(0)+list.get(2))/denominator);
        return point2DS;
    }
    private void display(Point2D[] point2DS){
        AppUI ui= (AppUI)applicationTemplate.getUIComponent();
        XYChart.Series<Number,Number> series = new XYChart.Series<>();

        Platform.runLater(()->{
            chart.setAnimated(true);
            if(chartOriginalDataSize!=chart.getData().size()&&chart.getData().size()>0) {
                chart.setAnimated(false);
                chart.getData().remove(chart.getData().size() - 1);
            }
            series.getData().add(new XYChart.Data<>(point2DS[0].getX(),point2DS[0].getY()));
            series.getData().add(new XYChart.Data<>(point2DS[1].getX(),point2DS[1].getY()));
            chart.getData().add(series);
            series.setName(PropertyManager.getManager().getPropertyValue(AppPropertyTypes.RANDOM_CLASSIFICATION.name()));
            ui.showDisplayPane(configuration.continous);
            ui.setRunInfo(currentIteration,configuration.MaxInterval);
            ui.disableScrnShotButton(configuration.continous);
            for (XYChart.Data<Number,Number> data: series.getData()) {
                data.getNode().setVisible(false);
            }
            if(currentIteration+configuration.IterationInterval>configuration.MaxInterval){
                if(!configuration.continous)
                    ui.getNext().setVisible(false);
                ui.getCancel().setText(PropertyManager.getManager().getPropertyValue(AppPropertyTypes.COMPLETE_BUTTON_LABEL.name()));
            }
        });
    }

    @Override
    public boolean CheckState(){
        return isAlive();
    }
    public void terminate(){
        running=false;
    }

}
