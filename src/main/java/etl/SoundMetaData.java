package etl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SoundMetaData {

    private Map<String, String> labelledDataset = new HashMap<>();
    private Map<String, List<String>> mappedLabels = new HashMap<>();
    private Logger logger = LoggerFactory.getLogger(SoundMetaData.class);
    private List<Path> allFiles = new ArrayList<>();
    private List<String> manuallyVerifiedFiles = new ArrayList<>();
    private Set<String> manuallyVerifiedClasses = new HashSet<>();
    private Set<String> uniqueLabels = new HashSet<>();
    private Properties metaProperties = new Properties();

    //private Map<String, List<String>> mappedLabels = new HashMap<>();

    public SoundMetaData(Properties metaProperties) {
        this.metaProperties = metaProperties;
    }

    public void initializeLabelledData(String audioResourcePath) {
        try {
            Files.list(Paths.get(audioResourcePath)).forEach(path -> allFiles.add(path));

            for (Path p : allFiles) {
                String s = p.toString();
                logger.info(s.replaceAll(audioResourcePath + "/", ""));
            }

        } catch (IOException io) {
            logger.error("Exception when crawling audioTrain data", io);
        }
    }

    public List<Path> getAllFiles() {
        return allFiles;
    }

    public List<String> getManuallyVerifiedFiles(String metaDataFile) {
        try {
            String dataLine = "";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(metaDataFile));

            while ( (dataLine = bufferedReader.readLine()) != null ) {
                String[] parts = dataLine.split(",");
                for (int i = 0; i < parts.length; i++) {
                    if ( parts[2].equalsIgnoreCase("1") ) {
                        labelledDataset.put(parts[0], parts[1]);
                        manuallyVerifiedClasses.add(parts[1]);
                        uniqueLabels.add(parts[1]);
                    }
                }
            }

            for (String key : labelledDataset.keySet()) {
                logger.info(" key = " + key + " value = " + labelledDataset.get(key));
            }
            logger.info("Number of manually verified samples = " + labelledDataset.size());

            return manuallyVerifiedFiles;
        } catch (IOException io) {
            logger.error("Error while reading manually verified files list.", io);
            return null;
        }
    }

    public Set<String> getManuallyVerifiedClasses() {
        return manuallyVerifiedClasses;
    }

    public Map<String, Integer> getClassCompositionGrndTruth() {
        Map<String, Integer> classComposition = new HashMap<>();

        for (String key : labelledDataset.keySet()) {
            String clasz = labelledDataset.get(key);
            if ( classComposition.containsKey(clasz) ) {
                int count = classComposition.get(clasz);
                classComposition.put(clasz, ++count);
                List<String> temp = mappedLabels.get(clasz);
                temp.add(key);
                mappedLabels.put(clasz, temp);
            } else {
                classComposition.put(clasz, 1);
                List<String> temp = new ArrayList<>();
                temp.add(key);
                mappedLabels.put(clasz, temp);
            }
        }

        return classComposition;
    }

    public Map<String, String> getLabelledDataset() {
        return labelledDataset;
    }

    public Map<String, List<String>> getMappedLabels() {
        return mappedLabels;
    }

    public Set<String> getUniqueLabels() {
        return uniqueLabels;
    }

    public void getFormattedTrainingSet(String encodedPath, OneHotEncoder oneHotEncoder) throws IOException {
        for (String inputFile : labelledDataset.keySet()) {
            String classId = labelledDataset.get(inputFile);
            String encodedFileName = inputFile.replaceAll(".wav", ".enc");

            String encodedContent = getEncodedFileContent(encodedPath + "/" + encodedFileName);
            encodedContent += " | " + oneHotEncoder.getEncodedClass(classId);
            FileUtil.writeContentToDisk(encodedPath + "/formattedInput/" + encodedFileName, encodedContent);
        }
    }

    public void getOneHotEncodings(String encodedPath, OneHotEncoder oneHotEncoder) throws IOException {
        for (String inputFile : labelledDataset.keySet()) {
            String classId = labelledDataset.get(inputFile);
            String encodedFileName = inputFile.replaceAll(".wav", ".lbl");

            String encodedContent = oneHotEncoder.getEncodedClass(classId);
            FileUtil.writeContentToDisk(encodedPath + encodedFileName, encodedContent);
        }
    }

    public String getEncodedFileContent(String filename) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        String dataline = "";
        while ( (dataline = bufferedReader.readLine()) != null ) {
            contentBuilder.append(dataline).append("\n");
        }
        bufferedReader.close();
        return contentBuilder.toString();
    }
}
