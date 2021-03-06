package server.commands;

public class Result {
    private final String response;
    private final String value;
    private final String reason;

    public Result(String response, String value, String reason) {
        this.response = response;
        this.value = value;
        this.reason = reason;
    }

    public Result(String response, String value) {
        this.response = response;
        this.value = value;
        this.reason = null;
    }

    public Result(String response) {
        this.response = response;
        this.value = null;
        this.reason = null;
    }

    public String getResponse() {
        return response;
    }

    public String getValue() {
        return value;
    }

    public String getReason() {
        return reason;
    }
}
