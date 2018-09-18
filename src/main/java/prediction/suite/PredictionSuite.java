package prediction.suite;

import http.AudioLabelResponse;
import http.CloudAccessToken;
import http.RequestTask;
import http.RequestUtil;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PredictionSuite {
    public static final int MAX_TESTFILES = 400;
    private volatile CloseableHttpClient httpClient = null;
    private Logger logger = LoggerFactory
            .getLogger(PredictionSuite.class);
    private Map<String, String> labelledTestSet = new HashMap<>();
    private Properties applicationProperties = null;
    private CloudAccessToken accessToken = null;
    private List<AudioLabelResponse> testResults = new ArrayList<>();
    private List<Future<AudioLabelResponse>> responseFutures = new ArrayList<>();
    private List<RequestTask> taskList = new ArrayList<>();
    private ExecutorService executorService = null;
    private Set<String> validLabels = new HashSet<>();
    private Map<String, HttpPost> postMap = new HashMap<>();

    public PredictionSuite(Map<String, String> labelledTestSet, Properties applicationProperties) {
        this.applicationProperties = applicationProperties;
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new
                PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(5);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(1);
        httpClient = HttpClientBuilder
                .create().setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(RequestUtil.createRequestConfig()).build();
        executorService = Executors.newFixedThreadPool(labelledTestSet.keySet().size());
        populateFileList(labelledTestSet);
        aquireAccessToken();
        prepareTaskList();
        fireTasks();
        populateResults();
        evaluateResults();
        cleanUp();
    }

    private void populateFileList(Map<String, String> testSet) {
        String[] validLabelsArr = applicationProperties.getProperty("audio.cloud.valid.labels").split(",");
        validLabels.addAll(Arrays.asList(validLabelsArr));

        int validCount = 0;
        for (String fileName : testSet.keySet()) {
            String label = testSet.get(fileName);
            if ( validLabels.contains(label) ) {
                labelledTestSet.put(fileName, label);
                if ( ++validCount > MAX_TESTFILES ) {
                    break;
                }
            }
        }
    }

    private void aquireAccessToken() {
        try {
            accessToken = RequestUtil.getAccessToken(applicationProperties);
            logger.info(
                    "Access token returned = " + accessToken.getAccessToken() + " validity = " + accessToken.isValid());
        } catch (Exception e) {
            logger.error("Error while requesting accessToken :: ", e);
        }
    }

    private void prepareTaskList() {
        for (String fileName : labelledTestSet.keySet()) {

            String actualLabel = labelledTestSet.get(fileName);
            HttpPost httpPost = null;
            try {
                httpPost = RequestUtil.createHttpPost(fileName, accessToken, applicationProperties);
                postMap.put(fileName, httpPost);
            } catch (Exception e) {
                logger.error(
                        "Error while creating HttpPost object for fileName = " + fileName + " Actual Lable = " +
                                actualLabel, e);
                continue;
            } finally {
                EntityUtils.consumeQuietly(httpPost.getEntity());
            }
            RequestTask requestTask = new RequestTask(httpClient, httpPost, fileName, actualLabel);
            taskList.add(requestTask);
        }
    }

    private void fireTasks() {
        try {
            this.responseFutures = executorService.invokeAll(taskList);
        } catch (InterruptedException ie) {
            logger.error("Encountered interrupted exception", ie);
        }
    }

    private void populateResults() {
        for (Future<AudioLabelResponse> future : responseFutures) {
            try {
                AudioLabelResponse response = future.get();
                testResults.add(response);
                postMap.get(response.getFileName()).releaseConnection();
                EntityUtils.consumeQuietly(postMap.get(response.getFileName()).getEntity());
                response.getHttpResponse().close();
            } catch (InterruptedException e) {
                logger.error("InterruptedException while executing request." + e.getMessage());
            } catch (ExecutionException e) {
                logger.error("ExecutionException while executing request." + e.getMessage());
            } catch (IOException e) {
                logger.error("IOException while executing request." + e.getMessage());
            }
        }
    }

    private void evaluateResults() {
        double totalResultSize = testResults.size();
        double accurateResults = 0.0d;
        for (AudioLabelResponse response : testResults) {
            if ( response.getActualLabel().equals(response.getPredictedClass()) ) {
                accurateResults++;
                logger.info("Filename = " + response.getFileName() + " classified correctly as " + response
                        .getPredictedClass());
            } else {
                logger.error(
                        "Prediction Failure for filename = " + response.getFileName() + " actualLable = " + response
                                .getActualLabel() + " predictedLabel = " + response.getPredictedClass());
                logger.error("Detailed resultComposition = ");
                for (String label : response.getClassProbabilities().keySet()) {
                    logger.info(
                            "For file " + response.getFileName() + " classId = " + label + " confidence = " + response
                                    .getClassProbabilities().get(label));
                }
            }
        }

        logger.info("Final result accuracy = " + (accurateResults / totalResultSize * 100.0d) + "%");
    }

    private void cleanUp() {
        try {
            executorService.shutdown();
            httpClient.close();
        } catch (IOException io) {
            logger.error("Error while cleaning up.", io);
        }
    }
}
