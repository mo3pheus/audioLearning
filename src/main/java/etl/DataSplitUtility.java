package etl;

import java.util.*;

public class DataSplitUtility {
    private Set<String> entireDataSet = new HashSet<>();
    private Set<String> trainingSet = new HashSet<>();
    private Set<String> testingSet = new HashSet<>();


    public DataSplitUtility(List<String> entireDatasetLabels) {
        entireDataSet.addAll(entireDatasetLabels);
    }

    public DataSplitUtility(String[] entireDatasetLabels) {
        entireDataSet.addAll(Arrays.asList(entireDatasetLabels));
    }

    public List<Set<String>> generateTrainTestSplit(int trainingPercentage) {
        int trainingSamplesCount = (int) (entireDataSet.size() * trainingPercentage / 100.0d);
        Map<String, Integer> pickedSamples = new HashMap<>();

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

        List<Set<String>> dataSplits = new ArrayList<>();
        dataSplits.add(trainingSet);
        dataSplits.add(testingSet);
        return dataSplits;
    }

    public Set<String> getTrainingSet() {
        return trainingSet;
    }

    public void setTrainingSet(Set<String> trainingSet) {
        this.trainingSet = trainingSet;
    }

    public Set<String> getTestingSet() {
        return testingSet;
    }

    public void setTestingSet(Set<String> testingSet) {
        this.testingSet = testingSet;
    }
}
