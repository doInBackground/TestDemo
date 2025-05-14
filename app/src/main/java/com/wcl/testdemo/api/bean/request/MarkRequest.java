package com.wcl.testdemo.api.bean.request;


/**
 * @Author WCL
 * @Date 2025/5/14 10:37
 * @Version
 * @Description 钛马星[标记广告位点击或展示]API请求体.
 */
public class MarkRequest {
    public static final String DISPLAY = "MARK_TYPE_DISPLAY";//展示.
    public static final String CLICK = "MARK_TYPE_CLICK";//点击.
    public long id;//广告ID.
    public String markType;//标记类型.

    private MarkRequest(long id, String markType) {
        this.id = id;
        this.markType = markType;
    }

    public static MarkRequest display(long id) {
        return new MarkRequest(id, DISPLAY);
    }

    public static MarkRequest click(long id) {
        return new MarkRequest(id, CLICK);
    }
}
