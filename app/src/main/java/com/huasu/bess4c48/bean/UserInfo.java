package com.huasu.bess4c48.bean;

import androidx.room.PrimaryKey;

import com.huasu.bess4c48.utils.ToolUtil;

/**
 * 用户信息
 */
public class UserInfo {
    @PrimaryKey(autoGenerate = true)//主键是否自动增长，默认为false
    public Long id; //用户id
    public int privilegeLevel; //用户等级
    public String loginName;     //用户名
    public Long lastLoginDate; //最后一次登录时间
    public String loginPassword; //登录密码

    public boolean isSysAdmin() {
        return privilegeLevel == 1 ? true : false;
    }

    public String getUserType() {
       return "--";
    }

    public String getLoginTime() {
        return ToolUtil.formatDate(lastLoginDate);
    }



}
