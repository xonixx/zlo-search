//
// Informa -- RSS Library for Java
// Copyright (c) 2002 by Niko Schmuck
//
// Niko Schmuck
// http://sourceforge.net/projects/informa
// mailto:niko_schmuck@users.sourceforge.net
//
// This library is free software.
//
// You may redistribute it and/or modify it under the terms of the GNU
// Lesser General Public License as published by the Free Software Foundation.
//
// Version 2.1 of the license should be included with this distribution in
// the file LICENSE. If the license is not included with this distribution,
// you may find a copy at the FSF web site at 'www.gnu.org' or 'www.fsf.org',
// or you may write to the Free Software Foundation, 675 Mass Ave, Cambridge,
// MA 02139 USA.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied waranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//


// $Id: RSS_1_0_Exporter.java,v 1.10 2007/01/05 23:07:58 niko_schmuck Exp $

package de.nava.informa.exporters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.nava.informa.core.ChannelExporterIF;
import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemIF;
import de.nava.informa.utils.ParserUtils;

/**
 * A channel exporter that can write channel objects out into the
 * interchange syntax defined by RSS 1.0.</p>
 */
public class RSS_1_0_Exporter implements ChannelExporterIF {

  private static final String NS_DEFAULT =  
    "http://purl.org/rss/1.0/";
  private static final String NS_RDF =
    "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
  /** RSS 1.0 Dublin Core namespace */
  private static final String NS_DC =
    "http://purl.org/dc/elements/1.1/";
  /** RSS 1.0 Syndication Module namespace */
  private static final String NS_SY =
    "http://purl.org/rss/1.0/modules/syndication/";

  private static SimpleDateFormat df =
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

  
  private Writer writer;
  private String encoding;
  
  /**
   * Creates a channel exporter bound to the file given in the
   * argument. The channel will be written out in the UTF-8 encoding.
   *
   * @param filename - The name of the file to which the channel object
   *                   is to be written.
   */
  public RSS_1_0_Exporter(String filename) throws IOException {
    this(new File(filename), "utf-8");
  }

  /**
   * Creates a channel exporter bound to the file given in the
   * argument. The channel will be written out in the UTF-8 encoding.
   *
   * @param file - The file object to which the channel object is
   *               to be written.
   */
  public RSS_1_0_Exporter(File file) throws IOException {
    this(file, "utf-8");
  }

  /**
   * Creates a channel exporter bound to the file given in the
   * arguments.
   *
   * @param file - The file object to which the channel object is
   *               to be written.
   * @param encoding - The character encoding to write the channel
   *                   object in.
   */
  public RSS_1_0_Exporter(File file, String encoding) throws IOException {
    this.writer = new OutputStreamWriter(new FileOutputStream(file), encoding);
    this.encoding = encoding;
  }
  
  /**
   * Creates a channel exporter bound to the Writer given in the
   * arguments.
   *
   * @param writer - The Writer to which the channel object is to be
   *                 written.
   * @param encoding - The character encoding the Writer writes in.
   */
  public RSS_1_0_Exporter(Writer writer, String encoding) {
    this.writer = writer;
    this.encoding = encoding;
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

    Namespace defNs = Namespace.getNamespace(NS_DEFAULT);
    Namespace rdfNs = Namespace.getNamespace("rdf", NS_RDF);
    Namespace dcNs = Namespace.getNamespace("dc", NS_DC);
    Namespace syNs = Namespace.getNamespace("sy", NS_SY);
    
    // ----
    Element rootElem = new Element("RDF", rdfNs);
    rootElem.addNamespaceDeclaration(defNs);
    rootElem.addNamespaceDeclaration(dcNs);
    rootElem.addNamespaceDeclaration(syNs);
    // rootElem.setAttribute("version");
    Element channelElem = new Element("channel", defNs);
    if (channel.getLocation() != null) {
      channelElem.setAttribute("about",
                               channel.getLocation().toString(), rdfNs);
    }
    channelElem.addContent(new Element("title", defNs)
                           .setText(channel.getTitle()));
    if (channel.getSite() != null) {
      channelElem.addContent(new Element("link", defNs)
                             .setText(channel.getSite().toString()));
      channelElem.addContent(new Element("source", dcNs)
                             .setAttribute("resource",
                                           channel.getSite().toString()));
    }

    channelElem.addContent(new Element("description", defNs)
                           .setText(channel.getDescription()));
    if (channel.getLanguage() != null) {
      channelElem.addContent(new Element("language", dcNs)
                             .setText(channel.getLanguage()));
    }
    if (channel.getCopyright() != null) {
      channelElem.addContent(new Element("copyright", dcNs)
                             .setText(channel.getCopyright()));
    }
    if (channel.getUpdateBase() != null) {
      channelElem.addContent(new Element("updateBase", syNs)
                             .setText(df.format(channel.getUpdateBase())));
    }
    if (channel.getUpdatePeriod() != null) {
      // don't put out frequency without specifying period
      channelElem.addContent(new Element("updateFrequency", syNs)
                             .setText((new Integer(channel.getUpdateFrequency())).toString()));
      channelElem.addContent(new Element("updatePeriod", syNs)
                             .setText(channel.getUpdatePeriod().toString()));
    }
    // export channel image            
    if (channel.getImage() != null) {
      Element imgElem = new Element("image", defNs);
      imgElem.addContent(new Element("title", defNs)
                         .setText(channel.getImage().getTitle()));
      imgElem.addContent(new Element("url", defNs)
                         .setText(channel.getImage().getLocation().toString()));
      imgElem.addContent(new Element("link", defNs)
                         .setText(channel.getImage().getLink().toString()));
      imgElem.addContent(new Element("height", defNs)
                         .setText("" + channel.getImage().getHeight()));
      imgElem.addContent(new Element("width", defNs)
                         .setText("" + channel.getImage().getWidth()));
      imgElem.addContent(new Element("description", defNs)
                         .setText(channel.getImage().getDescription()));
      channelElem.addContent(imgElem);
    }
     
    // TODO: add exporting textinput field
    //     if (channel.getTextInput() != null) {
    //       channelElem.addContent(channel.getTextInput().getElement());
    //     }

    // ===========================================
    Element itemsElem = new Element("items", defNs);
    Element seqElem = new Element("Seq", rdfNs);
    Collection items = channel.getItems();
    Iterator it = items.iterator();
    while (it.hasNext()) {
      ItemIF item = (ItemIF) it.next();
      Element itemElem = new Element("li", rdfNs);
      if (item.getLink() != null) {
        itemElem.setAttribute("resource", item.getLink().toString());
      }
      seqElem.addContent(itemElem);
    }
    itemsElem.addContent(seqElem);
    channelElem.addContent(itemsElem);
    rootElem.addContent(channelElem);

    // item-by-item en detail
    items = channel.getItems();
    it = items.iterator();
    while (it.hasNext()) {
      ItemIF item = (ItemIF) it.next();
      Element itemElem = new Element("item", defNs);
      if (item.getLink() != null) {
        itemElem.setAttribute("about",
                              item.getLink().toString(), rdfNs);
      }
      itemElem.addContent(new Element("title", defNs).setText(item.getTitle()));
      if (item.getLink() != null) {
        itemElem.addContent(new Element("link", defNs)
                            .setText(item.getLink().toString()));
      }
      if (item.getDescription() != null) {
        itemElem.addContent(new Element("description", dcNs)
                            .setText(item.getDescription()));
      }
      if (item.getDate() != null) {
        itemElem.addContent(new Element("date", dcNs)
                            .setText(ParserUtils.formatDate(item.getDate())));
      }
                          
      rootElem.addContent(itemElem);
    }

    // ---
    Document doc = new Document(rootElem);
    outputter.output(doc, writer);
    // ---
    writer.close();
  }

}
