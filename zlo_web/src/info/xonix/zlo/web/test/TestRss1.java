package info.xonix.zlo.web.test;

import de.nava.informa.core.CategoryIF;
import de.nava.informa.core.ChannelIF;
import de.nava.informa.impl.basic.Category;
import de.nava.informa.impl.basic.Channel;
import de.nava.informa.impl.basic.Item;
import info.xonix.zlo.web.rss.ZloRss20Exporter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

/**
 * Author: Vovan
 * Date: 28.05.2008
 * Time: 1:13:27
 */
public class TestRss1 {
    public static void main(String[] args) {
//        ChannelBuilder chb = new ChannelBuilder();

        ChannelIF ch = new Channel(); //chb.createChannel("Title", "Location");
        ch.setTitle("Title");

        try {
            ch.setLocation(new URL("http://Location/aaa"));
            ch.setLanguage("ru");

            Item it = new Item();
            it.setDate(new Date());
            it.setTitle("Title 1");
            it.setDescription("Descr 1");
            it.setLink(new URL("http://searcher.link/"));
            it.setComments(new URL("http://board.link/"));
            it.setCategories(Arrays.asList((CategoryIF)new Category("cat1")));
//            it.setCategories((Collection<? extends CategoryIF>) Arrays.asList(new Category("cat1")));

            it.setCreator("Nick 1");

            ch.addItem(it);

            it = new Item();
            it.setDate(new Date());
            it.setTitle("Title 2");
            it.setDescription("Descr 2");
            it.setCreator("Nick 2");

            ch.addItem(it);

            ZloRss20Exporter exporter = new ZloRss20Exporter(new PrintWriter(System.out), "windows-1251");

            exporter.setCssStylesheet("style.css");
            exporter.write(ch);

//            System.out.println(chb);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
