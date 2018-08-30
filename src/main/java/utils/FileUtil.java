package utils;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
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

    public static void writeFileNamesToDisk(List<Path> paths, String fileName) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(fileName)));
        for (Path path : paths) {
            bufferedWriter.write(path.toString() + "\n");
        }
        bufferedWriter.close();
    }

    public static void writeMappedLabelsToDisk(Map<String, List<String>> mappedLabels, String fileName) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(fileName)));

        for (String key : mappedLabels.keySet()) {
            String content = (key + ",");
            List<String> files = mappedLabels.get(key);
            int i = 0;
            for (; i < (files.size() - 1); i++) {
                content += files.get(i) + ":";
            }
            content += files.get(i) + "\n";
            bufferedWriter.write(content);
        }

        bufferedWriter.close();
    }

    public static void writeContentToDisk(String filename, String content) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(filename)));
        bufferedWriter.write(content);
        bufferedWriter.close();
    }
}
