package etl;

import java.util.*;

public class DataSplitUtility {
    private LinkedHashSet<String> entireDataSet = new LinkedHashSet<>();
    private LinkedHashSet<String> trainingSet   = new LinkedHashSet<>();
    private LinkedHashSet<String> testingSet    = new LinkedHashSet<>();


    public DataSplitUtility(List<String> entireDatasetLabels) {
        entireDataSet.addAll(entireDatasetLabels);
    }

    public DataSplitUtility(String[] entireDatasetLabels) {
        entireDataSet.addAll(Arrays.asList(entireDatasetLabels));
    }

    public List<LinkedHashSet<String>> generateTrainTestSplit(int trainingPercentage) {
        int                  trainingSamplesCount = (int) (entireDataSet.size() * trainingPercentage / 100.0d);
        Map<String, Integer> pickedSamples        = new HashMap<>();

        List<String> population = new ArrayList<>();
        population.addAll(entireDataSet);

        while ( pickedSamples.keySet().size() < trainingSamplesCount ) {
            int randomSample = (int) (Math.random() * population.size());

            String pickedSample = population.get(randomSample);
            if ( !pickedSamples.containsKey(pickedSample) ) {
                pickedSamples.put(pickedSample, 1);
                population.remove(randomSample);
            }
        }

        this.trainingSet.addAll(pickedSamples.keySet());
        this.testingSet.addAll(population);

        List<LinkedHashSet<String>> dataSplits = new ArrayList<>();
        dataSplits.add(trainingSet);
        dataSplits.add(testingSet);
        return dataSplits;
    }

    public LinkedHashSet<String> getEntireDataSet() {
        return entireDataSet;
    }

    public void setEntireDataSet(LinkedHashSet<String> entireDataSet) {
        this.entireDataSet = entireDataSet;
    }

    public LinkedHashSet<String> getTrainingSet() {
        return trainingSet;
    }

    public void setTrainingSet(LinkedHashSet<String> trainingSet) {
        this.trainingSet = trainingSet;
    }

    public LinkedHashSet<String> getTestingSet() {
        return testingSet;
    }

    public void setTestingSet(LinkedHashSet<String> testingSet) {
        this.testingSet = testingSet;
    }
}
