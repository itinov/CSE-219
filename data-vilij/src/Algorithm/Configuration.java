package Algorithm;


/**
 *
 * @author Weixin Tan
 */
public class Configuration {
    public int MaxInterval, IterationInterval, NumberOfClustering;
    public boolean continous;
    public Configuration(){}
    public void setConfigration(Configuration configration){
        this.MaxInterval=configration.MaxInterval;
        this.IterationInterval=configration.IterationInterval;
        this.continous=configration.continous;
        this.NumberOfClustering=configration.NumberOfClustering;
    }
}
