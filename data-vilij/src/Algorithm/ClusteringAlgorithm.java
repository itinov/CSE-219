package Algorithm;

import dataProcessors.Data;
/**
 *
 * @author Weixin Tan
 */
public abstract class ClusteringAlgorithm implements AlgorithmType {
    public enum Clusters{
        RandomClustering, KMeansClusterer
    }

    protected Configuration configuration;

    abstract Data getOutput();

    protected void setNumberOfCluster(){
        if(configuration.NumberOfClustering<2)
            configuration.NumberOfClustering=2;
        else if(configuration.NumberOfClustering>4)
            configuration.NumberOfClustering=4;
    }

}
