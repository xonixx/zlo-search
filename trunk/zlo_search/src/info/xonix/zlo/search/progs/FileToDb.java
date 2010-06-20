package info.xonix.zlo.search.progs;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.dao.DbManagerImpl;
import info.xonix.zlo.search.model.ZloMessage;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Vovan
 * Date: 16.10.2007
 * Time: 15:19:58
 */
public class FileToDb {
    public static final String CSV_FILE_NAME = "c:/zlo/messages_alexzam_1.csv";
//    public static final String CSV_FILE_NAME = "c:/zlo/out.txt";
    
    public static final String ENCODING = "UTF-16";
    public static final String COLUMN_DELIMITER = "<cd>";
    public static final String ROW_DELIMITER = "<rd>";

    public static final int BUFFER_LEN = 50000;
    private char[] BUF = new char[BUFFER_LEN];

    public static final DateFormat MSSQL_DATE_FORMAT = new SimpleDateFormat("yyyy-M-d hh:mm:ss");

    public static final String FALSE = "False";

    public static final Logger logger = Logger.getLogger(FileToDb.class);

    private void start(boolean doWork) {
        Site site = Site.forName("zlo");
        DbManagerImpl dbm = site.getDbManager();
        String lastLine = "";
        int result;
        try {

            Reader fr = new InputStreamReader(new FileInputStream(new File(CSV_FILE_NAME)), ENCODING);
/*
            long skipped = fr.skip(start);
            if (skipped == -1) {
                throw new IOException("Result skipped=-1");
            }

            char[] buff = new char[4];
            do {
                buff[0] = buff[1];
                buff[1] = buff[2];
                buff[2] = buff[3];
                buff[3] = buff[4];
                buff[4] = (char)fr.read();
                System.out.println(">" + new String(buff));
            } while (!COLUMN_DELIMITER.equals(new String(buff)));
*/

            do {
                result = fr.read(BUF);
                if (result == -1) {
                    break; //throw new IOException("Result read=-1");
                }

                String s = lastLine + new String(BUF);
                String[] lines = s.split(ROW_DELIMITER);
                lastLine = lines[lines.length - 1];

                List<ZloMessage> msgs = new ArrayList<ZloMessage>(lines.length - 1);

                for (int i = 0; i < lines.length - 1; i++) {
                    String[] columnVals = lines[i].split(COLUMN_DELIMITER);

                    try {
                        msgs.add(new ZloMessage(
                                site,
                                columnVals[2], // userName
                                columnVals[3], // alt
                                columnVals[4], // host
                                formTopic(columnVals[8]),
                                formTopicCode(columnVals[8]),
                                columnVals[1],
                                columnVals[6],
                                MSSQL_DATE_FORMAT.parse(columnVals[5]),
                                FALSE.equals(columnVals[13]),
                                Integer.parseInt(columnVals[0]), // num
                                Integer.parseInt(columnVals[7]), // parentNum
                                0));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        logger.error("Problem with line:\n" + lines[i]);
                    }
                }

                Collections.sort(msgs, ZloMessage.NUM_COMPARATOR);
                if (msgs.size() > 0) {
                    int min = msgs.get(0).getNum();
                    int max = msgs.get(msgs.size() - 1).getNum();
                    logger.info("Saving msgs (" + min + " - " + max + ")...");
                } else {
                    logger.info("Nothing to save");
                    // add read chars to lastLine
                    lastLine = s;
                }
                if (doWork)
                    dbm.saveMessagesFast(msgs);
                else {
                    for (ZloMessage m : msgs) {
                        System.out.println(m);
                    }
                }
            } while (result == BUF.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Done.");
    }

    private int formTopicCode(String category) {
        int cat = Integer.parseInt(category);
        switch(cat) {
            case 0: return 0; // без темы
            case 1: return 3; // мурзилка
            case 2: return 9; // программирование
            case 3: return 2; // работа
            case 4: return 1; // учеба
            case 5: return 4; // обсуждение
            case 6: return 10; // куплю
            case 7: return 8; // движок борды
            case 8: return 7; // развлечения
            case 9: return 11; // продам
            case 10: return 18; // temp
            case 11: return 12; // услуги
            case 12: return -1; //?
            case 13: return 13; // win
            case 14: return 15; // проблемы сети
            case 15: return 5; // новости
            case 16: return 16; // голосование
            case 17: return -1; //?
            case 18: return 6; // спорт
            // нет 12, 17 <==> -bsd/linux, -Потеряно/Найдено
        }
        return -1;
    }

    private String formTopic(String category) {
        int cat = Integer.parseInt(category);
        switch(cat) {
            case 0: return "Без темы"; // без темы
            case 1: return "Мурзилка"; // мурзилка
            case 2: return "Программирование"; // программирование
            case 3: return "Работа"; // работа
            case 4: return "Учеба"; // учеба
            case 5: return "Обсуждение"; // обсуждение
            case 6: return "Куплю"; // куплю
            case 7: return "Движок борды"; // движок борды
            case 8: return "Развлечения"; // развлечения
            case 9: return "Продам"; // продам
            case 10: return "Temp"; // temp
            case 11: return "Услуги"; // услуги
            case 12: return "?"; //?
            case 13: return "Windows"; // win
            case 14: return "Проблемы сети"; // проблемы сети
            case 15: return "Новости"; // новости
            case 16: return "Голосование"; // голосование
            case 17: return "?"; //?
            case 18: return "Спорт"; // спорт
            // нет 12, 17 <==> -bsd/linux, -Потеряно/Найдено
        }
        return "?";
    }

    public static void main(String[] args) {
        new Config(); // call static constractors
        new FileToDb().start(true);
    }
}
