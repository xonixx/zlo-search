package info.xonix.forumsearch.xmlfp;

import info.xonix.forumsearch.xmlfp.jaxb_generated.Forum;
import info.xonix.forumsearch.xmlfp.jaxb_generated.Message;
import info.xonix.forumsearch.xmlfp.jaxb_generated.MessageListUrl;
import info.xonix.forumsearch.xmlfp.jaxb_generated.Messages;
import info.xonix.forumsearch.xmlfp.utils.MarshalUtils;
import info.xonix.utils.UrlUtil;
import info.xonix.forumsearch.xmlfp.utils.XmlFpMarshalException;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * User: gubarkov
 * Date: 12.02.12
 * Time: 0:33
 */
public class XmlFpForum {
    private Forum forum;

    private XmlFpForum() {
    }

    public static XmlFpForum fromDescriptorUrl(String descriptorUrl) throws XmlFpException {
        XmlFpForum xmlFpForum = new XmlFpForum();

        final byte[] descriptorXmlBytes = getXmlAsBytesFromUrl(descriptorUrl);

        try {
            xmlFpForum.forum = (Forum) XmlFpContext.getDescriptorUnmarshaller().unmarshal(new ByteArrayInputStream(descriptorXmlBytes));
        } catch (JAXBException e) {
            throw new XmlFpException(e, "Error unmarshalling");
        }

        xmlFpForum.absolutizeUrls(descriptorUrl);
        return xmlFpForum;
    }

    public static XmlFpForum fromDescriptorXmlString(String descriptorUrl, String descriptorXml) throws XmlFpException {
        XmlFpForum xmlFpForum = new XmlFpForum();

        try {
            xmlFpForum.forum = (Forum) XmlFpContext.getDescriptorUnmarshaller().unmarshal(new StringReader(descriptorXml));
        } catch (JAXBException e) {
            throw new XmlFpException(e, "Error unmarshalling");
        }

        xmlFpForum.absolutizeUrls(descriptorUrl);
        return xmlFpForum;
    }

    private void absolutizeUrls(String descriptorUrl) {
        forum.setUrl(UrlUtil.combineUrls(descriptorUrl, forum.getUrl()));

        final Forum.XmlfpUrls xmlfpUrls = forum.getXmlfpUrls();

        xmlfpUrls.setLastMessageNumberUrl(UrlUtil.combineUrls(descriptorUrl, xmlfpUrls.getLastMessageNumberUrl()));
        xmlfpUrls.setMessageUrl(UrlUtil.combineUrls(descriptorUrl, xmlfpUrls.getMessageUrl()));
        xmlfpUrls.getMessageListUrl().setValue(UrlUtil.combineUrls(descriptorUrl, xmlfpUrls.getMessageListUrl().getValue()));


        final Forum.ForumUrls forumUrls = forum.getForumUrls();
        forumUrls.setMessageUrl(UrlUtil.combineUrls(descriptorUrl, forumUrls.getMessageUrl()));
        forumUrls.setUserProfileUrl(UrlUtil.combineUrls(descriptorUrl, forumUrls.getUserProfileUrl()));
    }

    public String formDescriptorXml() throws XmlFpMarshalException {
        return MarshalUtils.marshal(XmlFpContext.getDescriptorMarshaller(), forum);
    }

    public long getLastMessageNumber() throws XmlFpException {
        final byte[] bytes = getXmlAsBytesFromUrl(forum.getXmlfpUrls().getLastMessageNumberUrl());

        try {
            @SuppressWarnings("unchecked")
            final JAXBElement<Long> res = (JAXBElement<Long>) XmlFpContext.getLastMessageNumberUnmarshaller()
                    .unmarshal(new ByteArrayInputStream(bytes));

            return res.getValue();
        } catch (JAXBException e) {
            throw new XmlFpException(e, "Error unmarshalling");
        }
    }

    public Message getMessage(long id) throws XmlFpException {
        final String messageUrl = forum.getXmlfpUrls().getMessageUrl();
        final String messageUrlFilled = messageUrl.replace(
                XmlFpUrlsSubstitutions.MESSAGE_ID, Long.toString(id));

        final byte[] bytes = getXmlAsBytesFromUrl(messageUrlFilled);

        try {
            return (Message) XmlFpContext.getMessageUnmarshaller()
                    .unmarshal(new ByteArrayInputStream(bytes));
        } catch (JAXBException e) {
            throw new XmlFpException(e, "Error unmarshalling");
        }
    }

    /**
     * @param from (including)
     * @param to   (including)
     * @return
     * @throws XmlFpException
     */
    public List<Message> getMessageList(long from, long to) throws XmlFpException {
        if (!supportsMessageListGeneration()) {
            throw new IllegalStateException(forum.getName() + " doesn't support msg list download");
        }

        final MessageListUrl mlu = forum.getXmlfpUrls().getMessageListUrl();
        final int maxCount = mlu.getMaxCount();

        if (to - from + 1 > maxCount) {
            throw new IllegalArgumentException("from - to > allowed maxCount = " + maxCount);
        }

        final String messageListUrl = mlu.getValue();
        final String messageListUrlFiller = messageListUrl
                .replace(XmlFpUrlsSubstitutions.FROM, Long.toString(from))
                .replace(XmlFpUrlsSubstitutions.TO, Long.toString(to));

        final byte[] bytes = getXmlAsBytesFromUrl(messageListUrlFiller);

        final Messages messages;
        try {
            messages = (Messages) XmlFpContext.getMessagesUnmarshaller().unmarshal(new ByteArrayInputStream(bytes));
        } catch (JAXBException e) {
            throw new XmlFpException(e, "Error unmarshalling");
        }

        List<Message> res = new ArrayList<Message>(messages.getMessage().size());

        for (Message jaxbMessage : messages.getMessage()) {
            res.add(jaxbMessage);
        }

        return res;
    }

    //TODO: implement retry for download
    private static byte[] getXmlAsBytesFromUrl(final String url) throws XmlFpException {
        final byte[] bytes;

        try {
            bytes = IOUtils.toByteArray(new URL(url).openStream());
        } catch (IOException e) {
            throw new XmlFpIOException(e, "Error accessing forum site");
        }

        return bytes;
    }

    public String getForumMessageUrl(long messageId) {
        return forum.getForumUrls().getMessageUrl()
                .replace(XmlFpUrlsSubstitutions.MESSAGE_ID, Long.toString(messageId));
    }

    public String getForumUserProfileUrl(String userId, String userName) {
        if (userId == null) {
            userId = "";
        }

        if (userName == null) {
            userName = "";
        }

        return forum.getForumUrls().getUserProfileUrl()
                .replace(XmlFpUrlsSubstitutions.USER_ID, userId)
                .replace(XmlFpUrlsSubstitutions.USER_NAME, userName);
    }

    public String getForumUrl() {
        return forum.getUrl();
    }

    public String getDescription() {
        return forum.getDescription();
    }

    public String getTitle() {
        return forum.getName();
    }

    public boolean supportsMessageListGeneration() {
        return forum.getXmlfpUrls().getMessageListUrl() != null;
    }

    public int getMessageListMaxCount() {
        return forum.getXmlfpUrls().getMessageListUrl().getMaxCount();
    }
}
