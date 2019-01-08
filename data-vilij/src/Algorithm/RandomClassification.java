package Algorithm;

import dataProcessors.Data;

import java.util.Arrays;
import java.util.Random;

public class RandomClassification extends ClassificationAlgorithm{
    private static final Random RAND = new Random();

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private Data data;

    public RandomClassification(Data data) {
        this.data = data;
        this.configuration=new Configuration();
    }

    @Override
    public void run() {
        int xCoefficient =  new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
        int yCoefficient = 10;
        int constant     = RAND.nextInt(11);
        // this is the real output of the classifier
        output = Arrays.asList(xCoefficient, yCoefficient, constant);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }


}


