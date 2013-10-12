package ru.georgeee.android.gfeedreader.utility.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 12.10.13
 * Time: 15:41
 * To change this template use File | Settings | File Templates.
 */
public class WebString implements Serializable {
    protected String text;
    protected String type;
    protected boolean base64;
    protected boolean isUrl;

    public WebString(String text, String type, boolean base64, boolean isUrl) {
        this.text = text;
        this.base64 = base64;
        this.type = type;
        this.isUrl = isUrl;

    }

    public WebString(String text, String type) {
        this(text, type, false, false);
    }

    public WebString(String text) {
        this(text, "text/html; charset=UTF-8");

    }

    @Override
    public String toString() {
        return "WebString{" +
                "text='" + text + '\'' +
                ", type='" + type + '\'' +
                ", base64=" + base64 +
                ", isUrl=" + isUrl +
                '}';
    }

    public boolean isUrl() {
        return isUrl;
    }

    public void setUrl(boolean url) {
        isUrl = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isBase64() {
        return base64;
    }

    public void setBase64(boolean base64) {
        this.base64 = base64;
    }
}
