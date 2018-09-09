package http;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.CloudResponseObject;
import domain.ResultsTuple;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AudioLabelResponse {
    private CloseableHttpResponse httpResponse       = null;
    private String                fileName           = "";
    private String                actualLabel        = null;
    private Map<String, Double>   classProbabilities = new HashMap<>();
    private Long                  logId              = null;
    private String                predictedClass     = null;

    public CloseableHttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(CloseableHttpResponse httpResponse) throws IOException {
        this.httpResponse = httpResponse;
        JSONObject jsonObject = new JSONObject(RequestUtil.convertStreamToString(httpResponse.getEntity().getContent
                ()));
        ObjectMapper objectMapper = new ObjectMapper();
        CloudResponseObject cloudResponseObject = objectMapper
                .readValue(jsonObject.toString(), CloudResponseObject.class);

        double maxProbability = Double.MIN_VALUE;
        for (ResultsTuple resultsTuple : cloudResponseObject.getResults()) {
            classProbabilities.put(resultsTuple.getName(), resultsTuple.getScore());
            if (resultsTuple.getScore() > maxProbability) {
                maxProbability = resultsTuple.getScore();
                predictedClass = resultsTuple.getName();
            }
        }
        EntityUtils.consumeQuietly(httpResponse.getEntity());
    }

    public String getActualLabel() {
        return actualLabel;
    }

    public void setActualLabel(String actualLabel) {
        this.actualLabel = actualLabel;
    }

    public Map<String, Double> getClassProbabilities() {
        return classProbabilities;
    }

    public Long getLogId() {
        return logId;
    }

    public String getPredictedClass() {
        return predictedClass;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
