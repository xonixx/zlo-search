package org.xonix.zlo.search.site;

/**
 * Author: Vovan
 * Date: 28.12.2007
 * Time: 3:35:09
 */
public class SiteAccessorZlo extends SiteAccessor {

    public SiteAccessorZlo() {
        END_MSG_MARK =              "<BIG>Сообщения в этом потоке</BIG>";
        END_MSG_MARK_SIGN =         "<div class=\"sign\">";
        MSG_NOT_EXIST_OR_WRONG =    "Это сообщение не существует или введено неправильно";
        WITHOUT_TOPIC =             "без темы";

        MSG_REG_RE_STR =    "<DIV ALIGN=CENTER><BIG>\\[(.*?)\\]</BIG>&nbsp;&nbsp;<BIG>(.*?)</BIG>" +
                            "<BR>Сообщение было послано:\\s*<a href=\"\\?uinfo=.*?\" class=\"nn\" onclick=\"popup\\('uinfo', '.*?', 700, 600\\); return false;\" title=\"Информация о Пользователе\" target=\"_blank\">(.*?)</a>\\s*" +
                            "<small>\\((.*?)\\)</small><BR>Дата:\\s*(.*?)</DIV><BR><br\\s*/><div class=\"body\">(.*?)</div>";

        MSG_UNREG_RE_STR =  "<DIV ALIGN=CENTER><BIG>\\[(.*?)\\]</BIG>&nbsp;&nbsp;<BIG>(.*?)</BIG>" +
                            "<BR>Сообщение было послано:\\s*<b>(.*?)</b><SMALL>\\s*\\(unreg\\)</SMALL>\\s*<small>" +
                            "\\((.*?)\\)</small><BR>Дата:\\s*(.*?)</DIV><BR><br\\s*/><div class=\"body\">(.*?)</div>";

        INDEX_UNREG_RE_STR =    "<A NAME=(\\d+) HREF=\"\\?read=(\\d+)\">";

        INDEXING_URL =          "zlo.rt.mipt.ru";
        READ_QUERY =            "/?read=";
    }
}
