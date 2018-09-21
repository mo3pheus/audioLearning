package etl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class DataPreProcessor {
    public static final Integer DEFAULT_TRAIN_PERCENTAGE               = 75;
    public static final String  GROUND_TRUTH_PROPERTY                  = "audio.ml.ground.truth";
    public static final String  AUDIO_RESOURCES_PATH_PROPERTY          = "audio.resources.path";
    public static final String  ACCEPTED_LABELS_PROPERTY               = "audio.resources.train.acceptedLabels.list";
    public static final String  AUDIO_TRAIN_SPLIT_PATH_PROPERTY        = "audio.resources.train.split.path";
    public static final String  AUDIO_TEST_SPLIT_PATH_PROPERTY         = "audio.resources.test.split.path";
    public static final String  AUDIO_TEST_SPLIT_PATH_LABELS_PROPERTY  = "audio.resources.test.split.labelsPath";
    public static final String  AUDIO_TRAIN_SPLIT_PATH_LABELS_PROPERTY = "audio.resources.train.split.labelsPath";

    Properties            processConfiguration = new Properties();
    LinkedHashSet<String> superset             = new LinkedHashSet<>();
    LinkedHashSet<String> acceptedLabels       = new LinkedHashSet<>();
    Logger                logger               = LoggerFactory.getLogger(DataPreProcessor.class);
    DataSplitUtility      dataSplitUtility     = null;
    String                dataFile             = null;
    String                audioResourcesPath   = null;
    String                audioTrainSplitPath  = null;
    String                audioTestSplitPath   = null;
    String                audioTrainLabelsPath = null;
    String                audioTestLabelsPath  = null;

    Map<String, String>   classificationMap = new HashMap<>();
    LinkedHashSet<String> trainFileNames    = new LinkedHashSet<>();
    LinkedHashSet<String> testFileNames     = new LinkedHashSet<>();

    OneHotEncoder labelsEncoder = null;

    public DataPreProcessor(Properties processConfiguration) {
        this.processConfiguration = processConfiguration;
        configureProcessor();
        try {
            populateSuperset();

            List<String> tempSuperset = new ArrayList<>();
            tempSuperset.addAll(superset);

            dataSplitUtility = new DataSplitUtility(tempSuperset);
            List<LinkedHashSet<String>> trainTestSplit = dataSplitUtility.generateTrainTestSplit(DEFAULT_TRAIN_PERCENTAGE);
            this.trainFileNames = trainTestSplit.get(0);
            this.testFileNames = trainTestSplit.get(1);

            writeSplitSets();
        } catch (IOException io) {
            logger.error("Error while parsing groundTruth dataFile.", io);
        }
    }

    private void populateSuperset() throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(dataFile))) {
            String dataLine = "";
            while ( (dataLine = bufferedReader.readLine()) != null ) {
                String[] splitParts = dataLine.split(",");
                String   fileName   = splitParts[0];
                String   classId    = splitParts[1];

                if ( acceptedLabels.contains(classId) ) {
                    classificationMap.put(fileName, classId);
                    superset.add(fileName);
                }
            }
        }
    }

    private void configureProcessor() {
        this.dataFile = processConfiguration.getProperty(GROUND_TRUTH_PROPERTY);
        String[] acceptedLabelsArr = processConfiguration.getProperty(ACCEPTED_LABELS_PROPERTY).split(",");
        this.acceptedLabels.addAll(Arrays.asList(acceptedLabelsArr));
        this.audioResourcesPath = processConfiguration.getProperty(AUDIO_RESOURCES_PATH_PROPERTY);
        this.audioTestSplitPath = processConfiguration.getProperty(AUDIO_TEST_SPLIT_PATH_PROPERTY);
        this.audioTrainSplitPath = processConfiguration.getProperty(AUDIO_TRAIN_SPLIT_PATH_PROPERTY);
        this.audioTrainLabelsPath = processConfiguration.getProperty(AUDIO_TRAIN_SPLIT_PATH_LABELS_PROPERTY);
        this.audioTestLabelsPath = processConfiguration.getProperty(AUDIO_TEST_SPLIT_PATH_LABELS_PROPERTY);
        this.labelsEncoder = new OneHotEncoder(this.acceptedLabels);
    }

    private void writeSplitSets() {
        int filesWrittenCount = 0;
        try (BufferedWriter trainFileWriter = new BufferedWriter(new FileWriter(new File(audioTrainSplitPath)))) {
            for (String fileName : trainFileNames) {
                trainFileWriter.write(fileName + "\n");
                logger.debug("Now writing fileName = " + fileName);
                logger.debug("Original Classification = " + classificationMap.get(fileName));
                writeLabelsFile(audioTrainLabelsPath + fileName, labelsEncoder.getEncodedClass(classificationMap.get(fileName)));
                filesWrittenCount++;
            }
        } catch (Exception e) {
            logger.error("Encountered Exception when writing training data.", e);
        }
        logger.info("TrainingSet size = " + trainFileNames.size() + " actualFilesWritten = " + filesWrittenCount);

        try (BufferedWriter testFileWriter = new BufferedWriter(new FileWriter(new File(audioTestSplitPath)))) {
            for (String fileName : testFileNames) {
                testFileWriter.write(fileName + "\n");
                writeLabelsFile(audioTestLabelsPath + fileName, labelsEncoder.getEncodedClass(classificationMap.get(fileName)));
            }
        } catch (Exception e) {
            logger.error("Encountered Exception when writing testing data.", e);
        }
    }

    private void writeLabelsFile(String fileName, String content) throws IOException {
        String         labelsFileName = fileName.replace(".wav", ".lbl");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(labelsFileName));
        bufferedWriter.write(content);
        bufferedWriter.close();
    }
}
