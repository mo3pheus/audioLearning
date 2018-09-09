package test;

import bootstrap.Driver;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.CloudResponseObject;
import http.CloudAccessToken;
import http.RequestUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class AccessTokenTest {
    static Logger logger = LoggerFactory.getLogger(AccessTokenTest.class);

    public static void main(String[] args) throws IOException {
        logger.info("Test getting accessToken.");
        Driver.configureConsoleLogging(false);
        Properties       applicationProperties = Driver.getProjectProperties("src/main/resources/project.properties");
        CloudAccessToken cloudAccessToken      = RequestUtil.getAccessToken(applicationProperties);
        logger.info("Access Token = " + cloudAccessToken.getAccessToken());
        logger.info("Valid Seconds = " + cloudAccessToken.getValidSeconds());
        logger.info("Is valid = " + cloudAccessToken.isValid());

        String   soundFile = getParam(args, "--soundFile");
        HttpPost httpPost  = RequestUtil.createHttpPost(soundFile, cloudAccessToken, applicationProperties);

        logger.info("HttpPost:: " + httpPost);

        CloseableHttpClient httpClient = HttpClientBuilder.create().setMaxConnPerRoute(1).setMaxConnTotal(1)
                .setDefaultRequestConfig(RequestUtil.createRequestConfig()).build();
        CloseableHttpResponse response = httpClient.execute(httpPost);
        JSONObject jsonObject = new JSONObject(RequestUtil.convertStreamToString(response.getEntity().getContent
                ()));
        response.close();
        httpClient.close();

        ObjectMapper objectMapper = new ObjectMapper();
        CloudResponseObject cloudResponseObject = objectMapper
                .readValue(jsonObject.toString(), CloudResponseObject.class);

        logger.info("mapped Object logId = " + cloudResponseObject.getLog_id());
        logger.info(jsonObject.toString());
    }

    static String getParam(String[] args, String param) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(param)) {
                return args[i + 1];
            }
        }
        return null;
    }
}
