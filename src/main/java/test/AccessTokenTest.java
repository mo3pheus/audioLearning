package test;

import bootstrap.Driver;
import http.CloudAccessToken;
import http.RequestUtil;
import http.SoundJsonPayload;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

import static http.RequestUtil.convertStreamToString;

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

        SoundJsonPayload soundJsonPayload = new SoundJsonPayload("temp", "something");
        logger.info((new JSONObject(soundJsonPayload)).toString());

        String   testClarinetSoundFile = "/home/sanket/Documents/Data/SoundData/audio_train/b753a444.wav";
        HttpPost httpPost              = RequestUtil.createHttpPost(testClarinetSoundFile, applicationProperties);

        logger.info("HttpPost:: " + httpPost);

        CloseableHttpClient httpClient = HttpClientBuilder.create().setMaxConnPerRoute(1).setMaxConnTotal(1)
                .setDefaultRequestConfig(RequestUtil.createRequestConfig()).build();
        CloseableHttpResponse response = httpClient.execute(httpPost);
        JSONObject jsonObject = new JSONObject(convertStreamToString(response.getEntity().getContent
                ()));
        logger.info(jsonObject.toString());
    }
}
