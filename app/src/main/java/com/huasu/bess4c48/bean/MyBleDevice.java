package com.huasu.bess4c48.bean;

import android.text.TextUtils;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 蓝牙基本信息
 */
@Entity(tableName = "tb_ble_device")
public class MyBleDevice {
    @PrimaryKey(autoGenerate = true)//主键是否自动增长，默认为false
    public Long id;
    public String bleAddress;
    public String bleName;
    public String updateName;

    public MyBleDevice(String bleAddress, String bleName, String updateName) {
        this.bleAddress = bleAddress;
        this.bleName = bleName;
        this.updateName = updateName;
    }

    public String getBleName() {
        return TextUtils.isEmpty(updateName)?bleName:updateName;
    }
}
