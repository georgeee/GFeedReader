package ru.georgeee.android.gfeedreader.utility.model;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 12.10.13
 * Time: 4:11
 * To change this template use File | Settings | File Templates.
 */
public class Feed {
    protected String title;
    protected String url;
    protected String iconUrl;
    protected String logoUrl;
    protected String description;

    protected ArrayList<Entry> entries = new ArrayList<Entry>();

    public void addEntry(Entry entry){
        entries.add(entry);
    }

    public int getEntryCount(){
        return entries.size();
    }

    public Entry getEntry(int i){
        return entries.get(i);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", description='" + description + '\'' +
                ", entries=" + entries +
                '}';
    }
}
