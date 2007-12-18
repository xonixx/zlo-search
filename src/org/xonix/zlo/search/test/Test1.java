package org.xonix.zlo.search.test;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.index.Term;
import org.xonix.zlo.search.DAO;
import org.xonix.zlo.search.ZloSearcher;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.*;

/**
 * Author: Vovan
 * Date: 06.09.2007
 * Time: 0:48:24
 */

class A {

    public A(int a) {
        System.out.println(a);
    }
}

class B extends A {

    public B() {
        super(123);
    }
}

public class Test1 {
    public static void main(String[] args) {
        new Config();
        m15();
        System.exit(0);
    }

    public static void m20() {
        System.out.println(new ArrayList<Integer>(Arrays.asList(1,2,3,4,5)).contains(6));    
    }

    public static void m19() {
        /*try {
            String queryStr = "body:(+(Привет медведь т?ст) +Путин -извращенец педофил)";
            Analyzer analyzer = ZloMessage.constructAnalyzer();
            QueryParser parser = new QueryParser(ZloMessage.FIELDS.BODY, analyzer);
            Query query = parser.parse(queryStr);
            Set set = new HashSet();
            query.extractTerms(set);
            for(Object t: set) {
                System.out.println(((Term) t).text() + " " + t.getClass());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        String st = "+(Привет медведь т?ст) +Путин -извращенец педофил";
        for (String s : ZloSearcher.formHighlightedWords(st)) {
            System.out.println(s);
        }
    }

    public static void m18() {
        try {
//            System.out.println(new IndexSearcher(ZloSearcher.getIndexReader()).search(new TermQuery(new Term("nick", "abcdef"))).length());
//            System.out.println(new IndexSearcher(ZloSearcher.getIndexReader()).search(new TermQuery(new Term("nick", "abcdef")), new Sort(new SortField(ZloMessage.FIELDS.DATE, SortField.STRING, true))).length());
//            System.out.println(ZloSearcher.search("nick:abcdef").getHits().length());
            System.out.println(new IndexSearcher(ZloSearcher.getIndexReader()).search(new MatchAllDocsQuery(), new Sort(new SortField(ZloMessage.FIELDS.DATE, SortField.STRING, true))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void m17() {
//        System.out.println(ZloSearcher.getLastIndexedNumber());
        int from =4000000;
        int to=4000019;
        System.out.println((int)MessageFormat.format("{0}", 4000).charAt(1));
        System.out.println((int)' ');
    }

    public static void m16() {
        int N = 10000;
        String d = new Date().toString();
        String s = null;
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            s = String.format("asdasd %s %saaaa%s %s", 123, "Hello", d, " World ");
        }
        System.out.println(s);
        long t1 = System.currentTimeMillis();
        long dt1 = t1 - t0;
        for (int i = 0; i < N; i++) {
            s = MessageFormat.format("asdasd {0} {1}aaaa{2} {3}", 123, "Hello", d, " World ");
        }
        System.out.println(s);
        long t2 = System.currentTimeMillis();
        long dt2 = t2 - t1;
        System.out.println("dt1=" + dt1);
        System.out.println("dt2=" + dt2);
    }

    public static void m15() {
        try {
//            System.out.println(DbManager.getMessageByNumber(4149183));
            System.out.println(DAO.Site._getMessageByNumber(648064));
        } /*catch (DbException e) {
            e.printStackTrace();
        } */ catch (DAO.DAOException e) {
            e.printStackTrace();
        }
    }

    public static void m14() {
//        System.out.println(new int[2] instanceof Array);
//        System.out.println(ZloSearcher.search(9, null, true, true, false, false, false, null, null, null, null).getHits().length());
//        System.out.println(String.format("%1$tB, %1$td %1$tH:%1$tm:%1$tS %1$tY", new Date()));
//        System.out.println(String.format("%d{dd/MM/yy HH:mm:ss,SSS}", new Date()));
        for (int i = 0; i < 10; i++) {
            System.out.println("Cleaning...");
            ZloSearcher.clean();
            System.out.println("Creating " + i + " ...");
            ZloSearcher.getIndexReader();
            System.out.print("Searching... ");
            System.out.println(ZloSearcher.searchIndexReader(null, " +nick:Borisych", null).getHits().length());
        }
    }

    public static void m13() throws RuntimeException {
        try {
            System.out.println("throwing..");
            throw new RuntimeException("1");
        } catch (RuntimeException ex) {
            System.out.println("catching..");
            throw ex;
        } finally {
            System.out.println("finally");
        }
    }

    public static void m12() {
        try {
            for (ZloMessage m : DAO.Site._getMessages(10000, 10042)) {
                System.out.println(m);
            }
        } catch (DAO.DAOException e) {
            e.printStackTrace();
        }
    }

    public static void m11() {
        try {
            for (int i = 0; i < 6000; i++) {
                DAO.DB.SOURCE.getMessageByNumber(i);
            }
        } catch (DAO.DAOException e) {
            e.printStackTrace();
        }
    }

    public static void m10() {
        try {
//            System.out.println(new ZloStorage().getLastMessageNumber());
            System.out.println(DAO.DB._getLastMessageNumber());
        } catch (DAO.DAOException e) {
            e.printStackTrace();
        }
    }

    public static void m1() {
        QueryParser qp = new QueryParser("field1", new SimpleAnalyzer());
        try {
            Query q = qp.parse("[1.1.04 TO 5.30.05]");
            System.out.println(q.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void m2() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        System.out.println(df.format(new Date()));
    }

    public static void m3() {
        //new StandardAnalyzer().
        System.out.println(new Date().hashCode());
    }

    public static void m4() {
//        NumberFormat f = new DecimalFormat("0000000000");
//        System.out.println(f.format(-123));
//        System.out.println(Integer.parseInt(f.format(-123)));
//        System.out.println(ZloSearcher.searchMsgByNum(3765011));
        for (Object o : ZloSearcher.searchInNumRange(3765002, 3765007)) {
            System.out.println(o);
        }
    }

    public static void m5() {
        for (int i = 0; i < 10; i++) {
            try {
                System.out.println(">" + DAO.Site._getLastMessageNumber());
            } catch (DAO.DAOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class T extends Thread {
        private int i = 0;

        public T() {
        }

        public void run() {
            for (; i <= 10; i++) {
                System.out.println(">" + i);
            }
        }
    }

    public static void m6() {
        System.out.println("before");
        T t = new T();
        T t1 = new T();
        t.start();
        t1.start();
        try {
            t.join();
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("after");
    }

    public static void m7() {
        try {
            for (ZloMessage m : DAO.Site._getMessages(10, 110)) {
                System.out.println(m);
            }
        } catch (DAO.DAOException e) {
            e.printStackTrace();
        }
    }

    public static void m8() {
        try {
            System.out.println(DAO.Site._getMessageByNumber(3960198));
        } catch (DAO.DAOException e) {
            e.printStackTrace();
        }
        /*System.out.println(PageParser.parseMessage("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
                "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1251\" /><link rel=\"shortcut icon\" href=\"/favicon.ico\" /><link rel=\"stylesheet\" type=\"text/css\" href=\"/main.css\" /><meta http-equiv=\"Page-Exit\" content=\"progid:DXImageTransform.Microsoft.Fade(Duration=0.2)\" /><title>Форум-ФРТК-МФТИ : Программирование : я говорю про коды на FORTRAN-87, какие RPC?</title></head><body>\n" +
                "<script language=\"JavaScript\" type=\"text/javascript\">function popup(action, value, w, h){wnd=window.open(\"?\"+action+\"=\"+value,\"popup\",\"resizable=no,menubars=no,scrollbars=yes,width=\"+w+\",height=\"+h); }</script><div class=\"menu\"><A HREF=\"#3975000\">Перейти к ответам</A><A HREF=\"#Reply\">Ответить</A><A HREF=\"?index#3974909\" style=\"color:red;\">На главную страницу</A><a HREF=\"http://boards.alexzam.ru\">Поиск</A><A HREF=\"?register=form\">Регистрация</A><A HREF=\"?login=form\">Вход</A><A HREF=\"?rules\">Правила</A></div><BR><DIV ALIGN=CENTER><BIG>[Программирование]</BIG>&nbsp;&nbsp;<BIG>я говорю про коды на FORTRAN-87, какие RPC?</BIG><BR>Сообщение было послано: <b>Ник0лай</b><SMALL> (unreg)</SMALL> <small>(88.84.192.198)</small><BR>Дата: Пятница, Сентябрь 14 22:11:33 2007</DIV><BR><br /><div class=\"body\">Я говорю о синтаксисе вроде<BR><PRE STYLE=\"margin-left:25px\">\n" +
                "\n" +
                "\tSUBROUTINE RTMAP( JOPT, MSK, JER )\n" +
                "\tCHARACTER FNAME*20,NAME*20,JNAME*20\n" +
                "\tCHARACTER RTNAM*16\n" +
                "\tDIMENSION KER(2)\n" +
                "\tDIMENSION ER1(2),DELTA(2)\n" +
                "\tREAL*8 DMIN,DMAX,D,GM\n" +
                "\tINTEGER*2 MSK(1)\n" +
                "        INTEGER*4 TIM\n" +
                "\tCOMPLEX*16 ZD(6500),ZV(256),Z\n" +
                "\tCOMPLEX CF(130),FCT\n" +
                "\tinteger*2 ipremap\n" +
                "\tcommon /premap/ ipremap\n" +
                "\tCOMMON /RTM/ ZV,CF,DEL,ER,MD,KD,KF,LF,IENT\n" +
                "\tCOMMON /RTDMN/ ZD,ND(3),INDX(2,200),NK\n" +
                "\tCOMMON /PARAM/ FNAME,NAME,DLT,EP,EP1,IPLT,JOURN,JPLAY,IPG\n" +
                "\n" +
                "        CALL GETTIM(IHR,IMN,ISC,IDC)\n" +
                "        TIM=(INT4(IHR)*60+IMN)*60+ISC\n" +
                "\tJER=0\n" +
                "\tMSK(1)=3\n" +
                "\tIF(JPLAY.EQ.0) THEN\n" +
                "\t  MSK(4)=3\n" +
                "\tELSE\n" +
                "\t  MSK(4)=1\n" +
                "\tENDIF\n" +
                "\tDELTA(1)=DLT\n" +
                "\tDELTA(2)=DLT\n" +
                "\tND3=ND(1)+ND(2)+ND(3)\n" +
                "\tIF(JOPT.EQ.1) THEN\n" +
                "\t  NR=1\n" +
                "\t  NK=0\n" +
                "\tELSE\n" +
                "\t  NR=IABS(INDX(1,NK))+INDX(2,NK)+2\n" +
                "\tENDIF\n" +
                "\tKER(1)=ND(1)\n" +
                "\tKER(2)=ND(2)</PRE><BR><BR>О чём Вы говорите! современный Фортран такое, уверен, не скомпилит даже.</div><P></P><BR><CENTER><BIG>Сообщения в этом потоке</BIG></CENTER><DIV class=w><span id=m3974909"))*/
        ;
/*        try {
            System.out.println(new ZloStorage().getMessageByNumber(3960198));
        } catch (DAO.DAOException e) {
            e.printStackTrace();
        }*/
    }

    public static void m9() {
        try {
            List<ZloMessage> l = DAO.Site._getMessages(3999995, 3999999);
//            Collections.
            for (ZloMessage m : l) {
                System.out.println(m);
            }
            System.out.println("#############################################");
            List<ZloMessage> l1 = DAO.Site._getMessages(4000000, 4000005);
            for (ZloMessage m : l1) {
                System.out.println(m);
            }
        } catch (DAO.DAOException e) {
            e.printStackTrace();
        }
    }
}
