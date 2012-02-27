package info.xonix.zlo.search.xmlfp;

import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.xmlfp.jaxb_generated.Forum;
import info.xonix.zlo.search.xmlfp.utils.UrlUtil;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * User: gubarkov
 * Date: 12.02.12
 * Time: 0:33
 */
public class ForumAccessor {
    private XmlFpUrls xmlFpUrls;
    private String forumMessageUrlPattern;
    private String forumUserProfileUrlPattern;

    public ForumAccessor(XmlFpUrls xmlFpUrls) {
        this.xmlFpUrls = xmlFpUrls;
    }

    public ForumAccessor(String descriptorUrl) throws XmlFpException {
        final byte[] descriptorXmlBytes = getXmlAsBytesFromUrl(descriptorUrl);

        final Forum forum;
        try {
            forum = (Forum) XmlFpContext.getUnmarshaller().unmarshal(new ByteArrayInputStream(descriptorXmlBytes));
        } catch (JAXBException e) {
            throw new XmlFpException(e, "Error unmarshalling");
        }

        this.xmlFpUrls = new XmlFpUrls(
                UrlUtil.combineUrls(descriptorUrl, forum.getXmlfpUrls().getLastMessageNumberUrl()),
                UrlUtil.combineUrls(descriptorUrl, forum.getXmlfpUrls().getMessageUrl())
        );

        this.forumMessageUrlPattern = UrlUtil.combineUrls(descriptorUrl, forum.getForumUrls().getMessageUrl());
        this.forumUserProfileUrlPattern = UrlUtil.combineUrls(descriptorUrl, forum.getForumUrls().getUserProfileUrl());
    }

    public long getLastMessageNumber() throws XmlFpException {
        final byte[] bytes = getXmlAsBytesFromUrl(xmlFpUrls.getLastMessageNumberUrl());

        try {
            @SuppressWarnings("unchecked")
            final JAXBElement<Long> res = (JAXBElement<Long>) XmlFpContext.getUnmarshaller()
                    .unmarshal(new ByteArrayInputStream(bytes));

            return res.getValue();
        } catch (JAXBException e) {
            throw new XmlFpException(e, "Error unmarshalling");
        }
    }

    public Message getMessage(long id) throws XmlFpException {
        final String messageUrl = xmlFpUrls.getMessageUrl();
        final String messageUrlFilled = messageUrl.replace(
                XmlFpUrlsSubstitutions.MESSAGE_ID, Long.toString(id));

        final byte[] bytes = getXmlAsBytesFromUrl(messageUrlFilled);

        try {
            @SuppressWarnings("unchecked")
            final info.xonix.zlo.search.xmlfp.jaxb_generated.Message jaxbMessage = (info.xonix.zlo.search.xmlfp.jaxb_generated.Message) XmlFpContext.getUnmarshaller()
                    .unmarshal(new ByteArrayInputStream(bytes));

            return Convert.fromJaxbMessage(jaxbMessage);
        } catch (JAXBException e) {
            throw new XmlFpException(e, "Error unmarshalling");
        }

    }

    //TODO: implement retry for download
    private byte[] getXmlAsBytesFromUrl(final String url) throws XmlFpException {
        final byte[] bytes;

        try {
            bytes = IOUtils.toByteArray(new URL(url).openStream());
        } catch (IOException e) {
            throw new XmlFpIOException(e, "Error accessing forum site");
        }

        return bytes;
    }

    public String getForumMessageUrl(long messageId) {
        return forumMessageUrlPattern
                .replace(XmlFpUrlsSubstitutions.MESSAGE_ID, Long.toString(messageId));
    }

    public String getForumUserProfileUrl(long userId, String userName) {
        return forumUserProfileUrlPattern
                .replace(XmlFpUrlsSubstitutions.USER_ID, Long.toString(userId))
                .replace(XmlFpUrlsSubstitutions.USER_NAME, userName);
    }
}
