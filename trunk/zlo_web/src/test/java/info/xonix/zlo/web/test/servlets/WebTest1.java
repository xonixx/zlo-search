package info.xonix.zlo.web.test.servlets;

import info.xonix.zlo.search.FoundTextHighlighter;
import info.xonix.zlo.search.config.Config;

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
//        fh.setText("<a href=\"http://a.vkontakte.ru/club886777\" style=\"text-decoration: underline;\" target=\"_blank\">они нас ненаvkontakteвидят vkontakte О_о</a>");
//        System.out.println(fh.getHighlightedText());
//        fh.setText("Олечка позитифффчик) Поройкова<br><img class=\"imgtag\" src=\"http://cs41.vkontakte.ru/u823713/a_584103a.jpg\"><br><br>Ольга Юдина<br><img class=\"imgtag\" src=\"http://cs66.vkontakte.ru/u1888933/a_fce39de.jpg\">");
//        System.out.println(fh.getHighlightedText());
//        fh.setText("<a href=\"http://www.google.com/search?hl=ru&amp;q=%D1%87%D1%82%D0%BE+%D0%B4%D0%B5%D0%BB%D0%B0%D1%82%D1%8C+%D0%B5%D1%81%D0%BB%D0%B8+%D0%BD%D0%B5%D1%87%D0%B5%D0%B3%D0%BE+%D0%B4%D0%B5%D0%BB%D0%B0%D1%82%D1%8C&amp;btnG=%D0%9F%D0%BE%D0%B8%D1%81%D0%BA+%D0%B2+Google&amp;lr=\" style=\"text-decoration: underline;\" target=\"_blank\">http://www.google.com/search?hl=ru&amp;q=%D1%87%D1%82%D0%BE+%D0%B4%D0%B5%D0%BB%D0%B0%D1%82%D1%8C+%D0%B5%D1%81%D0%BB%D0%B8+%D0%BD%D0%B5%D1%87%D0%B5%D0%B3%D0%BE+%D0%B4%D0%B5%D0%BB%D0%B0%D1%82%D1%8C&amp;btnG=%D0%9F%D0%BE%D0%B8%D1%81%D0%BA+%D0%B2+Google&amp;lr=</a>");
//        fh.setWordsStr("google");
        fh.setText("Пять человек пострадали от задымления после взрыва на Охотном ряду");
        fh.setWordsStr("п*да");
        System.out.println(fh.getHighlightedText());
//        long t0 = System.currentTimeMillis();
//        for (int i=0; i<50; i++) {
//            System.out.println(fh.getHighlightedText());
//            fh.getHighlightedText();
//        }
//        System.out.println((System.currentTimeMillis() - t0)/50);
//        System.out.println("aAbaA".replaceAll("(?i)aa", "1"));
//        System.out.println("иИвиИ".replaceAll("(?iu)ии", "1"));
//        System.out.println("ИииВввРрр".toLowerCase());
//        System.out.println(Pattern.compile("ии", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher("Ии").find());
    }
}
