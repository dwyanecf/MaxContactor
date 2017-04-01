package com.dwyanecf.maxcontactor;

/**
 * Created by fan on 2016/3/23.
 */
import android.provider.BaseColumns;
//加了
public class ContactColumn implements BaseColumns {
    public ContactColumn(){
    }
    //列名
    public static final String NAME = "name";
    public static final String MOBILE = "mobileNumber";
    public static final String EMAIL = "email";
    public static final String CREATED = "createdDate";
    public static final String MODIFIED = "modifiedDate";
    //列 索引值
    public static final int _ID_COLUMN = 0;
    public static final int NAME_COLUMN = 1;
    public static final int MOBILE_COLUMN = 2;
    public static final int EMAIL_COLUMN = 3;
    public static final int CREATED_COLUMN = 4;
    public static final int MODIFIED_COLUMN = 5;
    //查询结果
    public static final String[] PROJECTION ={
            _ID,//0
            NAME,//1
            MOBILE,//2
            EMAIL//3
    };

}
