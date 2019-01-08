package Algorithm;

import dataProcessors.Data;
import javafx.geometry.Point2D;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Ritwik Banerjee
 */
public class KMeansClusterer extends ClusteringAlgorithm {

    private Data       data;
    private List<Point2D> centroids;
    private boolean firstRun;

    private final AtomicBoolean tocontinue;

    @Override
    public Data getOutput() {
        return data;
    }

    public KMeansClusterer(Data data) {
        this.configuration= new Configuration();
        this.data = data;
        this.tocontinue = new AtomicBoolean(false);
        this.firstRun=true;
    }
    @Override
    public void run() {

        if(firstRun) {
            setNumberOfCluster();
            initializeCentroids();
            firstRun=false;
        }
        if(tocontinue.get()) {
            assignLabels();
            recomputeCentroids();
        }
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
    private void initializeCentroids() {
        Set<String>  chosen        = new HashSet<>();
        List<String> instanceNames = new ArrayList<>(data.getDataLabels().keySet());
        Random       r             = new Random();
        while (chosen.size() < configuration.NumberOfClustering) {
            int i = r.nextInt(instanceNames.size());
            while (chosen.contains(instanceNames.get(i)))
                i = (++i % instanceNames.size());
            chosen.add(instanceNames.get(i));
        }
        centroids = chosen.stream().map(name -> data.getDataPoints().get(name)).collect(Collectors.toList());
        tocontinue.set(true);
    }


    private void assignLabels() {
        data.getDataPoints().forEach((instanceName, location) -> {
            double minDistance      = Double.MAX_VALUE;
            int    minDistanceIndex = -1;
            for (int i = 0; i < centroids.size(); i++) {
                double distance = computeDistance(centroids.get(i), location);
                if (distance < minDistance) {
                    minDistance = distance;
                    minDistanceIndex = i;
                }
            }
            data.getDataLabels().put(instanceName, Integer.toString(minDistanceIndex));
        });
    }

    private void recomputeCentroids() {
        tocontinue.set(false);
        IntStream.range(0, configuration.NumberOfClustering).forEach(i -> {
            AtomicInteger clusterSize = new AtomicInteger();
            Point2D sum = data.getDataLabels()
                    .entrySet()
                    .stream()
                    .filter(entry -> i == Integer.parseInt(entry.getValue()))
                    .map(entry -> data.getDataPoints().get(entry.getKey()))
                    .reduce(new Point2D(0, 0), (p, q) -> {
                        clusterSize.incrementAndGet();
                        return new Point2D(p.getX() + q.getX(), p.getY() + q.getY());
                    });
            Point2D newCentroid = new Point2D(sum.getX() / clusterSize.get(), sum.getY() / clusterSize.get());
            if (!newCentroid.equals(centroids.get(i))) {
                centroids.set(i, newCentroid);
                tocontinue.set(true);
            }
        });
    }


    private static double computeDistance(Point2D p, Point2D q) {
        return Math.sqrt(Math.pow(p.getX() - q.getX(), 2) + Math.pow(p.getY() - q.getY(), 2));
    }

}