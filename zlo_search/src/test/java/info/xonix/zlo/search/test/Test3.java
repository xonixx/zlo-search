package info.xonix.zlo.search.test;


import info.xonix.zlo.search.logic.forum_adapters.impl.wwwconf.WwwconfUtils;
import info.xonix.zlo.search.logic.site.PageParseException;
import info.xonix.zlo.search.logic.site.PageParser;
import info.xonix.zlo.search.model.Message;

/**
 * Author: Vovan
 * Date: 22.03.2008
 * Time: 22:05:23
 */
public class Test3 {
    public static void main(String[] args) throws PageParseException {
        String msg;
        msg = "</CENTER>\n" +
                "<BR>\n" +
                "\n" +
                "<DIV align = center>\n" +
                "<BIG>[без темы] STELS без рамы. Покататься хорошо подойдет.</BIG><BR>\n" +
                "Сообщение было послано: <B>bull</B> <SMALL>(unreg)</SMALL> <SMALL>(ppp85-140-32-253.pppoe.mtu-net.ru)</SMALL><BR>\n" +
                "Дата: 22/03/2008  2:00</DIV>\n" +
                "\n" +
                "<BR>\n" +
                "<BR>\n" +
                "<CENTER><BIG>Сообщения в этом потоке</BIG></CENTER>";

        /*msg = "</TABLE>\n" +
                "</CENTER>\n" +
                "<BR>\n" +
                "\n" +
                "<DIV align = center>\n" +
                "<BIG>[без темы] В инвалидной коляске с электроприводом будет еще комфортнее.</BIG><BR>\n" +
                "Сообщение было послано: <B>subebitor</B> <SMALL>(unreg)</SMALL> <SMALL>(172.16.1.51)</SMALL><BR>\n" +
                "Дата: 08/01/2008  18:07</DIV>\n" +
                "\n" +
                "<BR>\n" +
                "\n" +
                "<DIV class = body>\n" +
                "А в чем польза? В том, что от седока требуется все меньше меньше умений, навыков и способностей? (Платежеспособность, разумеется, тут должна быть развита).\n" +
                "</DIV>\n" +
                "\n" +
                "<BR>\n" +
                "<CENTER><BIG>Сообщения в этом потоке</BIG></CENTER>";*/
        msg = "/CENTER>\n" +
                "<BR>\n" +
                "\n" +
                "<DIV align = center>\n" +
                "<BIG>[без темы] welcome ;)</BIG><BR>\n" +
                "Сообщение было послано: <A class = profile href = \"index.cgi?uinfo=Mnemonic\" target = _blank><B>Mnemonic</B></A> <SMALL>(mnemonic.rt.mipt.ru)</SMALL><BR>\n" +
                "Дата: 04/06/2003  11:13</DIV>\n" +
                "<BR>\n" +
                "<BR>\n" +
                "<CENTER><BIG>Сообщения в этом потоке</BIG></CENTER>\n" +
                "\n" +
                "<DIV class = ots>";

        String forumId = "velo";
        PageParser p = new PageParser();
        Message m = p.parseMessage(forumId, WwwconfUtils.getWwwconfParams(forumId), msg, 123);
        System.out.println(m);
    }
}
