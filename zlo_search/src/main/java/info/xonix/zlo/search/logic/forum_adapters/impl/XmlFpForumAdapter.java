package info.xonix.zlo.search.logic.forum_adapters.impl;

import info.xonix.forumsearch.xmlfp.XmlFpForum;
import info.xonix.forumsearch.xmlfp.XmlFpException;
import info.xonix.forumsearch.xmlfp.XmlFpIOException;
import info.xonix.zlo.search.dao.XmlFpDao;
import info.xonix.zlo.search.logic.forum_adapters.ForumAccessException;
import info.xonix.zlo.search.logic.forum_adapters.ForumAdapterAbstract;
import info.xonix.zlo.search.logic.forum_adapters.ForumFormatException;
import info.xonix.zlo.search.logic.forum_adapters.ForumIoException;
import info.xonix.zlo.search.model.Message;
import info.xonix.forumsearch.xmlfp.utils.XmlFpMarshalException;
import info.xonix.zlo.search.xmlfp.ForumAccessor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * User: gubarkov
 * Date: 19.02.12
 * Time: 1:32
 */
public class XmlFpForumAdapter extends ForumAdapterAbstract
        implements InitializingBean {
    private final static Logger log = Logger.getLogger(XmlFpForumAdapter.class);

    @Autowired
    private XmlFpDao xmlFpDao;

    private XmlFpForum xmlFpForum;
    private ForumAccessor forumAccessor;
    private String descriptorUrl;

    public XmlFpForumAdapter(String descriptorUrl) {
        this.descriptorUrl = descriptorUrl;
    }

    @Override
    public void afterPropertiesSet() throws ForumAccessException {
        log.info("Initializing xmlfp adapter for: " + descriptorUrl + " ...");

        final String descriptorXmlInDb = xmlFpDao.getDescriptorXmlByUrl(descriptorUrl);

        try {
            xmlFpForum = XmlFpForum.fromDescriptorUrl(descriptorUrl);

            try {
                final String descriptorXml = xmlFpForum.formDescriptorXml();

                if (descriptorXmlInDb == null) {
                    log.info("Adding xmlfp descriptor to DB: " + descriptorUrl);

                    xmlFpDao.addDescriptorInfo(descriptorUrl, descriptorXml);

                } else if (!StringUtils.equals(descriptorXml, descriptorXmlInDb)) {
                    log.info("Updating xmlfp descriptor in DB: " + descriptorUrl);

                    xmlFpDao.updateDescriptorInfo(descriptorUrl, descriptorXml);
                }
            } catch (XmlFpMarshalException e) {
                log.error("Unable to update xmlfp info in DB", e);
            }
        } catch (XmlFpException e) {
            if (descriptorXmlInDb != null) {
                log.warn("Unable to get xmlfp info from forum, loading from DB, err: ", e);

                try {
                    xmlFpForum = XmlFpForum.fromDescriptorXmlString(descriptorUrl, descriptorXmlInDb);
                } catch (XmlFpException e1) {
                    throw translateException(e1, "Can't create XMLFP XmlFpForum from xml: " + descriptorXmlInDb);
                }
            }

            throw translateException(e, "Can't create XMLFP XmlFpForum for descriptorUrl: " + descriptorUrl);
        }

        forumAccessor = new ForumAccessor(xmlFpForum);
    }

    private ForumAccessException translateException(XmlFpException ex, String msg) {
        if (ex instanceof XmlFpIOException) {
            return new ForumIoException(msg, ex);
        } else {
            return new ForumFormatException(msg, ex);
        }
    }

    @Override
    public long getLastMessageNumber(String forumId) throws ForumAccessException {
        try {
            return xmlFpForum.getLastMessageNumber();
        } catch (XmlFpException e) {
            throw translateException(e, "Can't get last number");
        }
    }

    @Override
    public Message getMessage(String forumId, long messageId) throws ForumAccessException {
        try {
            return forumAccessor.getMessage(messageId);
        } catch (XmlFpException e) {
            throw translateException(e, "Can't get message #" + messageId);
        }
    }

    @Override
    public List<Message> getMessages(String forumId, final long from, final long to) throws ForumAccessException {
        long xmlfpTo = to - 1;
        if (from > xmlfpTo) {
            throw new IllegalArgumentException("from=" + from + " > to=" + xmlfpTo);
        }

        if (xmlFpForum.supportsMessageListGeneration()) {
            try {
                final int maxCount = xmlFpForum.getMessageListMaxCount();

                long currentFrom = from;
                long currentTo = Math.min(xmlfpTo, from + maxCount - 1);

                List<Message> res = new ArrayList<Message>((int) (xmlfpTo - from + 1));

                while (currentFrom <= xmlfpTo) {
                    log.info(forumId + " : getting part [" + currentFrom + " to " + currentTo +
                            "] of [" + from + " to " + xmlfpTo + "]");

                    res.addAll(forumAccessor.getMessageList(currentFrom, currentTo));
                    currentFrom = currentTo + 1;
                    currentTo = Math.min(xmlfpTo, currentFrom + maxCount - 1);
                }
                return res;
            } catch (XmlFpException e) {
                throw translateException(e, "Can't download msg list from=" + from + " to=" + xmlfpTo);
            }
        } else {
            return super.getMessages(forumId, from, xmlfpTo);
        }
    }

    @Override
    public String prepareMessageUrl(long messageId) {
        return xmlFpForum.getForumMessageUrl(messageId);
    }

    @Override
    public String prepareUserProfileUrl(String userId, String userName) {
        return xmlFpForum.getForumUserProfileUrl(userId, userName);
    }

    @Override
    public String getForumUrl() {
        return xmlFpForum.getForumUrl();
    }

    @Override
    public String getForumTitle() {
        return xmlFpForum.getTitle();
    }

    @Override
    public long extractMessageIdFromMessageUrl(String messageUrl) {
//        throw new UnsupportedOperationException("TBD");
        return super.extractMessageIdFromMessageUrl(messageUrl); // TODO !!!
    }

    @Override
    public boolean supportsParents() {
        return true;
    }

    public XmlFpForum getXmlFpForum() {
        return xmlFpForum;
    }
}
