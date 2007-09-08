package org.xonix.zlo.search;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 17:36:44
 */
public class PageRetriever {
    public static String getPageContentByNumber(int num) throws IOException{
        String uri = "http://" + Config.INDEXING_URL + Config.READ_QUERY + num;
        System.out.println("Retrieving: "+uri);
        HttpClient httpClient = new HttpClient();

        GetMethod getMethod = new GetMethod(uri);
        getMethod.addRequestHeader("Host", Config.INDEXING_URL);
        httpClient.executeMethod(getMethod);
        // реализовано чтение до "<BIG>Сообщения в этом потоке</BIG>"
        InputStream is = getMethod.getResponseBodyAsStream();
        List<String> stringGroups = new ArrayList<String>();
        stringGroups.add("");

        int currSize;
        do {
            byte[] buff = new byte[Config.BUFFER];
            int lenRead = is.read(buff);
            //System.out.println(">>"+lenRead);
            if (lenRead == -1)
                break;
            stringGroups.add(new String(buff, 0, lenRead, Config.CHARSET_NAME));
            currSize = stringGroups.size();
        } while(
            (stringGroups.get(currSize - 2) + stringGroups.get(currSize - 1)).indexOf(Config.END_MSG_MARK) == -1
            );
        is.close();
        StringBuffer sb = new StringBuffer(stringGroups.size());
        for (String s : stringGroups) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            System.out.println(getPageContentByNumber(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
