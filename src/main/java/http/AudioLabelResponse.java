package http;

import org.apache.http.client.methods.CloseableHttpResponse;

public class AudioLabelResponse {
    private CloseableHttpResponse httpResponse = null;
    private String actualLabel = null;

    public CloseableHttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(CloseableHttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public String getActualLabel() {
        return actualLabel;
    }

    public void setActualLabel(String actualLabel) {
        this.actualLabel = actualLabel;
    }
}
