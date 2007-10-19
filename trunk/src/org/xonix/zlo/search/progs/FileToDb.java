package org.xonix.zlo.search.progs;

import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.DBManager;
import org.xonix.zlo.search.DBException;
import org.xonix.zlo.search.config.Config;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

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

    private void start(int start) {
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
                                columnVals[2], // userName
                                columnVals[4], // host
                                Integer.parseInt(columnVals[8]),
                                columnVals[1],
                                columnVals[6],
                                MSSQL_DATE_FORMAT.parse(columnVals[5]),
                                FALSE.equals(columnVals[13]),
                                Integer.parseInt(columnVals[0]),
                                null,
                                null,
                                ZloMessage.Status.OK
                        ));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        logger.error("Problem with line:\n"+lines[i]);
                    }
                }

    //            for (ZloMessage m:msgs) {
    //                System.out.println(m);
    //            }
                Collections.sort(msgs, ZloMessage.NUM_COMPARATOR);
                if (msgs.size() > 0) {
                    int min = msgs.get(0).getNum();
                    int max = msgs.get(msgs.size()-1).getNum();
                    logger.info("Saving msgs (" + min + " - " + max + ")...");
                } else {
                    logger.info("Nothing to save");
                    // add read chars to lastLine
                    lastLine = s;
                }
                DBManager.saveMessagesFast(msgs);
            } while (result == BUF.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DBException e) {
            e.printStackTrace();
        }

        logger.info("Done.");

    }

    private void start() {
        start(0);
    }

    public static void main(String[] args) {
        new Config(); // call static constractors
        new FileToDb().start();
    }
}
