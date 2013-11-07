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
public class Entry implements Serializable, Comparable<Entry> {
    protected long entryId;
    protected long feedId;
    protected String id;
    protected WebString title;
    protected WebString content;
    protected WebString summary;
    protected String url;
    protected Date pubDate;
    protected String imageUrl;
    protected String feedUrl;

    public long getEntryId() {
        return entryId;
    }

    public void setEntryId(long entryId) {
        this.entryId = entryId;
    }

    public long getFeedId() {
        return feedId;
    }

    public void setFeedId(long feedId) {
        this.feedId = feedId;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public WebString getSummary() {
        return summary;
    }

    public void setSummary(WebString summary) {
        this.summary = summary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entry)) return false;
        return getUniqueIdentifier().equals(((Entry) o).getUniqueIdentifier());
    }

    @Override
    public int hashCode() {
        return getUniqueIdentifier().hashCode();
    }

    public String getUniqueIdentifier() {
        if (id != null) return id;
        if (url != null) return feedUrl + "  " + url;
        if (title != null) return feedUrl + "  " + title;
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WebString getTitle() {
        return title;
    }

    public void setTitle(String description) {
        setTitle(new WebString(description));
    }

    public void setTitle(WebString title) {
        this.title = title;
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

    @Override
    public int compareTo(Entry entry) {
        int cmpr = pubDate.compareTo(entry.pubDate);
        if (cmpr != 0) return cmpr;
        else return getUniqueIdentifier().compareTo(entry.getUniqueIdentifier());
    }
}
