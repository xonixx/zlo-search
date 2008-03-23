<%@ include file="WEB-INF/include/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<link rel="stylesheet" type="text/css" href="main.css" />

<title>FAQ</title>

<div class="content">
    <h3 class="attention">News</h3>
    <ul>
        <li>Добавлен поиск по форуму <a href="http://velo.mipt.ru/cgi/forum/index.cgi">velo.mipt.ru</a></li>
        <li>Ссылки теперь можно искать в любом режиме, например
            <a href="search?st=all&text=http%3A%2F%2Fbash.org.ru%2Fbest.php&topic=-1&inTitle=on&inBody=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">
                http://bash.org.ru/best.php
            </a>
        </li>
        <li>Стал возможен поиск только по сообщениям <a href="search?st=all&text=&topic=-1&inTitle=on&inBody=on&hasImg=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">с картинками</a>
            , и только по сообщениям <a href="search?st=all&text=&topic=-1&inTitle=on&inBody=on&hasUrl=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">со ссылками</a></li>
    </ul>

    <h3>Как искать?</h3>
    Поисковик имеет три режима поиска:
    <ul>
        <li><a href="#all">Со всеми словами</a></li>
        <li><a href="#exct">Точная фраза</a></li>
        <li><a href="#adv">Расширенный поиск</a></li>
    </ul>
    Кроме того, возможна фильтрация результата поиска по:
    <ul>
        <li>Теме сообщени</li>
        <li>Имени пользователя</li>
        <li>Хосту пользователя</li>
        <li>Промежутку дат</li>
        <li>Дополнительным критериям: 1) только от зарегистрированных пользователей 2) только среди сообщений со ссылками 3) только среди сообщений с картинками</li>
    </ul>
    Также, возможен поиск только по имени и хосту.

    <h4 id="adv">Расширенный поиск</h4>
    В объяснении нуждается разве что этот режим. При расширенном поиске поисковые запросы могут иметь вид:
    <table border=1>
        <tr><th>Запрос</th><th>Объяснение</th></tr>
        <tr>
            <td><a href="search?st=adv&text=%F7%E5%F0%ED%FB%E9+%E1%E5%EB%FB%E9+%EA%F0%E0%F1%ED%FB%E9&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=Search">
                черный белый красный</a></td>
            <td>Запрос "ИЛИ". Будут найдены все сообщения, содержащие хотя бы одно из слов.</td>
        </tr>
        <tr>
            <td><a href="search?st=adv&text=%EF%F0%E5%E2%E5%E4+-%EC%E5%E4%E2%E5%E4&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=Search">
                превед -медвед</a></td>
            <td>Запрос "НЕ". Будут найдены все сообщения, содержащие "превед" но не содержащие "медвед". Вместо "-" можно использовать "!".</td>
        </tr>
        <tr>
            <td><a href="search?st=adv&text=%2B%EF%F0%E5%E2%E5%E4+%2B%EC%E5%E4%E2%E5%E4&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=Search">
                +превед +медвед</a></td>
            <td>Запрос "И". Будут найдены все сообщения, содержащие одновременно оба слова. Соответствует режиму поиска "Со всеми словами".</td>
        </tr>
        <tr>
            <td><a href="search?st=adv&text=%22java+html%22%7E2&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">
                "java html"~2</a></td>
            <td>Будут найдены сообщения, где слова "java" и "html" отстоят не более чем на 2 слова друг от друга.</td>
        </tr>
        <tr>
            <td><a href="search?st=adv&text=%22%F3+%EB%F3%EA%EE%EC%EE%F0%FC%FF+%E4%F3%E1+%E7%E5%EB%E5%ED%FB%E9%22&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=Search">
                "у лукоморья дуб зеленый"</a></td>
            <td>Будут найдены сообщения, содержащие точную фразу. Соответствует режиму поиска "Точная фраза".</td>
        </tr>
        <tr>
            <td><a href="search?st=adv&text=%F2%3F%E7&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=Search">
                т?з</a></td>
            <td>"?" сотвтетствует одной произвольной букве. Будут надены слова "таз", "туз".</td>
        </tr>
        <tr>
            <td><a href="search?st=adv&text=%F1%F2%EE*&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=Search">
                сто*</a></td>
            <td>"*" сотвтетствует произвольному числу букв, включая 0. будут надены все слова, начинающиеся на "сто"</td>
        </tr>
        <tr>
            <td><a href="search?st=adv&text=%2B%28%E1%E5%EB%FB%E9+%F7%E5%F0%ED%FB%E9%29+%2B%F6%E2%E5%F2&topic=-1&inTitle=on&nick=&host=&site=0&pageSize=0&submitBtn=Search">
                +(белый черный) +цвет</a></td>
            <td>Можно использовать скобки. Будут найдены сообщения со словом "цвет" и с любым из слов "белый" и "черный" или с двумя сразу.</td>
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

    <h4>Поиск ссылок</h4>
    <s>
    Ссылки следует искать в режиме "Точная фраза", например
    <a href="search?st=exct&text=http%3A%2F%2Fbash.org.ru%2Fbest.php&topic=-1&inTitle=on&inBody=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">
        http://bash.org.ru/best.php
    </a></s>
    <br/>
    <span class="attention">(upd!)</span> Ссылки теперь можно искать в любом режиме, например
    <a href="search?st=all&text=http%3A%2F%2Fbash.org.ru%2Fbest.php&topic=-1&inTitle=on&inBody=on&nick=&host=&site=0&pageSize=0&submitBtn=%C8%F1%EA%E0%F2%FC%21">
        http://bash.org.ru/best.php
    </a>
</div>
<jsp:include page="WEB-INF/include/_ga.jsp" flush="true" />