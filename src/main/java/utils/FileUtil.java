package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class FileUtil {
    public static void writeMetaDataToDisk(Map<String, String> manuallyVerifiedFiles, String fileName) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(fileName)));
        for (String key : manuallyVerifiedFiles.keySet()) {
            bufferedWriter.write(key + "," + manuallyVerifiedFiles.get(key) + "\n");
        }
        bufferedWriter.close();
    }

    public static void writeCompDataToDisk(Map<String, Integer> compositionMap, String fileName) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(fileName)));
        for (String key : compositionMap.keySet()) {
            bufferedWriter.write(key + "," + compositionMap.get(key) + "\n");
        }
        bufferedWriter.close();
    }
}
