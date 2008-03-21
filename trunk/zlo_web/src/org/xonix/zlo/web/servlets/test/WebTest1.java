package org.xonix.zlo.web.servlets.test;

import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.FoundTextHighlighter;

/**
 * Author: Vovan
 * Date: 14.12.2007
 * Time: 19:35:01
 */
public class WebTest1 {
    public static void main(String[] args) {
        new Config();
        m1();
        System.exit(0);
    }

    public static void m2() {
        System.out.println("abc".replaceAll("b", "s\\\\s"));
    }

    public static void m1() {
        FoundTextHighlighter fh = new FoundTextHighlighter();
//        fh.setHighlightWords(new String[] {"vkontakte"});
//        fh.setText("<a href=\"http://a.vkontakte.ru/club886777\" style=\"text-decoration: underline;\" target=\"_blank\">они нас ненаvkontakteвид€т vkontakte ќ_о</a>");
//        System.out.println(fh.getHighlightedText());
//        fh.setText("ќлечка позитифффчик) ѕоройкова<br><img class=\"imgtag\" src=\"http://cs41.vkontakte.ru/u823713/a_584103a.jpg\"><br><br>ќльга ёдина<br><img class=\"imgtag\" src=\"http://cs66.vkontakte.ru/u1888933/a_fce39de.jpg\">");
//        System.out.println(fh.getHighlightedText());
//        fh.setText("<a href=\"http://www.google.com/search?hl=ru&amp;q=%D1%87%D1%82%D0%BE+%D0%B4%D0%B5%D0%BB%D0%B0%D1%82%D1%8C+%D0%B5%D1%81%D0%BB%D0%B8+%D0%BD%D0%B5%D1%87%D0%B5%D0%B3%D0%BE+%D0%B4%D0%B5%D0%BB%D0%B0%D1%82%D1%8C&amp;btnG=%D0%9F%D0%BE%D0%B8%D1%81%D0%BA+%D0%B2+Google&amp;lr=\" style=\"text-decoration: underline;\" target=\"_blank\">http://www.google.com/search?hl=ru&amp;q=%D1%87%D1%82%D0%BE+%D0%B4%D0%B5%D0%BB%D0%B0%D1%82%D1%8C+%D0%B5%D1%81%D0%BB%D0%B8+%D0%BD%D0%B5%D1%87%D0%B5%D0%B3%D0%BE+%D0%B4%D0%B5%D0%BB%D0%B0%D1%82%D1%8C&amp;btnG=%D0%9F%D0%BE%D0%B8%D1%81%D0%BA+%D0%B2+Google&amp;lr=</a>");
//        fh.setWordsStr("google");
        fh.setText("ѕ€ть человек пострадали от задымлени€ после взрыва на ќхотном р€ду");
        fh.setWordsStr("п*да");
        System.out.println(fh.getHighlightedText());
//        long t0 = System.currentTimeMillis();
//        for (int i=0; i<50; i++) {
//            System.out.println(fh.getHighlightedText());
//            fh.getHighlightedText();
//        }
//        System.out.println((System.currentTimeMillis() - t0)/50);
//        System.out.println("aAbaA".replaceAll("(?i)aa", "1"));
//        System.out.println("и»ви»".replaceAll("(?iu)ии", "1"));
//        System.out.println("»ии¬вв–рр".toLowerCase());
//        System.out.println(Pattern.compile("ии", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher("»и").find());
    }
}
