package ru.georgeee.android.gfeedreader.utility.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.georgeee.android.gfeedreader.utility.http.HttpUtility;
import ru.georgeee.android.gfeedreader.utility.model.Entry;
import ru.georgeee.android.gfeedreader.utility.model.Feed;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 12.10.13
 * Time: 4:09
 * To change this template use File | Settings | File Templates.
 */
public class FeedReader extends DefaultHandler implements SAXHandler<Feed> {
    protected static final int FEED_FORMAT_UNDEFINED = 0;
    protected static final int FEED_FORMAT_RSS = 1;
    protected static final int FEED_FORMAT_ATOM = 2;
    protected int feedFormat;
    protected Feed feed;
    protected Entry entry = null;
    ArrayList<TagHandler> tagStack;
    ArrayList<StringBuilder> buffers;

    @Override
    public void startDocument() throws SAXException {
        feedFormat = FEED_FORMAT_UNDEFINED;
        entry = null;
        tagStack = new ArrayList<TagHandler>();
        buffers = new ArrayList<StringBuilder>();
    }

    @Override
    public void endDocument() throws SAXException {

    }

    protected boolean checkPathInStack(String ... components){
        if(components.length != tagStack.size()) return false;
        for(int i=0; i< components.length; ++i){
            if(!tagStack.get(i).localName.equals(components[i])) return false;
        }
        return true;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        TagHandler tagHandler = null;
        boolean isUriEmpty = uri == null || uri.isEmpty();
        if (feedFormat == FEED_FORMAT_UNDEFINED) {
            if (isUriEmpty && localName.equals("feed")) {
                feedFormat = FEED_FORMAT_ATOM;
                feed = new Feed();
            } else if (isUriEmpty && localName.equals("rss")) feedFormat = FEED_FORMAT_RSS;
            else throw new FeedReaderException("Unknown feed format : localName="+localName+" uri="+uri+" qName="+qName);
        } else if (feedFormat == FEED_FORMAT_RSS) {
            if (feed == null) {//Inside rss tag
                if (isUriEmpty && localName.equals("channel")) {
                    feed = new Feed();
                } else throw new FeedReaderException("Channel should be the only child of rss tag");
            } else if (entry == null) {//Inside channel tag
                if (isUriEmpty) {
                    if(localName.equals("item")){
                        entry = new Entry();
                        tagHandler = new TagHandler(true, uri, localName, qName, attributes){
                            @Override
                            void onClose(String content) {
                                feed.addEntry(entry);
                                entry = null;
                            }
                        };
                    }else if (localName.equals("title")) {
                        tagHandler = new TagHandler(true, uri, localName, qName, attributes){
                            @Override
                            void onClose(String content) {
                                feed.setTitle(content);
                            }
                        };
                    } else if (localName.equals("link")) {
                        tagHandler = new TagHandler(true, uri, localName, qName, attributes){
                            @Override
                            void onClose(String content) {
                                feed.setUrl(content);
                            }
                        };
                    } else if (localName.equals("description")) {
                        tagHandler = new TagHandler(true, uri, localName, qName, attributes){
                            @Override
                            void onClose(String content) {
                                feed.setDescription(content);
                            }
                        };
                    } else if(localName.equals("url") && checkPathInStack("rss", "channel", "image")){ //rss->channel->image->url
                        tagHandler = new TagHandler(true, uri, localName, qName, attributes){
                            @Override
                            void onClose(String content) {
                                feed.setLogoUrl(content);
                            }
                        };
                    }
                }
            } else {//Inside entry tag
                if (isUriEmpty) {
                    if (localName.equals("title")) {
                        tagHandler = new TagHandler(true, uri, localName, qName, attributes){
                            @Override
                            void onClose(String content) {
                                entry.setTitle(content);
                            }
                        };
                    } else if (localName.equals("link")) {
                        tagHandler = new TagHandler(true, uri, localName, qName, attributes){
                            @Override
                            void onClose(String content) {
                                entry.setUrl(content);
                            }
                        };
                    } else if (localName.equals("description")) {
                        tagHandler = new TagHandler(true, uri, localName, qName, attributes){
                            @Override
                            void onClose(String content) {
                                entry.setDescription(content);
                            }
                        };
                    } else if(localName.equals("guid")){
                        tagHandler = new TagHandler(true, uri, localName, qName, attributes){
                            @Override
                            void onClose(String content) {
                                entry.setId(content);
                            }
                        };
                    } else if(localName.equals("pubDate")){
                        tagHandler = new TagHandler(true, uri, localName, qName, attributes){
                            @Override
                            void onClose(String content) {
                                entry.setPubDateTS(HttpUtility.parseRfc822DateString(content).getTime());
                            }
                        };
                    }
                }
            }
        } else if (feedFormat == FEED_FORMAT_ATOM) {

        }
        if(tagHandler == null){
            tagHandler = new TagHandler(false, uri, localName, qName, attributes);
        }
        tagHandler.open();
        tagStack.add(tagHandler);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        for(StringBuilder sb : buffers){
            sb.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        tagStack.remove(tagStack.size()-1).close(uri,localName,qName);
    }

    public Feed getResult() {
        return feed;
    }

    protected class TagHandler {
        boolean captureContent;
        String uri;
        String localName;
        String qName;
        Attributes attributes;

        public TagHandler(boolean captureContent, String uri, String localName, String qName, Attributes attributes) {
            this.captureContent = captureContent;
            this.uri = uri;
            this.localName = localName;
            this.qName = qName;
            this.attributes = attributes;
        }

        void open() {
            if (captureContent) buffers.add(new StringBuilder());
        }

        private boolean objsEqual(Object obj1, Object obj2){
            return (obj1 == null && obj2 == null) || (obj1 != null && obj1.equals(obj2));
        }

        void close(String _uri, String _localName, String _qName) throws FeedReaderException {
            if(!objsEqual(uri, _uri) || !objsEqual(localName, _localName) || !objsEqual(qName, _qName)){
                throw new FeedReaderException("Openning and closing tags do not match");
            }
            onClose(captureContent ? buffers.remove(buffers.size() - 1).toString() : null);
        }

        void onClose(String content) {

        }
    }
}
