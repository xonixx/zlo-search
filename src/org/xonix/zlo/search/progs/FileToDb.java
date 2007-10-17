package org.xonix.zlo.search.progs;

import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.DAO;
import org.xonix.zlo.search.DBManager;
import org.xonix.zlo.search.DBException;
import org.xonix.zlo.search.config.Config;
import org.apache.log4j.Logger;
import org.apache.commons.collections.CollectionUtils;

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
    public static final String ENCODING = "UTF-16";
    public static final String COLUMN_DELIMITER = "<cd>";
    public static final String ROW_DELIMITER = "<rd>";

    public static final int BUFFER_LEN = 50000;
    private char[] BUF = new char[BUFFER_LEN];

    public static final DateFormat MSSQL_DATE_FORMAT = new SimpleDateFormat("yyyy-M-d hh:mm:ss");

    public static final String FALSE = "False";

    public static final Logger logger = Logger.getLogger(FileToDb.class);

    public void main0() {
        String lastLine = "";
        int result = 0;
        try {

            Reader fr = new InputStreamReader(new FileInputStream(new File(CSV_FILE_NAME)), ENCODING);
            do {
                result = fr.read(BUF);
                if (result == -1) {
                    throw new IOException("Result read=-1");
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

    public static void main(String[] args) {
        new Config(); // call static constractors
        new FileToDb().main0();
    }
}
