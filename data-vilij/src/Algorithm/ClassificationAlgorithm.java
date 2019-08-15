package Algorithm;




import java.util.List;
/**
 *
 * @author Ivan Tinov
 */
public abstract class ClassificationAlgorithm implements AlgorithmType {
    enum classifer{
        RandomClassification
    }
    protected List<Integer> output;
    protected Configuration configuration;

    public List<Integer> getOutput() {
        return output;
    }

}
