package in.codingninjas.themoviedb.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by nsbhasin on 23/07/17.
 */

public class Account implements Serializable {
    @SerializedName("avatar")
    Avatar avatar;

    @SerializedName("id")
    long id;

    @SerializedName("iso_639_1")
    String languageCode;

    @SerializedName("iso_3166_1")
    String countryCode;

    @SerializedName("include_adult")
    Boolean includeAdult;

    @SerializedName("username")
    String username;

    public Account(Avatar avatar, long id, String languageCode, String countryCode, Boolean includeAdult, String username) {
        this.avatar = avatar;
        this.id = id;
        this.languageCode = languageCode;
        this.countryCode = countryCode;
        this.includeAdult = includeAdult;
        this.username = username;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Boolean getIncludeAdult() {
        return includeAdult;
    }

    public void setIncludeAdult(Boolean includeAdult) {
        this.includeAdult = includeAdult;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public class Avatar {
        @SerializedName("gravatar")
        Gravatar gravatar;

        public Avatar(Gravatar gravatar) {
            this.gravatar = gravatar;
        }

        public Gravatar getGravatar() {
            return gravatar;
        }

        public void setGravatar(Gravatar gravatar) {
            this.gravatar = gravatar;
        }

        public class Gravatar {
            @SerializedName("hash")
            String hash;

            public Gravatar(String hash) {
                this.hash = hash;
            }

            public String getHash() {
                return hash;
            }

            public void setHash(String hash) {
                this.hash = hash;
            }
        }
    }
}
