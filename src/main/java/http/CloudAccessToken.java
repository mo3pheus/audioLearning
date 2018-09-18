package http;

import org.joda.time.DateTime;

import java.util.concurrent.TimeUnit;

public class CloudAccessToken {
    String accessToken = "";
    DateTime creationTime = new DateTime(System.currentTimeMillis());
    int validSeconds = 0;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public DateTime getCreationTime() {
        return creationTime;
    }

    public int getValidSeconds() {
        return validSeconds;
    }

    public void setValidSeconds(int validSeconds) {
        this.validSeconds = validSeconds;
    }

    public boolean isValid() {
        long duration = System.currentTimeMillis() - creationTime.getMillis();
        return (duration < TimeUnit.SECONDS.toMillis(validSeconds));
    }
}
