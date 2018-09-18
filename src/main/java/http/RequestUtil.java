package http;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class RequestUtil {

    public static RequestConfig createRequestConfig() {
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout((int) TimeUnit.MINUTES
                .toMillis(5)).setConnectTimeout((int) TimeUnit.SECONDS.toMillis(1200))
                .setSocketTimeout((int) TimeUnit.MINUTES.toMillis(10l))
                .build();
        return requestConfig;
    }

    public static CloudAccessToken getAccessToken(Properties httpProperties) throws IOException {
        String endPoint = httpProperties.getProperty("audio.cloud.access.token.endpoint");
        String clientId = httpProperties.getProperty("audio.cloud.client.id");
        String clientSecret = httpProperties.getProperty("audio.cloud.client.secret");
        String grantType = httpProperties.getProperty("audio.cloud.grant.type");

        RequestConfig requestConfig = createRequestConfig();
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().setMaxConnPerRoute(1).setMaxConnTotal(1)
                .setDefaultRequestConfig(requestConfig).build();

        String url = endPoint + "?grant_type=" + grantType + "&client_id=" + clientId +
                "&client_secret=" + clientSecret;
        HttpPost httpPost = new HttpPost(url);

        CloseableHttpResponse httpResponse = closeableHttpClient.execute(httpPost);
        JSONObject jsonObject = new JSONObject(convertStreamToString(httpResponse.getEntity().getContent
                ()));
        httpResponse.close();
        closeableHttpClient.close();

        CloudAccessToken cloudAccessToken = new CloudAccessToken();
        cloudAccessToken.setAccessToken((String) jsonObject.get("access_token"));
        cloudAccessToken.setValidSeconds((Integer) (jsonObject.get("expires_in")));
        return cloudAccessToken;
    }

    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ( (line = reader.readLine()) != null ) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static HttpPost createHttpPost(String audioFile, CloudAccessToken cloudAccessToken, Properties
            httpProperties) throws IOException {
        String audioSourcePrefix = httpProperties.getProperty("audio.resources.path") + "/";
        byte[] content = Files.readAllBytes(Paths.get(audioSourcePrefix + audioFile));
        String contentString = Base64.encode(content);

        String url = httpProperties.getProperty("audio.cloud.classification" +
                ".endpoint") +
                "?access_token=" + cloudAccessToken.getAccessToken();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        JSONObject payload = new JSONObject(new SoundJsonPayload(contentString, "6"));
        StringEntity payloadEntity = new StringEntity(payload.toString());
        httpPost.setEntity(payloadEntity);

        return httpPost;
    }
}
