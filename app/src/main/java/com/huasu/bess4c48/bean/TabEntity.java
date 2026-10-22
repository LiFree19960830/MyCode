package com.huasu.bess4c48.bean;

import com.flyco.tablayout.listener.CustomTabEntity;
import com.huasu.bess4c48.utils.CPResourceUtil;

public class TabEntity implements CustomTabEntity {
    public String title;
    public int selectedIcon;
    public int unSelectedIcon;

    public TabEntity(int titleResId, int selectedIcon, int unSelectedIcon) {
        this.title = CPResourceUtil.getResStringByResId(titleResId);
        this.selectedIcon = selectedIcon;
        this.unSelectedIcon = unSelectedIcon;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public int getTabSelectedIcon() {
        return selectedIcon;
    }

    @Override
    public int getTabUnselectedIcon() {
        return unSelectedIcon;
    }
}
