package org.xonix.zlo.web;

/**
 * Author: gubarkov
 * Date: 14.08.2007
 * Time: 16:44:39
 */
public class SearchBean {
    private String text = "";

    public ResultLine[] getSearchResult() {
        ResultLine[] res = new ResultLine[10];

        for (int i=0; i<10; i++) {
            ResultLine rl = new ResultLine();
            rl.setCompName("comp_name_" + text);
            rl.setFileName("file_name_" + text);
            rl.setFileSize(text.length());
            res[i] = rl;
        }
        return res;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
