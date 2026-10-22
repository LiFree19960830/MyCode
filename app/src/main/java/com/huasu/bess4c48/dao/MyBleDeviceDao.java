package com.huasu.bess4c48.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.huasu.bess4c48.bean.MyBleDevice;

import java.util.List;

/**
 * 我的已连接蓝牙设备
 */
@Dao
public interface MyBleDeviceDao {


    //onConflict = OnConflictStrategy.REPLACE 表示插入数据重复了直接修改保证数据的不重复。
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MyBleDevice data);

    @Query("select * from tb_ble_device where bleAddress=:address")
    List<MyBleDevice>  queryByAddress(String address);
    @Delete
    void delete(MyBleDevice device);


    @Query("select * from tb_ble_device")
    List<MyBleDevice> selectAll();



}
