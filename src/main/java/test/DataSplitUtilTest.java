package test;

import bootstrap.Driver;
import etl.DataSplitUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class DataSplitUtilTest {

    static Logger logger = LoggerFactory.getLogger(DataSplitUtilTest.class);

    public static void main(String[] args) {
        Driver.configureConsoleLogging(false);
        String[] names = {
                "Sanket1", "Sanket2", "Sanket3", "Sanket4", "Sanket5", "Sanket6", "Sanket7", "Sanket8", "Sanket9", "Sanket10"};
        DataSplitUtility dataSplitUtility = new DataSplitUtility(names);
        List<Set<String>> dataSplits = dataSplitUtility.generateTrainTestSplit(80);

        Set<String> trainingSet = dataSplits.get(0);
        Set<String> testingSet = dataSplits.get(1);

        logger.info("Training Set contains::");
        for (String key : trainingSet) {
            logger.info(key);
        }

        logger.info("Test Set contains::");
        for (String key : testingSet) {
            logger.info(key);
        }
    }
}
