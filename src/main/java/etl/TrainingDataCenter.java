package etl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TrainingDataCenter {
    private static final String              DESTINATION_PATH =
            "/home/sanket/Documents/Data/SoundData/reducedLabelsSet/";
    private static final String              DESTINATION_DIR  = "/home/sanket/Documents/Data/SoundData/";
    private              String              trainingMetaFile = "";
    private              Set<String>         acceptedLabels   = new HashSet<>();
    private              Set<String>         acceptedFiles    = new HashSet<>();
    private              OneHotEncoder       oneHotEncoder    = null;
    private              Map<String, String> fileLabelMap     = new HashMap<>();
    private              Logger              logger           = LoggerFactory.getLogger(TrainingDataCenter.class);

    public TrainingDataCenter(List<String> acceptedLabels, String trainingMetaFile) {
        this.acceptedLabels.addAll(acceptedLabels);
        oneHotEncoder = new OneHotEncoder(this.acceptedLabels);
        this.trainingMetaFile = trainingMetaFile;
        try {
            this.fileLabelMap = FileUtil.readLabelledData(trainingMetaFile);
        } catch (IOException io) {
            logger.error("Can't read labelled data", io);
        }
    }

    public void writeReducedLabelsDataset() {
        for (String key : fileLabelMap.keySet()) {
            if (acceptedLabels.contains(fileLabelMap.get(key))) {
                String fileName = key.replace(".wav", ".lbl");
                try {
                    FileUtil.writeContentToDisk(DESTINATION_PATH + fileName,
                                                oneHotEncoder.getEncodedClass(fileLabelMap.get(key)));
                    acceptedFiles.add(key);
                } catch (IOException io) {
                    logger.error("Error while writing content to disk, fileName = " + fileName, io);
                }
            }
        }
    }

    public String getTrainingMetaFile() {
        return trainingMetaFile;
    }

    public Set<String> getAcceptedLabels() {
        return acceptedLabels;
    }

    public Set<String> getAcceptedFiles() {
        return acceptedFiles;
    }

    public OneHotEncoder getOneHotEncoder() {
        return oneHotEncoder;
    }

    public Map<String, String> getFileLabelMap() {
        return fileLabelMap;
    }
}
