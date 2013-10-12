package ru.georgeee.android.gfeedreader.utility.model;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 12.10.13
 * Time: 4:39
 * To change this template use File | Settings | File Templates.
 */
public class Entry {
    protected String id;
    protected String title;
    protected String url;
    protected String description;
    protected long pubDateTS;
    protected String imageUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPubDateTS() {
        return pubDateTS;
    }

    public void setPubDateTS(long pubDateTS) {
        this.pubDateTS = pubDateTS;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", pubDateTS=" + pubDateTS +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
