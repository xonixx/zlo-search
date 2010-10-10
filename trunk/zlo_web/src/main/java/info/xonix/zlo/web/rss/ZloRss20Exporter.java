package info.xonix.zlo.web.rss;

import de.nava.informa.core.CategoryIF;
import de.nava.informa.core.ChannelExporterIF;
import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemIF;
import de.nava.informa.utils.ParserUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * Author: Vovan
 * Date: 28.05.2008
 * Time: 18:05:02
 */

/*
copy-paste of RSS_2_0_Exporter + change
 */
public class ZloRss20Exporter implements ChannelExporterIF {

    private static final String RSS_VERSION = "2.0";

    /**
     * RSS 1.0 Dublin Core namespace
     */
    private static final String NS_DC = "http://purl.org/dc/elements/1.1/";

    /**
     * RSS 1.0 Syndication Module namespace
     */
    private static final String NS_SY = "http://purl.org/rss/1.0/modules/syndication/";

    private static final String NS_ADMIN = "http://webns.net/mvcb/";

    //private static final String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    private Writer writer;

    private String encoding;

    private String cssStylesheet;
    private String xslStylesheet;

    /**
     * Creates a channel exporter bound to the file given in the argument. The
     * channel will be written out in the UTF-8 encoding.
     *
     * @param filename - The name of the file to which the channel object is to be
     *                 written.
     */
    public ZloRss20Exporter(String filename) throws IOException {
        this(new File(filename), "utf-8");
    }


    /**
     * Creates a channel exporter bound to the file given in the argument. The
     * <p/>
     * channel will be written out in the UTF-8 encoding.
     *
     * @param file -  The file object to which the channel object is to be
     *             <p/>
     *             written.
     */
    public ZloRss20Exporter(File file) throws IOException {
        this(file, "utf-8");
    }

    /**
     * Creates a channel exporter bound to the file given in the arguments.
     *
     * @param file     -  The file object to which the channel object is to be
     *                 <p/>
     *                 written.
     * @param encoding -
     *                 <p/>
     *                 The character encoding to write the channel object in.
     */

    public ZloRss20Exporter(File file, String encoding) throws IOException {
        this.writer = new OutputStreamWriter(new FileOutputStream(file), encoding);
        this.encoding = encoding;
    }

    /**
     * Creates a channel exporter bound to the Writer given in the arguments.
     *
     * @param writer   -
     *                 <p/>
     *                 The Writer to which the channel object is to be written.
     * @param encoding -
     *                 <p/>
     *                 The character encoding the Writer writes in.
     */
    public ZloRss20Exporter(Writer writer, String encoding) {
        this.writer = writer;
        this.encoding = encoding;
    }

    // ------------------------------------------------------------
    // Build a hierarchical category String from CategoryIF
    // ------------------------------------------------------------

    private Element getCategoryElements(Element elem, CategoryIF category, StringBuffer catString) {
        StringBuffer l_catString;
        if (catString == null || catString.length() < 1)
            l_catString = new StringBuffer(category.getTitle());
        else
            l_catString = catString.append("/").append(category.getTitle());

        Collection categories = category.getChildren();
        if (categories.size() == 0) {
            elem.addContent(new Element("category").setText(l_catString.toString()));
        } else {
            for (Object category1 : categories) {
                CategoryIF childCat = (CategoryIF) category1;
                elem = getCategoryElements(elem, childCat, l_catString);
            }
        }
        return elem;
    }

    // ------------------------------------------------------------
    // implementation of ChannelExporterIF interface
    // ------------------------------------------------------------

    public void write(ChannelIF channel) throws IOException {
        if (writer == null) {
            throw new RuntimeException("No writer has been initialized.");
        }

        // create XML outputter with indent: 2 spaces, print new lines.
        Format format = Format.getPrettyFormat();
        format.setEncoding(encoding);
        XMLOutputter outputter = new XMLOutputter(format);

        Namespace dcNs = Namespace.getNamespace("dc", NS_DC);
        Namespace syNs = Namespace.getNamespace("sy", NS_SY);
        Namespace adminNs = Namespace.getNamespace("admin", NS_ADMIN);
        //Namespace rdfNs = Namespace.getNamespace("rdf", NS_RDF);

        Element rootElem = new Element("rss");
        rootElem.addNamespaceDeclaration(dcNs);
        rootElem.addNamespaceDeclaration(syNs);
        rootElem.addNamespaceDeclaration(adminNs);
        rootElem.setAttribute("version", RSS_VERSION);

        Element channelElem = new Element("channel");
        // rootElem.setAttribute("version");
        channelElem.addContent(new Element("title").setText(channel.getTitle()));
        if (channel.getSite() != null) {
            channelElem.addContent(new Element("link").setText(channel.getSite()
                    .toString()));
        }

        channelElem.addContent(new Element("description").setText(channel
                .getDescription()));
        if (channel.getLanguage() != null) {
            channelElem.addContent(new Element("language", dcNs).setText(channel
                    .getLanguage()));
        }
        if (channel.getCopyright() != null) {
            channelElem.addContent(new Element("copyright", dcNs).setText(channel
                    .getCopyright()));
        }
        if (channel.getPubDate() != null) {
            channelElem.addContent(new Element("pubDate").setText(ParserUtils
                    .formatDate(channel.getPubDate())));
        }
        if (channel.getCategories() != null) {
            for (Object category : channel.getCategories()) {
                CategoryIF cat = (CategoryIF) category;
                channelElem = getCategoryElements(channelElem, cat, null);
            }
        }

        if (channel.getUpdateBase() != null) {
            channelElem.addContent(new Element("updateBase", syNs).setText(df
                    .format(channel.getUpdateBase())));
        }
        if (channel.getUpdatePeriod() != null) {
            // don't put out frequency without specifying period
            channelElem.addContent(new Element("updateFrequency", syNs)
                    .setText((new Integer(channel.getUpdateFrequency())).toString()));
            channelElem.addContent(new Element("updatePeriod", syNs).setText(channel
                    .getUpdatePeriod().toString()));
        }
        // export channel image
        if (channel.getImage() != null) {
            Element imgElem = new Element("image");
            imgElem.addContent(new Element("title")
                    .setText(channel.getImage().getTitle()));
            imgElem.addContent(new Element("url")
                    .setText(channel.getImage().getLocation().toString()));
            imgElem.addContent(new Element("link")
                    .setText(channel.getImage().getLink().toString()));
            imgElem.addContent(new Element("height")
                    .setText("" + channel.getImage().getHeight()));
            imgElem.addContent(new Element("width")
                    .setText("" + channel.getImage().getWidth()));
            imgElem.addContent(new Element("description")
                    .setText(channel.getImage().getDescription()));
            channelElem.addContent(imgElem);
        }

        // TODO: add exporting textinput field
        //     if (channel.getTextInput() != null) {
        //       channelElem.addContent(channel.getTextInput().getElement());
        //     }

        // XXX: this change while switching to use older version of informa from maven
//        for (ItemIF item : channel.getItems()) {
        for (Object _item : channel.getItems()) {
            ItemIF item = (ItemIF) _item;
            Element itemElem = new Element("item");
            itemElem.addContent(new Element("title").setText(item.getTitle()));
            if (item.getLink() != null) {
                itemElem.addContent(new Element("link").setText(item.getLink()
                        .toString()));
            }
            if (item.getDescription() != null) {
                itemElem.addContent(new Element("description").setText(item
                        .getDescription()));
            }
            if (item.getCategories() != null) {
                for (Object category : item.getCategories()) {
                    CategoryIF cat = (CategoryIF) category;
                    itemElem = getCategoryElements(itemElem, cat, null);
                }
            }
            if (item.getDate() != null) {
                itemElem.addContent(new Element("pubDate").setText(ParserUtils
                        .formatDate(item.getDate())));
            }
            if (item.getGuid() != null) {
                Element guid = new Element("guid").setText(item.getGuid().getLocation());
                guid.setAttribute("isPermaLink", Boolean.toString(item.getGuid().isPermaLink()));
                itemElem.addContent(guid);
            }
            if (item.getComments() != null) {
                itemElem.addContent(new Element("comments").setText(item.getComments()
                        .toString()));
            }
            /* added */

            if (item.getCreator() != null) {
                Element el = new Element("creator");
                el.setText(item.getCreator());
                el.setNamespace(dcNs);
                itemElem.addContent(el);
            }

            /* added end */
            channelElem.addContent(itemElem);
        }

        rootElem.addContent(channelElem);

        // ---
        Document doc = new Document();
        if (cssStylesheet != null) {
            doc.addContent(new ProcessingInstruction("xml-stylesheet", String.format("type=\"text/css\" href=\"%s\"", cssStylesheet)));
        }
        if (xslStylesheet != null) {
            doc.addContent(new ProcessingInstruction("xml-stylesheet", String.format("type=\"text/css\" href=\"%s\"", xslStylesheet)));
        }
        doc.addContent(rootElem);
        outputter.output(doc, writer);
        // ---
        writer.close();
    }

    public String getCssStylesheet() {
        return cssStylesheet;
    }

    public void setCssStylesheet(String cssStylesheet) {
        this.cssStylesheet = cssStylesheet;
    }

    public String getXslStylesheet() {
        return xslStylesheet;
    }

    public void setXslStylesheet(String xslStylesheet) {
        this.xslStylesheet = xslStylesheet;
    }
}
