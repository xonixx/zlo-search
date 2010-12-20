<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<link rel="stylesheet" type="text/css" href="main.css"/>

<title>FAQ</title>

<tiles:insertDefinition name="header.faq"/>

<div class="content">
    <h3 class="attention">News</h3>
    <ul>
        <li>Добавлен <a href="ws.jsp">веб-сервис поиска</a>;</li>
        <li>Добавлен поиск по <a href="http://forum.dolgopa.org/">http://forum.dolgopa.org/</a>;</li>
        <li>Добавлен поиск по <a href="http://x.zlowiki.ru/">http://x.zlowiki.ru/</a>;</li>
        <li>Добавлен поиск по форумам: <a href="http://takeoff.mipt.ru/cgi-bin/board/index.cgi?index">форум горной
            секции МФТИ</a>, <a href="http://zlo.rt.mipt.ru/dev/">Programming Board</a>;
        </li>
        <li>Добавлена функциональность RSS. Теперь можно подписаться на сообщения по конкретному запросу, например,
            канал <a
                    href="search?rss&st=adv&text=%F1%E8%F1%FC*+%F2%E8%F2%FC*+boobs+tits+%F1%E8%F1%E8+%F6%FB%F6%EA%E8&topic=-1&inTitle=on&inBody=on&hasImg=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">сисек
                <img src="feed-icon-14x14.png" alt="картинки сисек"/></a>;
        </li>
        <li>Добавлен поиск по форуму <a href="http://velo.mipt.ru/cgi/forum/index.cgi">velo.mipt.ru</a>;</li>
        <li>Ссылки теперь можно искать в любом режиме, например
            <a href="search?st=all&text=http%3A%2F%2Fbash.org.ru%2Fbest.php&topic=-1&inTitle=on&inBody=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">
                http://bash.org.ru/best.php</a>;
        </li>
        <li>Стал возможен поиск только по сообщениям <a
                href="search?st=all&text=&topic=-1&inTitle=on&inBody=on&hasImg=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">с
            картинками</a>,
            и только по сообщениям <a
                    href="search?st=all&text=&topic=-1&inTitle=on&inBody=on&hasUrl=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">со
                ссылками</a>;
        </li>
    </ul>

    <h3>Как искать?</h3>
    Поисковик имеет три режима поиска:
    <ul>
        <li><a href="#adv">Со всеми словами</a>;</li>
        <li><a href="#adv">Хотя бы с одним из слов</a>;</li>
        <li><a href="#exct">Точная фраза</a>;</li>
    </ul>
    Кроме того, возможна фильтрация результата поиска по:
    <ul>
        <li>Теме сообщени;</li>
        <li>Имени пользователя;</li>
        <li>Хосту пользователя;</li>
        <li>Промежутку дат;</li>
        <li>Дополнительным критериям: 1) только от зарегистрированных пользователей 2) только среди сообщений со
            ссылками 3) только среди сообщений с картинками;
        </li>
    </ul>
    Также, возможен поиск только по имени пользователя и хосту.

    <h4 id="exct">Точная фраза</h4>
    При поиске в режиме <i>"Точная фраза"</i> ищутся сообщения с точным вхождением искомой фразы вплоть до порядка
    следования слов.

    <h4 id="adv">Расширенный поиск</h4>
    Режимы <i>"Со всеми словами"</i> и <i>"Хотя бы с одним из слов"</i> предоставляют возможности расширенного поиска и
    отличаются лишь тем, что в первом случае между словами неявно ставится
    оператор AND, а во втором OR. При расширенном поиске поисковые запросы могут иметь вид:
    <table border=1>
        <tr>
            <th>Запрос</th>
            <th>Объяснение</th>
        </tr>
        <tr>
            <td>
                <a href="search?st=adv&text=%F7%E5%F0%ED%FB%E9+%E1%E5%EB%FB%E9+%EA%F0%E0%F1%ED%FB%E9&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=Search">
                    черный белый красный</a>
                или
                <a href="search?st=all&text=%F7%E5%F0%ED%FB%E9+OR+%E1%E5%EB%FB%E9+OR+%EA%F0%E0%F1%ED%FB%E9&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">
                    черный OR белый OR красный
                </a>
            </td>
            <td>Запрос "ИЛИ". Будут найдены все сообщения, содержащие хотя бы одно из слов.</td>
        </tr>
        <tr>
            <td>
                <a href="search?st=adv&text=%EF%F0%E5%E2%E5%E4+-%EC%E5%E4%E2%E5%E4&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=Search">
                    превед -медвед</a>
                или
                <a href="search?st=all&text=%EF%F0%E5%E2%E5%E4+NOT+%EC%E5%E4%E2%E5%E4&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">
                    превед NOT медвед
                </a>
            </td>
            <td>Запрос "НЕ". Будут найдены все сообщения, содержащие "превед" но не содержащие "медвед". Вместо "-"
                можно использовать "!".
            </td>
        </tr>
        <tr>
            <td>
                <a href="search?st=adv&text=%2B%EF%F0%E5%E2%E5%E4+%2B%EC%E5%E4%E2%E5%E4&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=Search">
                    +превед +медвед</a>
                или
                <a href="search?st=all&text=%EF%F0%E5%E2%E5%E4+AND+%EC%E5%E4%E2%E5%E4&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">
                    превед AND медвед
                </a>
            </td>
            <td>Запрос "И". Будут найдены все сообщения, содержащие одновременно оба слова. Соответствует режиму поиска
                "Со всеми словами".
            </td>
        </tr>
        <tr>
            <td>
                <a href="search?st=adv&text=%22java+html%22%7E2&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">
                    "java html"~2</a></td>
            <td>Будут найдены сообщения, где слова "java" и "html" отстоят не более чем на 2 слова друг от друга.</td>
        </tr>
        <tr>
            <td>
                <a href="search?st=adv&text=%22%F3+%EB%F3%EA%EE%EC%EE%F0%FC%FF+%E4%F3%E1+%E7%E5%EB%E5%ED%FB%E9%22&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=Search">
                    "у лукоморья дуб зеленый"</a></td>
            <td>Будут найдены сообщения, содержащие точную фразу. Соответствует режиму поиска "Точная фраза".</td>
        </tr>
        <tr>
            <td>
                <a href="search?st=adv&text=%F2%3F%E7&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=Search">
                    т?з</a></td>
            <td>"?" соответствует одной произвольной букве. Будут надены слова "таз", "туз". <i>Не работает в режиме
                точная фраза!</i></td>
        </tr>
        <tr>
            <td>
                <a href="search?st=adv&text=%F1%F2%EE*&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=Search">
                    сто*</a></td>
            <td>"*" соответствует произвольному числу букв, включая 0. будут надены все слова, начинающиеся на "сто".
                <i>Не работает в режиме точная фраза!</i></td>
        </tr>
        <tr>
            <td>
                <a href="search?st=adv&text=%2B%28%E1%E5%EB%FB%E9+%F7%E5%F0%ED%FB%E9%29+%2B%F6%E2%E5%F2&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=Search">
                    +(белый черный) +цвет</a>
                или
                <a href="search?st=all&text=%28%E1%E5%EB%FB%E9+OR+%F7%E5%F0%ED%FB%E9%29+%F6%E2%E5%F2&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">
                    (белый OR черный) цвет
                </a>
            </td>
            <td>Можно использовать скобки. Будут найдены сообщения со словом "цвет" и с любым из слов "белый" и "черный"
                или с двумя сразу.
            </td>
        </tr>
    </table>

    Еще примеры запросов:
    <ul>
        <li>
            <a href="search?st=adv&text=%2B%28%EF%EE%F1%EE%E2%E5%F2%F3%E9%F2%E5+%EF%EE%E4%F1%EA%E0%E6%E8%F2%E5%29+%2B%28%F4%E8%EB%FC%EC+%EA%E8%ED%EE%29&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">
                +(посоветуйте подскажите) +(фильм кино)
            </a>
        </li>
        <li>
            <a href="search?st=adv&text=%2B%F2%E5%EB%E5%F4%EE%ED+%2B%F2%E0%EA%F1%E8&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">
                +телефон +такси
            </a>
        </li>
        <li>
            <a href="search?st=adv&text=%2B%E7%E5%F0%EA%E0%EB%EE+%2Bnod32&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">
                +зеркало +nod32
            </a>
        </li>
    </ul>

    <h4>Полезный совет</h4>
    Не пытайтесь отбрасывать окончания слов при поиске для того, чтобы получить больше результатов. Используемый в
    поисковике стеммер русского языка сам обрежет окончание с тем чтоб
    найти все подходящие слова.
</div>

<tiles:insertDefinition name="ga"/>