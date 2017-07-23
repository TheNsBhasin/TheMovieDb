package in.codingninjas.themoviedb.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nsbhasin on 23/07/17.
 */

public class Session {
    @SerializedName("status_code")
    Integer statusCode;

    @SerializedName("status_message")
    String statusMessage;

    @SerializedName("success")
    Boolean isSuccess;

    @SerializedName("session_id")
    String sessionId;

    public Session(Integer statusCode, String statusMessage, Boolean isSuccess, String sessionId) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.isSuccess = isSuccess;
        this.sessionId = sessionId;
    }

    public Session(Integer statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public Session(Boolean isSuccess, String sessionId) {
        this.isSuccess = isSuccess;
        this.sessionId = sessionId;
    }


    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
