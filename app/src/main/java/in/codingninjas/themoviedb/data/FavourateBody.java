package in.codingninjas.themoviedb.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nsbhasin on 04/08/17.
 */

public class FavourateBody {

    @SerializedName("media_type")
    private String media_type;

    @SerializedName("media_id")
    private long media_id;

    @SerializedName("favorite")
    private boolean favorite;

    public FavourateBody(String media_type, long media_id, boolean favorite) {
        this.media_type = media_type;
        this.media_id = media_id;
        this.favorite = favorite;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public long getMedia_id() {
        return media_id;
    }

    public void setMedia_id(long media_id) {
        this.media_id = media_id;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
