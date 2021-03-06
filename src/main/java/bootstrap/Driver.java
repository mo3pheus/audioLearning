package bootstrap;

import audioProcessing.WavFile;
import etl.DataPreProcessor;
import etl.SoundMetaData;
import org.apache.log4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class Driver {
    public static final String SEPARATOR =
            "==============================================================";

    public static Properties projectProperties = new Properties();
    public static Logger     logger            = LoggerFactory.getLogger(Driver.class);

    public static void main(String[] args) {
        try {
            String logFilePath = "";
            configureConsoleLogging(Boolean.parseBoolean(args[0]));
            logger.info(SEPARATOR);
            logger.info(" Project properties are loaded. Log file generated for this run = " + logFilePath);
            projectProperties = getProjectProperties(args[1]);
//
//            SoundMetaData soundMetaData = new SoundMetaData(projectProperties);
//            soundMetaData.initializeLabelledData(projectProperties.getProperty("audio.resources.path"));
//            soundMetaData.getManuallyVerifiedFiles(projectProperties.getProperty("audio.resources.train.metaFile"));
//
//            logger.info(SEPARATOR);
//            for (String uniqueClass : soundMetaData.getManuallyVerifiedClasses()) {
//                logger.info(uniqueClass);
//            }
//            logger.info("Total number of manually verified classes = " + soundMetaData.getManuallyVerifiedClasses()
//                    .size());
//            logger.info(SEPARATOR);
//
//            FileUtil.writeMetaDataToDisk(soundMetaData.getLabelledDataset(),
//                    projectProperties.getProperty("audio.ml.ground.truth"));
//            FileUtil.writeCompDataToDisk(soundMetaData.getClassCompositionGrndTruth(), projectProperties.getProperty
//                    ("audio.resources.class.compositionFile"));
//            FileUtil.writeFileNamesToDisk(soundMetaData.getAllFiles(),
//                    projectProperties.getProperty("audio.resources.train.fileList"));
//            FileUtil.writeMappedLabelsToDisk(soundMetaData.getMappedLabels(),
//                    projectProperties.getProperty("audio.resources.train.mappedLabelsFile"));
//            FileUtil.createSplitTrainingSet(soundMetaData.getMappedLabels(),
//                    projectProperties.getProperty("audio.resources.training.splitDataPath"),
//                    projectProperties.getProperty("audio.resources.train.etl.commands"));
//
////            PredictionSuite predictionSuite = new PredictionSuite(soundMetaData.getLabelledDataset(),
////                                                                  projectProperties);
//
//            logger.info(SEPARATOR);
//            OneHotEncoder oneHotEncoder = new OneHotEncoder(soundMetaData.getUniqueLabels());
//            for (String classId : soundMetaData.getUniqueLabels()) {
//                logger.info("ClassId = " + classId + " encoding = " + oneHotEncoder.getEncodedClass(classId));
//            }
//
//            for (String classId : soundMetaData.getUniqueLabels()) {
//                logger.info(oneHotEncoder.getClassIdForEncoding(oneHotEncoder.getEncodedClass(classId)));
//            }
//            logger.info(SEPARATOR);
//
//            logger.info(SEPARATOR);
//            logger.info("Generating formatted input.");
//            soundMetaData.getFormattedTrainingSet(projectProperties.getProperty("audio.resources.train.encodedInput.path"), oneHotEncoder);
//            soundMetaData.getOneHotEncodings(projectProperties.getProperty("audio.resources.train.labels.path"),
//                    oneHotEncoder);
//            logger.info("Finished consolidated input");
//            logger.info(SEPARATOR);
//
//            String[] acceptedLabels = {"Meow", "Bark", "Cowbell"};
//            TrainingDataCenter trainingDataCenter = new TrainingDataCenter(Arrays.asList(acceptedLabels),
//                    projectProperties.getProperty("audio.resources.train.metaFile"));
//            trainingDataCenter.writeReducedLabelsDataset();
//
//            List<String> acceptedFiles = new ArrayList<>();
//            acceptedFiles.addAll(trainingDataCenter.getAcceptedFiles());
//            FileUtil.writeContentToDisk(projectProperties.getProperty("audio.resources.reduced.metaFile"),
//                    acceptedFiles);

            DataPreProcessor dataPreProcessor = new DataPreProcessor(projectProperties);
        } catch (IOException io) {
            logger.error("Error while reading the project properties file.", io);
        }
    }

    public static void processAudioFiles(SoundMetaData soundMetaData) {
        for (Path path : soundMetaData.getAllFiles()) {
            displayAudioInfo(path.toString());
        }
    }

    public static void displayAudioInfo(String audioFilePath) {
        try {
            // Open the wav file specified as the first argument
            WavFile wavFile = WavFile.openWavFile(new File(audioFilePath));

            // Display information about the wav file
            wavFile.display();

            // Get the number of audio channels in the wav file
            int numChannels = wavFile.getNumChannels();

            // Create a buffer of 100 frames
            double[] buffer = new double[100 * numChannels];

            int    framesRead;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;

            do {
                // Read frames into buffer
                framesRead = wavFile.readFrames(buffer, 100);

                // Loop through frames and look for minimum and maximum value
                for (int s = 0; s < framesRead * numChannels; s++) {
                    if ( buffer[s] > max ) {
                        max = buffer[s];
                    }
                    if ( buffer[s] < min ) {
                        min = buffer[s];
                    }
                }
            }
            while ( framesRead != 0 );

            // Close the wavFile
            wavFile.close();

            // Output the minimum and maximum value
            logger.info("Min: %f, Max: %f\n", min, max);
        } catch (Exception e) {
            logger.info("Exception when handing audioFile = " + audioFilePath, e);
        }
    }

    public static String configureLogging(boolean debug) {
        FileAppender fa = new FileAppender();

        if ( !debug ) {
            fa.setThreshold(Level.toLevel(Priority.INFO_INT));
            fa.setFile("executionLogs/log_infoLevel_report_" + Long.toString(System.currentTimeMillis()) + ".log");
        } else {
            fa.setThreshold(Level.toLevel(Priority.DEBUG_INT));
            fa.setFile("executionLogs/log_debugLevel_report_" + Long.toString(System.currentTimeMillis()) + ".log");
        }

        fa.setLayout(new EnhancedPatternLayout("%-6d [%25.35t] %-5p %40.80c - %m%n"));

        fa.activateOptions();
        org.apache.log4j.Logger.getRootLogger().addAppender(fa);
        return fa.getFile();
    }

    public static void configureConsoleLogging(boolean debug) {
        ConsoleAppender ca = new ConsoleAppender();
        if ( !debug ) {
            ca.setThreshold(Level.toLevel(Priority.INFO_INT));
        } else {
            ca.setThreshold(Level.toLevel(Priority.DEBUG_INT));
        }
        ca.setLayout(new EnhancedPatternLayout("%-6d [%t] %-5p %c - %m%n"));
        ca.activateOptions();
        org.apache.log4j.Logger.getRootLogger().addAppender(ca);
    }

    public static Properties getProjectProperties(String propertiesFilePath) throws IOException {
        logger.info("Properties file specified at location = " + propertiesFilePath);
        FileInputStream projFile   = new FileInputStream(propertiesFilePath);
        Properties      properties = new Properties();
        properties.load(projFile);
        return properties;
    }
}
