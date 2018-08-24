package etl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SoundMetaData {

    private Map<String, String> labelledDataset = new HashMap<>();
    private Logger logger = LoggerFactory.getLogger(SoundMetaData.class);
    private List<Path> allFiles = new ArrayList<>();
    private List<String> manuallyVerifiedFiles = new ArrayList<>();
    private Set<String> manuallyVerifiedClasses = new HashSet<>();

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

    public List<String> getManuallyVerifiedFiles(String metaDataFile) {
        try {
            String dataLine = "";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(metaDataFile));

            while ((dataLine = bufferedReader.readLine()) != null) {
                String[] parts = dataLine.split(",");
                for (int i = 0; i < parts.length; i++) {
                    //System.out.println(" i = " + i + " :: part = " + parts[i]);
                    if (parts[2].equalsIgnoreCase("1")) {
                        labelledDataset.put(parts[0], parts[1]);
                        manuallyVerifiedClasses.add(parts[1]);
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
            if (classComposition.containsKey(labelledDataset.get(key))) {
                int count = classComposition.get(labelledDataset.get(key));
                classComposition.put(labelledDataset.get(key), ++count);
            } else {
                classComposition.put(labelledDataset.get(key), 1);
            }
        }

        return classComposition;
    }

    public Map<String, String> getLabelledDataset() {
        return labelledDataset;
    }
}
