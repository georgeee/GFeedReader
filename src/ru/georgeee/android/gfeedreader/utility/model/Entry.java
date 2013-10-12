package ru.georgeee.android.gfeedreader.utility.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 12.10.13
 * Time: 4:39
 * To change this template use File | Settings | File Templates.
 */
public class Entry  implements Serializable {
    protected String id;
    protected WebString title;
    protected String url;
    protected WebString content;
    protected Date pubDate;
    protected String imageUrl;

    public WebString getSummary() {
        return summary;
    }

    public void setSummary(WebString summary) {
        this.summary = summary;
    }

    protected WebString summary;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WebString getTitle() {
        return title;
    }

    public void setTitle(WebString title) {
        this.title = title;
    }
    public void setTitle(String description) {
        setTitle(new WebString(description));
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WebString getContent() {
        return content;
    }

    public void setContent(WebString content) {
        this.content = content;
    }
    public void setDescription(String description) {
        setContent(new WebString(description));
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
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
                ", content='" + content + '\'' +
                ", pubDateTS=" + pubDate +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
