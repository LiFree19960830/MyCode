package com.huasu.bess4c48.bean;

import java.util.ArrayList;
import java.util.List;

public class ParentParam {
    public String title;
    public List<DeviceParam> childs=new ArrayList<>();
    public boolean isShowReadBtn;
    public boolean isShowControl;
    public boolean isStorageControl;//是否强控中
    public boolean isShowTab=true;//是否显示tab
}
