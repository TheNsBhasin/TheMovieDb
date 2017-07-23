package in.codingninjas.themoviedb.data;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

import in.codingninjas.themoviedb.util.Constants;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class MovieVideo {
    @SerializedName("id")
    private String videoId;

    @SerializedName("iso_639_1")
    private String languageCode;

    @SerializedName("iso_3166_1")
    private String countryCode;

    @SerializedName("key")
    private String key;

    @SerializedName("name")
    private String name;

    @SerializedName("site")
    private String site;

    @SerializedName("size")
    private int size;

    @SerializedName("type")
    private String type;

    public MovieVideo(String videoId, String languageCode, String countryCode, String key, String name, String site, int size, String type) {
        this.videoId = videoId;
        this.languageCode = languageCode;
        this.countryCode = countryCode;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isYoutubeVideo() {
        return site.toLowerCase(Locale.US).equals(Constants.SITE_YOUTUBE.toLowerCase(Locale.US));
    }
}
