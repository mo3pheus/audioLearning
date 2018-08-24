package bootstrap;

import etl.SoundMetaData;
import org.apache.log4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Driver {
    public static final String SEPARATOR =
            "==============================================================";

    public static Properties projectProperties = new Properties();
    public static Logger logger = LoggerFactory.getLogger(Driver.class);

    public static void main(String[] args) {
        try {
            String logFilePath = "";
            configureConsoleLogging(Boolean.parseBoolean(args[0]));
            logger.info(SEPARATOR);
            logger.info(" Project properties are loaded. Log file generated for this run = " + logFilePath);
            projectProperties = getProjectProperties(args[1]);

            SoundMetaData soundMetaData = new SoundMetaData();
            soundMetaData.initializeLabelledData(projectProperties.getProperty("audio.resources.path"));
            soundMetaData.getManuallyVerifiedFiles(projectProperties.getProperty("audio.resources.train.metaFile"));

            logger.info(SEPARATOR);
            for (String uniqueClass : soundMetaData.getManuallyVerifiedClasses()) {
                logger.info(uniqueClass);
            }
            logger.info("Total number of manually verified classes = " + soundMetaData.getManuallyVerifiedClasses().size());
            logger.info(SEPARATOR);

            FileUtil.writeCompDataToDisk(soundMetaData.getClassCompositionGrndTruth(),projectProperties.getProperty("audio.resources.class.compositionFile"));

        } catch (IOException io) {
            logger.error("Error while reading the project properties file.", io);
        }
    }

    public static String configureLogging(boolean debug) {
        FileAppender fa = new FileAppender();

        if (!debug) {
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
        if (!debug) {
            ca.setThreshold(Level.toLevel(Priority.INFO_INT));
        } else {
            ca.setThreshold(Level.toLevel(Priority.DEBUG_INT));
        }
        ca.setLayout(new EnhancedPatternLayout("%-6d [%25.35t] %-5p %40.80c - %m%n"));
        ca.activateOptions();
        org.apache.log4j.Logger.getRootLogger().addAppender(ca);
    }

    public static Properties getProjectProperties(String propertiesFilePath) throws IOException {
        logger.info("Properties file specified at location = " + propertiesFilePath);
        FileInputStream projFile = new FileInputStream(propertiesFilePath);
        Properties properties = new Properties();
        properties.load(projFile);
        return properties;
    }
}
