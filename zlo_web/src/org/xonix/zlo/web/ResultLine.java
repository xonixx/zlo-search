package org.xonix.zlo.web;

/**
 * Author: gubarkov
 * Date: 14.08.2007
 * Time: 17:58:28
 */
public class ResultLine {
    private String compName;
    private String fileName;
    private int fileSize;

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
}
