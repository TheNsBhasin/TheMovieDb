package in.codingninjas.themoviedb.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by nsbhasin on 23/07/17.
 */

public class RequestToken implements Serializable {
    @SerializedName("success")
    Boolean isSuccess;

    @SerializedName("expires_at")
    String expiry;

    @SerializedName("request_token")
    String requestToken;

    public RequestToken(Boolean isSuccess, String expiry, String requestToken) {
        this.isSuccess = isSuccess;
        this.expiry = expiry;
        this.requestToken = requestToken;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }
}
