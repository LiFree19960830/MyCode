package com.huasu.bess4c48.bean;

/**
 * 历史数据模版
 */
public class HistoryDataTemplate {
    public float zoom;
    public int offsetVal;
    public int precision;
    public int position;
    public String method;

    public HistoryDataTemplate(float zoom, int offsetVal, int precision, int position, String method) {
        this.zoom = zoom;
        this.offsetVal = offsetVal;
        this.precision = precision;
        this.position = position;
        this.method = method;
    }
}
