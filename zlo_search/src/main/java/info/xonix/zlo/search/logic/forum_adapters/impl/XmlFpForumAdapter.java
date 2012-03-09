package info.xonix.zlo.search.logic.forum_adapters.impl;

import info.xonix.zlo.search.dao.XmlFpDao;
import info.xonix.zlo.search.logic.forum_adapters.ForumAccessException;
import info.xonix.zlo.search.logic.forum_adapters.ForumAdapterAbstract;
import info.xonix.zlo.search.logic.forum_adapters.ForumFormatException;
import info.xonix.zlo.search.logic.forum_adapters.ForumIoException;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.xmlfp.ForumAccessor;
import info.xonix.zlo.search.xmlfp.XmlFpException;
import info.xonix.zlo.search.xmlfp.XmlFpIOException;
import info.xonix.zlo.search.xmlfp.utils.XmlFpMarshalException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: gubarkov
 * Date: 19.02.12
 * Time: 1:32
 */
public class XmlFpForumAdapter extends ForumAdapterAbstract {
    private final static Logger log = Logger.getLogger(XmlFpForumAdapter.class);

    @Autowired
    private XmlFpDao xmlFpDao;

    private ForumAccessor forumAccessor;

    public XmlFpForumAdapter(String descriptorUrl) throws ForumAccessException {
        final String descriptorXmlInDb = xmlFpDao.getDescriptorXmlByUrl(descriptorUrl);

        try {
            forumAccessor = ForumAccessor.fromDescriptorUrl(descriptorUrl);

            try {
                final String descriptorXml = forumAccessor.formDescriptorXml();

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
                    forumAccessor = ForumAccessor.fromDescriptorXmlString(descriptorUrl, descriptorXmlInDb);
                } catch (XmlFpException e1) {
                    throw translateException(e1, "Can't create XMLFP ForumAccessor from xml: " + descriptorXmlInDb);
                }
            }

            throw translateException(e, "Can't create XMLFP ForumAccessor for descriptorUrl: " + descriptorUrl);
        }
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
            return forumAccessor.getLastMessageNumber();
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
    public String prepareMessageUrl(long messageId) {
        return forumAccessor.getForumMessageUrl(messageId);
    }

    @Override
    public String prepareUserProfileUrl(long userId, String userName) {
        return forumAccessor.getForumUserProfileUrl(userId, userName);
    }

    @Override
    public String getForumUrl() {
        return forumAccessor.getForumUrl();
    }

    @Override
    public String getForumTitle() {
        return forumAccessor.getTitle();
    }

    @Override
    public long extractMessageIdFromMessageUrl(String messageUrl) {
        throw new UnsupportedOperationException("TBD");
    }
}
