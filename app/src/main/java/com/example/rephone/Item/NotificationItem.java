package com.example.rephone.Item;

public class NotificationItem {
    private String logType;
    private String occurDate;

    public NotificationItem(String logType, String occurDate) {
        this.logType = logType;
        this.occurDate = occurDate;
    }

    public NotificationItem(String str) {
        String[] strArr = str.split(",");
        if (strArr[0].equals("sound"))
            this.logType = "소리";
        else if (strArr[0].equals("motion"))
            this.logType = "움직임";
        this.occurDate = strArr[1];
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getOccurDate() {
        return occurDate;
    }

    public void setOccurDate(String occurDate) {
        this.occurDate = occurDate;
    }
}
