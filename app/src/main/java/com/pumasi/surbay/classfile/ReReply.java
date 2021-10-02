package com.pumasi.surbay.classfile;

import java.util.ArrayList;
import java.util.Date;

public class ReReply {
    private String id;
    private ArrayList<String> reports;
    private ArrayList<String> report_reasons;
    private boolean hide;
    private String writer;



    private String writer_name;
    private String content;
    private Date date;
    private String replyID;

    public ReReply(String id, ArrayList<String> reports, ArrayList<String> report_reasons, boolean hide, String writer, String writer_name, String content, Date date, String replyID) {
        this.id = id;
        this.reports = reports;
        this.report_reasons = report_reasons;
        this.hide = hide;
        this.writer = writer;
        this.writer_name = writer_name;
        this.content = content;
        this.date = date;
        this.replyID = replyID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getReports() {
        return reports;
    }

    public void setReports(ArrayList<String> reports) {
        this.reports = reports;
    }

    public ArrayList<String> getReport_reasons() {
        return report_reasons;
    }

    public void setReport_reasons(ArrayList<String> report_reasons) {
        this.report_reasons = report_reasons;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getReplyID() {
        return replyID;
    }

    public void setReplyID(String replyID) {
        this.replyID = replyID;
    }
    public String getWriter_name() {
        return writer_name;
    }

    public void setWriter_name(String writer_name) {
        this.writer_name = writer_name;
    }
}
