package ru.georgeee.android.gfeedreader.utility.model;

import android.os.SystemClock;
import org.apache.http.impl.cookie.DateUtils;
import ru.georgeee.android.gfeedreader.utility.xml.FeedReaderTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 12.10.13
 * Time: 4:11
 * To change this template use File | Settings | File Templates.
 */
public class Feed implements Serializable{
    protected WebString title;
    protected String url;
    protected String iconUrl;
    protected String logoUrl;
    protected WebString description;
    protected String feedUrl;
    protected Date lastUpdated;

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public WebString getDescription() {
        return description;
    }

    public void setDescription(WebString description) {
        this.description = description;
    }
    public void setDescription(String description) {
        setDescription(new WebString(description));
    }

    @Override
    public String toString() {
        return "Feed{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }
}
