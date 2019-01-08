package ui;

import Algorithm.KMeansClusterer;
import Algorithm.RandomClassification;
import Algorithm.RandomClustering;
import dataProcessors.Data;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ConfigurationTest {
    private AppUI                               ui;
    private Stage                               stage;

    private String []                           maxIteration;
    private String []                           IterationInterval;
    private String []                           numberOfClustering;

    private RandomClassification                randomClassification;
    private RandomClustering                    randomClustering;
    private KMeansClusterer                     kMeansClusterer;

    private Method                              ConfigurationAction;

    /**
     * Test the whole ConfgiurationAction method of AppUi, which
     * checks the validity of the user input configuration
     */
    @Before
    public void setUpClass(){
        ui=new AppUI();
        for(Method m:ui.getClass().getDeclaredMethods() ){
            if(m.getName().equals("ConfigurationAction")) {
                ConfigurationAction = m;
                break;
            }
        }
        ConfigurationAction.setAccessible(true);

        maxIteration=new String[]{"-10","0","15","500"};
        IterationInterval=new String[]{"-5","0","28","3"};
        numberOfClustering=new String[]{"-4","3","10"};

        randomClustering=new RandomClustering(new Data());
        kMeansClusterer=new KMeansClusterer(new Data());
        randomClassification=new RandomClassification(new Data());
    }
    /**
     * RandomClassification Configuration Test
     */

    //valid configuration
    @Test
    public void RandomClassification_validInputConfigurationTest()throws IllegalAccessException,InvocationTargetException {
        ConfigurationAction.invoke(ui,randomClassification,
                maxIteration[3],
                IterationInterval[3],
                true,
                stage,
                "0");
        assertEquals(500,randomClassification.getConfiguration().MaxInterval);
        assertEquals(3,randomClassification.getConfiguration().IterationInterval);
        assertEquals(0,randomClassification.getConfiguration().NumberOfClustering);
        assertTrue(randomClassification.getConfiguration().continous);
    }
    //invalid Configuration :Negative Input, input<0
    @Test
    public void RandomClassification_NegativeInputConfigurationTest()throws IllegalAccessException,InvocationTargetException {
        ConfigurationAction.invoke(ui,randomClassification,
                maxIteration[0],
                IterationInterval[0],
                true,
                stage,
                "0");
        assertEquals(1,randomClassification.getConfiguration().MaxInterval);
        assertEquals(1,randomClassification.getConfiguration().IterationInterval);
        assertEquals(0,randomClassification.getConfiguration().NumberOfClustering);
        assertTrue(randomClassification.getConfiguration().continous);
    }

    //invalid Configuration input: 0
    @Test
    public void RandomClassification_ZeroInputConfigurationTest()throws IllegalAccessException,InvocationTargetException {
        randomClassification.getConfiguration().IterationInterval=5;
        randomClassification.getConfiguration().MaxInterval=100;
        ConfigurationAction.invoke(ui,randomClassification,
                maxIteration[1],
                IterationInterval[1],
                true,
                stage,
                "0");
        assertEquals(100,randomClassification.getConfiguration().MaxInterval);
        assertEquals(5,randomClassification.getConfiguration().IterationInterval);
        assertEquals(0,randomClassification.getConfiguration().NumberOfClustering);
        assertTrue(randomClassification.getConfiguration().continous);
    }
    //invalid Configuration input: Iteration interval> max interval
    @Test
    public void RandomClassification_IterationGreatThnMaxInputConfigurationTest()throws IllegalAccessException,InvocationTargetException {
        ConfigurationAction.invoke(ui,randomClassification,
                maxIteration[2],
                IterationInterval[2],
                true,
                stage,
                "0");
        assertEquals(15,randomClassification.getConfiguration().MaxInterval);
        assertEquals(15,randomClassification.getConfiguration().IterationInterval);
        assertEquals(0,randomClassification.getConfiguration().NumberOfClustering);
        assertTrue(randomClassification.getConfiguration().continous);
    }
    /**
     * RandomClustering Configuration Test
     */
    //valid configuration
    @Test
    public void RandomClustering_ValidDataInput() throws IllegalAccessException,InvocationTargetException{
        ConfigurationAction.invoke(ui,randomClustering,
                maxIteration[3],
                IterationInterval[3],
                true,
                stage,
                numberOfClustering[1]);
        assertEquals(500,randomClustering.getConfiguration().MaxInterval);
        assertEquals(3,randomClustering.getConfiguration().IterationInterval);
        assertEquals(3,randomClustering.getConfiguration().NumberOfClustering);
        assertTrue(randomClustering.getConfiguration().continous);
    }
    //invalid Configuration :Negative Input, input<0
    @Test
    public void RandomClustering_NegativeInputConfigurationTest() throws IllegalAccessException,InvocationTargetException{
        ConfigurationAction.invoke(ui,randomClustering,
                maxIteration[0],
                IterationInterval[0],
                true,
                stage,
                numberOfClustering[1]);
        assertEquals(1,randomClustering.getConfiguration().MaxInterval);
        assertEquals(1,randomClustering.getConfiguration().IterationInterval);
        assertEquals(3,randomClustering.getConfiguration().NumberOfClustering);
        assertTrue(randomClustering.getConfiguration().continous);
    }
    //invalid Configuration input: 0
    @Test
    public void RandomClustering_ZeroInputConfigurationTest()throws IllegalAccessException,InvocationTargetException {
        randomClustering.getConfiguration().IterationInterval=5;
        randomClustering.getConfiguration().MaxInterval=100;
        randomClustering.getConfiguration().NumberOfClustering=3;
        ConfigurationAction.invoke(ui,randomClustering,
                maxIteration[1],
                IterationInterval[1],
                true,
                stage,
                numberOfClustering[1]);
        assertEquals(100,randomClustering.getConfiguration().MaxInterval);
        assertEquals(5,randomClustering.getConfiguration().IterationInterval);
        assertEquals(3,randomClustering.getConfiguration().NumberOfClustering);
        assertTrue(randomClustering.getConfiguration().continous);
    }
    //invalid Configuration input: Iteration interval> max interval
    @Test
    public void RandomClustering_IterationGreatThnMaxInputConfigurationTest()throws IllegalAccessException,InvocationTargetException {
        ConfigurationAction.invoke(ui,randomClustering,
                maxIteration[2],
                IterationInterval[2],
                true,
                stage,
                numberOfClustering[1]);
        assertEquals(15,randomClustering.getConfiguration().MaxInterval);
        assertEquals(15,randomClustering.getConfiguration().IterationInterval);
        assertEquals(3,randomClustering.getConfiguration().NumberOfClustering);
        assertTrue(randomClustering.getConfiguration().continous);
    }
    //invalid Configuration input: number of clustering <0
    @Test
    public void RandomClustering_ClusterNumberBelowTwoInputConfigurationTest()throws IllegalAccessException,InvocationTargetException {
        ConfigurationAction.invoke(ui,randomClustering,
                maxIteration[3],
                IterationInterval[3],
                true,
                stage,
                numberOfClustering[0]);
        assertEquals(500,randomClustering.getConfiguration().MaxInterval);
        assertEquals(3,randomClustering.getConfiguration().IterationInterval);
        assertEquals(2,randomClustering.getConfiguration().NumberOfClustering);
        assertTrue(randomClustering.getConfiguration().continous);
    }
    //invalid Configuration input: number of clustering >4
    @Test
    public void RandomClustering_ClusterNumberGreaterFourInputConfigurationTest()throws IllegalAccessException,InvocationTargetException {
        ConfigurationAction.invoke(ui,randomClustering,
                maxIteration[3],
                IterationInterval[3],
                true,
                stage,
                numberOfClustering[2]);
        assertEquals(500,randomClustering.getConfiguration().MaxInterval);
        assertEquals(3,randomClustering.getConfiguration().IterationInterval);
        assertEquals(4,randomClustering.getConfiguration().NumberOfClustering);
        assertTrue(randomClustering.getConfiguration().continous);
    }

    /**
     * K-MeanClustering Configuration Test
     */
    //valid configuration
    @Test
    public void KmeanCluster_ValidDataInput() throws IllegalAccessException,InvocationTargetException{
        ConfigurationAction.invoke(ui,kMeansClusterer,
                maxIteration[3],
                IterationInterval[3],
                true,
                stage,
                numberOfClustering[1]);
        assertEquals(500,kMeansClusterer.getConfiguration().MaxInterval);
        assertEquals(3,kMeansClusterer.getConfiguration().IterationInterval);
        assertEquals(3,kMeansClusterer.getConfiguration().NumberOfClustering);
        assertTrue(kMeansClusterer.getConfiguration().continous);
    }
    //invalid Configuration :Negative Input, input<0
    @Test
    public void KmeanCluster_NegativeInputConfigurationTest() throws IllegalAccessException,InvocationTargetException{
        ConfigurationAction.invoke(ui,kMeansClusterer,
                maxIteration[0],
                IterationInterval[0],
                true,
                stage,
                numberOfClustering[1]);
        assertEquals(1,kMeansClusterer.getConfiguration().MaxInterval);
        assertEquals(1,kMeansClusterer.getConfiguration().IterationInterval);
        assertEquals(3,kMeansClusterer.getConfiguration().NumberOfClustering);
        assertTrue(kMeansClusterer.getConfiguration().continous);
    }
    //invalid Configuration input: 0
    @Test
    public void KmeanCluster_ZeroInputConfigurationTest()throws IllegalAccessException,InvocationTargetException {
        kMeansClusterer.getConfiguration().IterationInterval=5;
        kMeansClusterer.getConfiguration().MaxInterval=100;
        kMeansClusterer.getConfiguration().NumberOfClustering=3;
        ConfigurationAction.invoke(ui,kMeansClusterer,
                maxIteration[1],
                IterationInterval[1],
                true,
                stage,
                numberOfClustering[1]);
        assertEquals(100,kMeansClusterer.getConfiguration().MaxInterval);
        assertEquals(5,kMeansClusterer.getConfiguration().IterationInterval);
        assertEquals(3,kMeansClusterer.getConfiguration().NumberOfClustering);
        assertTrue(kMeansClusterer.getConfiguration().continous);
    }
    //invalid Configuration input: Iteration interval> max interval
    @Test
    public void KmeanCluster_IterationGreatThnMaxInputConfigurationTest()throws IllegalAccessException,InvocationTargetException {
        ConfigurationAction.invoke(ui,kMeansClusterer,
                maxIteration[2],
                IterationInterval[2],
                true,
                stage,
                numberOfClustering[1]);
        assertEquals(15,kMeansClusterer.getConfiguration().MaxInterval);
        assertEquals(15,kMeansClusterer.getConfiguration().IterationInterval);
        assertEquals(3,kMeansClusterer.getConfiguration().NumberOfClustering);
        assertTrue(kMeansClusterer.getConfiguration().continous);
    }
    //invalid Configuration input: number of clustering <0
    @Test
    public void KmeanCluster_ClusterNumberBelowTwoInputConfigurationTest()throws IllegalAccessException,InvocationTargetException {
        ConfigurationAction.invoke(ui,kMeansClusterer,
                maxIteration[3],
                IterationInterval[3],
                true,
                stage,
                numberOfClustering[0]);
        assertEquals(500,kMeansClusterer.getConfiguration().MaxInterval);
        assertEquals(3,kMeansClusterer.getConfiguration().IterationInterval);
        assertEquals(2,kMeansClusterer.getConfiguration().NumberOfClustering);
        assertTrue(kMeansClusterer.getConfiguration().continous);
    }
    //invalid Configuration input: number of clustering >4
    @Test
    public void KmeanCluster_ClusterNumberGreaterFourInputConfigurationTest()throws IllegalAccessException,InvocationTargetException {
        ConfigurationAction.invoke(ui,kMeansClusterer,
                maxIteration[3],
                IterationInterval[3],
                true,
                stage,
                numberOfClustering[2]);
        assertEquals(500,kMeansClusterer.getConfiguration().MaxInterval);
        assertEquals(3,kMeansClusterer.getConfiguration().IterationInterval);
        assertEquals(4,kMeansClusterer.getConfiguration().NumberOfClustering);
        assertTrue(kMeansClusterer.getConfiguration().continous);
    }
}