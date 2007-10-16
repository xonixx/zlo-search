package org.xonix.zlo.search.progs;

import java.io.*;

/**
 * Author: Vovan
 * Date: 16.10.2007
 * Time: 15:19:58
 */
public class FileToDb {
    public static final String CSV_FILE_NAME = "D:/TEST/sql/messages_alexzam_1.csv";
    public static final String ENCODING = "UTF-8";
    public static final String COLUMN_DELIMITER = "<cd>";
    public static final String ROW_DELIMITER = "<rd>";

    public static final int BUFFER_LEN = 10000;
    private char[] BUF = new char[BUFFER_LEN];

    public void main0() {
        try {
            Reader fr = new InputStreamReader(new FileInputStream(new File(CSV_FILE_NAME)), ENCODING);
            //fr.read(BUF);

            String s = new String(BUF);
            System.out.println(s);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new FileToDb().main0();
    }
}
