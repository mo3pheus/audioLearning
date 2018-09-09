package http;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.concurrent.Callable;

public class RequestTask implements Callable<AudioLabelResponse> {
    private AudioLabelResponse  audioLabelResponse = null;
    private CloseableHttpClient httpClient         = null;
    private HttpPost            httpPost           = null;
    private String              fileName           = null;
    private String              acutalLabel        = null;

    public RequestTask(CloseableHttpClient httpClient, HttpPost httpPost, String fileName, String acutalLabel) {
        this.fileName = fileName;
        this.httpClient = httpClient;
        this.httpPost = httpPost;
        this.acutalLabel = acutalLabel;
        audioLabelResponse = new AudioLabelResponse();
    }

    @Override
    public AudioLabelResponse call() throws Exception {
        Thread.currentThread().setName("requestTask_" + fileName);
        audioLabelResponse.setFileName(fileName);
        audioLabelResponse.setHttpResponse(httpClient.execute(httpPost));
        audioLabelResponse.setActualLabel(acutalLabel);
        return audioLabelResponse;
    }
}
