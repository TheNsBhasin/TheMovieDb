package in.codingninjas.themoviedb.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class MovieReview implements Serializable {
    @SerializedName("id")
    private String reviewId;

    @SerializedName("author")
    private String author;

    @SerializedName("url")
    private String reviewUrl;

    @SerializedName("content")
    private String content;

    public MovieReview(String reviewId) {
        this.reviewId = reviewId;
    }

    public MovieReview(String reviewId, String author, String reviewUrl, String content) {
        this.reviewId = reviewId;
        this.author = author;
        this.reviewUrl = reviewUrl;
        this.content = content;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}