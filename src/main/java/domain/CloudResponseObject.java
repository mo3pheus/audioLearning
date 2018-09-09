package domain;

public class CloudResponseObject {
    private double         log_id;
    private ResultsTuple[] results;

    public double getLog_id() {
        return log_id;
    }

    public void setLog_id(double log_id) {
        this.log_id = log_id;
    }

    public ResultsTuple[] getResults() {
        return results;
    }

    public void setResults(ResultsTuple[] results) {
        this.results = results;
    }
}
