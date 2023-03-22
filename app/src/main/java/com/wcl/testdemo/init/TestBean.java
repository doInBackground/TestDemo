package com.wcl.testdemo.init;

/**
 * @Author WCL
 * @Date 2022/11/16 10:38
 * @Version
 * @Description Bean数据类.
 */
public class TestBean {
    /**
     * Comment: 文字内容.
     */
    private String text;

    public TestBean(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}