import info.xonix.zlo.search.utils.SmartQueryParser;
import org.junit.Test;

/**
 * User: gubarkov
 * Date: 02.10.11
 * Time: 18:22
 */
public class SmartQueryParserTests {
    private final SmartQueryParser smartQueryParser = new SmartQueryParser("windows-1251");

    @Test
    public void tests() {
        System.out.println(smartQueryParser.smartDecodeUrlencoded(
                "%E7%E0%E1%E0%ED%E5%ED&topic=-1&inTitle=on&inBody=on&nick=Artfulvampire&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21"));

        System.out.println(smartQueryParser.smartDecodeUrlencoded(
                "http://zlo.rt.mipt.ru:7500/search?st=all&text=%F2%F0%E0%EA%F2%E0%F2+%EE+%F1%EA%EE%F2%F1%EA%EE%E9+%F1%F3%F9%ED%EE%F1%F2%E8+%E0%F2%E5%E8%F1%F2%EE%E2&topic=-1&inTitle=on&inBody=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21"));

        System.out.println(smartQueryParser.smartDecodeUrlencoded(
                "http://zlo.rt.mipt.ru:7500/search?st=all&text=%EB%EE%E3%EE%F2%E8%EF+%E2+%E2%E5%EA%F2%EE%F0%E5&topic=-1&inTitle=on&inBody=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21"));

        System.out.println(smartQueryParser.smartDecodeUrlencoded(
                "http://zlo.rt.mipt.ru:7500/search?st=all&text=%D0%BF%D0%BE%D1%81%D0%BE%D0%B2%D0%B5%D1%82%D1%83%D0%B9%D1%82%D0%B5+%D1%81%D0%B5%D1%80%D0%B8%D0%B0%D0%BB&topic=-1&inTitle=on&inBody=on&nick=&host=&site=0&pageSize=0&submitBtn=%D0%98%D1%81%D0%BA%D0%B0%D1%82%D1%8C%21"));

        System.out.println(smartQueryParser.smartDecodeUrlencoded(
                "http://zlo.rt.mipt.ru:7500/search?st=all&text=%22%D0%BF%D1%80%D0%BE%D1%88%D0%BB%D0%BE%D0%B5+%D0%B4%D0%B5%D0%B2%D1%83%D1%88%D0%BA%D0%B8%22&topic=-1&inTitle=on&inBody=on&nick=&host=&site=0&pageSize=0&submitBtn=%D0%98%D1%81%D0%BA%D0%B0%D1%82%D1%8C%21"));
    }
}
