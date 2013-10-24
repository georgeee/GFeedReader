package ru.georgeee.android.gfeedreader.utility.xml;

import android.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.georgeee.android.gfeedreader.utility.Storage;
import ru.georgeee.android.gfeedreader.utility.http.HttpUtility;
import ru.georgeee.android.gfeedreader.utility.model.Entry;
import ru.georgeee.android.gfeedreader.utility.model.Feed;
import ru.georgeee.android.gfeedreader.utility.model.WebString;

import java.net.MalformedURLException;
import java.net.URL;
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
    public static final String ATOM_XMLNS = "http://www.w3.org/2005/Atom";
    protected static final int FEED_FORMAT_UNDEFINED = 0;
    protected static final int FEED_FORMAT_RSS = 1;
    protected static final int FEED_FORMAT_ATOM = 2;
    protected int feedFormat;
    protected Feed feed;
    protected Entry entry;
    ArrayList<TagHandler> tagStack;
    ArrayList<StringBuilder> buffers;
    ArrayList<URL> urlBases;
    URL documentSrcUrl;

    public FeedReader() throws FeedReaderException {
        this(null);
    }

    public FeedReader(String documentSrcUrl) throws FeedReaderException {
        try {
            this.documentSrcUrl = new URL(documentSrcUrl);
        } catch (MalformedURLException e) {
            throw new FeedReaderException(e);
        }
    }

    protected static WebString parseAtomText(String text, String type) {
        return parseAtomText(text, type, null);
    }

    protected static WebString parseAtomText(String text, String type, String src) {
        String charsetPart = "; charset=UTF-8";
        if (type.equals("text") || type==null) return new WebString(text, "text/plain"+charsetPart);
        if (type.equals("html")) return new WebString(text, "text/html"+charsetPart);
        if (type.equals("xhtml")) return new WebString(text, "text/xhtml"+charsetPart);
        if (src != null) {
            return new WebString(src, type, false, true);
        }
        if (type.endsWith("/xml")
                || type.endsWith("+xml")
                || type.startsWith("text")) return new WebString(text, type+charsetPart);
        return new WebString(text, type, true, false);
    }

    protected URL getUrlBase() {
        if (!urlBases.isEmpty()) return urlBases.get(urlBases.size() - 1);
        return documentSrcUrl;
    }

    protected URL createUrlWithBase(String url) throws MalformedURLException {
        URL baseUrl = getUrlBase();
        URL result = new URL(baseUrl, url);
        Log.d("FeedReader", "createUrlWithBase(" + url + "): base=" + baseUrl + " = " + result);
        return result;
    }

    @Override
    public void startDocument() throws SAXException {
        feedFormat = FEED_FORMAT_UNDEFINED;
        feed = null;
        entry = null;
        tagStack = new ArrayList<TagHandler>();
        buffers = new ArrayList<StringBuilder>();
        urlBases = new ArrayList<URL>();
    }

    @Override
    public void endDocument() throws SAXException {
        if(feed != null) Storage.getInstance().saveFeed(feed);
    }

    protected boolean checkPathInStack(String... components) {
        if (components.length != tagStack.size()) return false;
        for (int i = 0; i < components.length; ++i) {
            if (!tagStack.get(i).localName.equals(components[i])) return false;
        }
        return true;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        TagHandler tagHandler = null;
        boolean isUriEmpty = (uri == null || uri.isEmpty()) || (feedFormat == FEED_FORMAT_ATOM && uri.equals(ATOM_XMLNS));
        if (feedFormat == FEED_FORMAT_UNDEFINED) {
            if ((isUriEmpty || uri.equals(ATOM_XMLNS)) && localName.equals("feed")) {
                feedFormat = FEED_FORMAT_ATOM;
                feed = new Feed();
                feed.setLastUpdated(new Date());
                feed.setFeedUrl(documentSrcUrl.toString());
            } else if (isUriEmpty && localName.equals("rss")) feedFormat = FEED_FORMAT_RSS;
            else {
                throw new FeedReaderException("Unknown feeds format : localName=" + localName + " uri=" + uri + " qName=" + qName);
            }
        } else if (feedFormat == FEED_FORMAT_RSS) {
            if (feed == null) {//Inside rss tag
                if (isUriEmpty && localName.equals("channel")) {
                    feed = new Feed();
                    feed.setLastUpdated(new Date());
                    feed.setFeedUrl(documentSrcUrl.toString());
                } else throw new FeedReaderException("Channel should be the only child of rss tag");
            } else if (entry == null) {//Inside channel tag
                if (isUriEmpty) {
                    if (localName.equals("item")) {
                        entry = new Entry();
                        entry.setFeedUrl(feed.getFeedUrl());
                        tagHandler = new TagHandler(false, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                Storage.getInstance().saveEntry(entry);
                                entry = null;
                            }
                        };
                    } else if (localName.equals("title")) {
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                feed.setTitle(content);
                            }
                        };
                    } else if (localName.equals("link")) {
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                feed.setUrl(content);
                            }
                        };
                    } else if (localName.equals("description")) {
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                feed.setDescription(content);
                            }
                        };
                    } else if (localName.equals("url") && checkPathInStack("rss", "channel", "image")) { //rss->channel->image->url
                        tagHandler = new TagHandler(true, uri, localName, qName) {
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
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                entry.setTitle(content);
                            }
                        };
                    } else if (localName.equals("link")) {
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                entry.setUrl(content);
                            }
                        };
                    } else if (localName.equals("description")) {
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                entry.setDescription(content);
                            }
                        };
                    } else if (localName.equals("guid")) {
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                entry.setId(content);
                            }
                        };
                    } else if (localName.equals("pubDate")) {
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                entry.setPubDate(HttpUtility.parseRfc822DateString(content));
                            }
                        };
                    }
                }
            }
        } else if (feedFormat == FEED_FORMAT_ATOM) {
            final String type = attributes.getValue("type") == null ? "text" : attributes.getValue("type");
            if (entry == null) {
                if (isUriEmpty) {
                    if (localName.equals("entry")) {
                        entry = new Entry();
                        entry.setFeedUrl(feed.getFeedUrl());
                        tagHandler = new TagHandler(false, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                Storage.getInstance().saveEntry(entry);
                                entry = null;
                            }
                        };
                    } else if (localName.equals("title")) {
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                feed.setTitle(parseAtomText(content, type));
                            }
                        };
                    } else if (localName.equals("link")) {
                        String href = attributes.getValue("href");
                        String rel = attributes.getValue("rel");
                        try {
                            if (rel == null || rel.equals("alternate"))
                                feed.setUrl(createUrlWithBase(href).toString());
                            else if (rel.equals("self"))
                                documentSrcUrl = createUrlWithBase(href);
                        } catch (MalformedURLException e) {
                            throw new FeedReaderException(e);
                        }
                    } else if (localName.equals("subtitle")) {
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                feed.setDescription(parseAtomText(content, type));
                            }
                        };
                    } else if (localName.equals("logo")) {
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) throws FeedReaderException {
                                try {
                                    feed.setLogoUrl(createUrlWithBase(content).toString());
                                } catch (MalformedURLException e) {
                                    throw new FeedReaderException(e);
                                }
                            }
                        };
                    } else if (localName.equals("icon")) {
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) throws FeedReaderException {
                                try {
                                    feed.setIconUrl(createUrlWithBase(content).toString());
                                } catch (MalformedURLException e) {
                                    throw new FeedReaderException(e);
                                }
                            }
                        };
                    }
                }
            } else {
                if (isUriEmpty) {
                    if (localName.equals("title")) {
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                entry.setTitle(parseAtomText(content, type));
                            }
                        };
                    } else if (localName.equals("link")) {
                        String href = attributes.getValue("href");
                        String rel = attributes.getValue("rel");
                        try {
                            if (rel == null || rel.equals("alternate"))
                                entry.setUrl(createUrlWithBase(href).toString());
                        } catch (MalformedURLException e) {
                            throw new FeedReaderException(e);
                        }
                    } else if (localName.equals("summary")) {
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                entry.setSummary(parseAtomText(content, type));
                            }
                        };
                    } else if (localName.equals("content")) {
                        String _src = attributes.getValue("src");
                        if (_src != null) {
                            try {
                                _src = (createUrlWithBase(_src).toString());
                            } catch (MalformedURLException e) {
                                throw new FeedReaderException(e);
                            }
                        }
                        final String src = _src;
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                entry.setContent(parseAtomText(content, type, src));
                            }
                        };
                    } else if (localName.equals("id")) {
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                entry.setId(content);
                            }
                        };
                    } else if (localName.equals("updated")) {
                        tagHandler = new TagHandler(true, uri, localName, qName) {
                            @Override
                            void onClose(String content) {
                                entry.setPubDate(HttpUtility.parseRfc3339DateString(content));
                            }
                        };
                    }
                }
            }
        }
        if (tagHandler == null) {
            tagHandler = new TagHandler(false, uri, localName, qName);
        }
        tagHandler.open(attributes);
        tagStack.add(tagHandler);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        for (StringBuilder sb : buffers) {
            sb.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        tagStack.remove(tagStack.size() - 1).close(uri, localName, qName);
    }

    public Feed getResult() {
        return feed;
    }

    protected class TagHandler {
        boolean captureContent;
        String uri;
        String localName;
        String qName;
        boolean xmlBase = false;

        public TagHandler(boolean captureContent, String uri, String localName, String qName) {
            this.captureContent = captureContent;
            this.uri = uri;
            this.localName = localName;
            this.qName = qName;
        }

        void open(Attributes attributes) throws FeedReaderException {
            if (captureContent) buffers.add(new StringBuilder());
            if (attributes.getValue("xml:base") != null) {
                try {
                    urlBases.add(createUrlWithBase(attributes.getValue("xml:base")));
                    xmlBase = true;
                } catch (MalformedURLException ex) {
                    throw new FeedReaderException(ex);
                }
            }
            onOpen(attributes);
        }

        void onOpen(Attributes attributes) throws FeedReaderException {

        }

        private boolean objsEqual(Object obj1, Object obj2) {
            return (obj1 == null && obj2 == null) || (obj1 != null && obj1.equals(obj2));
        }

        void close(String _uri, String _localName, String _qName) throws FeedReaderException {
            if (!objsEqual(uri, _uri) || !objsEqual(localName, _localName) || !objsEqual(qName, _qName)) {
                throw new FeedReaderException("Openning and closing tags do not match");
            }
            onClose(captureContent ? buffers.remove(buffers.size() - 1).toString() : null);
            if (xmlBase) {
                urlBases.remove(urlBases.size() - 1);
            }
        }

        void onClose(String content) throws FeedReaderException {

        }
    }
}
