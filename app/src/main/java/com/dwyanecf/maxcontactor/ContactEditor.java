package com.dwyanecf.maxcontactor;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.TimeZone;

public class ContactEditor extends Activity {

    private static final String TAG = "ContactEditor";


    private static final int STATE_EDIT = 0;
    private static final int STATE_INSERT = 1;

    private static final int REVERT_ID = Menu.FIRST;
    private static final int DISCARD_ID = Menu.FIRST + 1;
    private static final int DELETE_ID = Menu.FIRST + 2;

    private int mState;
    private Uri mUri;
    private Cursor mCursor;

    private EditText nameText;
    private EditText lnameText;
    private EditText mPhoneText;
    private EditText emailText;
    Button BtnDate;
    private Button saveButton;
    private Button cancelButton;
    TextView mDate;
    TextView mTime;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    private String originalNameText = "";
    private String originalLNameText = "";
    private String originalMPhoneText = "";
    private String originalEmailText = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final String action = intent.getAction();
        Log.e(TAG + ":onCreate", action);
        if (Intent.ACTION_EDIT.equals(action)) {
            mState = STATE_EDIT;
            mUri = intent.getData();
        } else if (Intent.ACTION_INSERT.equals(action)) {
            mState = STATE_INSERT;
            mUri = getContentResolver().insert(intent.getData(), null);

            if (mUri == null) {
                Log.e(TAG+":onCreate", "Failed to insert new Contact into " + getIntent().getData());
                finish();
                return;
            }
            setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));

        } else {
            Log.e(TAG+":onCreate", " unknown action");
            finish();
            return;
        }

        setContentView(R.layout.contact_editor);
        nameText = (EditText) findViewById(R.id.EditText01);
        mPhoneText = (EditText) findViewById(R.id.EditText02);
        emailText = (EditText) findViewById(R.id.EditText03);
        lnameText= (EditText) findViewById(R.id.last);
        mDate=(TextView) findViewById(R.id.vdate);
        mTime=(TextView) findViewById(R.id.vtime);
        saveButton = (Button)findViewById(R.id.Button01);
        cancelButton = (Button)findViewById(R.id.Button02);
        BtnDate= (Button)findViewById(R.id.BtnDate);
        initTime();
        setDatetime();

        saveButton.setOnClickListener(new OnClickListener(){

            public void onClick(View v) {
                String text = nameText.getText().toString();
                if(text.length()==0){
                    setResult(RESULT_CANCELED);
                    deleteContact();
                    finish();
                }else{
                    updateContact();
                }
            }

        });
        cancelButton.setOnClickListener(new OnClickListener(){

            public void onClick(View v) {
                if(mState == STATE_INSERT){
                    setResult(RESULT_CANCELED);
                    deleteContact();
                    finish();
                }else{
                    backupContact();
                }

            }

        });

        Log.e(TAG+":onCreate", mUri.toString());
        // 获得并保存原始联系人信息
        mCursor = managedQuery(mUri, ContactColumn.PROJECTION, null, null, null);
        mCursor.moveToFirst();
        originalNameText = mCursor.getString(ContactColumn.NAME_COLUMN);
        originalMPhoneText = mCursor.getString(ContactColumn.MOBILE_COLUMN);
        originalEmailText = mCursor.getString(ContactColumn.EMAIL_COLUMN);

        Log.e(TAG, "end of onCreate()");
    }



    private void initTime(){
        Calendar c = Calendar. getInstance(TimeZone.getTimeZone("GMT-05:00"));
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH)+1;
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
    }

    private void setDatetime(){
        mDate.setText(mYear+"-"+mMonth+"-"+mDay);
        mTime.setText(pad(mHour)+":"+pad(mMinute));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, false);
            case 2:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth-1, mDay);
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case 1:
                ((TimePickerDialog) dialog).updateTime(mHour, mMinute);
                break;
            case 2:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth-1, mDay);
                break;
        }
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear+1;
                    mDay = dayOfMonth;

                    setDatetime();
                }
            };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {

                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinute = minute;
                    setDatetime();
                }
            };
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (mCursor != null) {
            Log.e(TAG+":onResume","count:"+mCursor.getColumnCount());
            // 读取并显示联系人信息
            mCursor.moveToFirst();
            if (mState == STATE_EDIT) {
                setTitle(getText(R.string.contact_edit));
            } else if (mState == STATE_INSERT) {
                setTitle(getText(R.string.contact_create));
            }
            String name = mCursor.getString(ContactColumn.NAME_COLUMN);
            String mPhone = mCursor.getString(ContactColumn.MOBILE_COLUMN);
            String email = mCursor.getString(ContactColumn.EMAIL_COLUMN);

            Log.e(TAG+":onResume","name:"+name+"mPhone:"+mPhone+"email:"+email);

            nameText.setText(name);
            mPhoneText.setText(mPhone);
            emailText.setText(email);

        }else{
            setTitle(getText(R.string.error_msg));
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCursor != null) {
            String text = nameText.getText().toString();

            if (text.length() == 0) {
                Log.e(TAG+":onPause","nameText is null ");
                setResult(RESULT_CANCELED);
                deleteContact();

                //更新信息
            } else {
                ContentValues values = new ContentValues();
                values.put(ContactColumn.NAME, nameText.getText().toString());
                values.put(ContactColumn.MOBILE, mPhoneText.getText().toString());
                values.put(ContactColumn.EMAIL, emailText.getText().toString());
                Log.e(TAG+":onPause",mUri.toString());
                Log.e(TAG+":onPause",values.toString());
                getContentResolver().update(mUri, values, null, null);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (mState == STATE_EDIT) {
            menu.add(0, REVERT_ID, 0, R.string.menu_revert)
                    .setShortcut('0', 'r')
                    .setIcon(android.R.drawable.ic_menu_revert);
            menu.add(0, DELETE_ID, 0, R.string.menu_delete)
                    .setShortcut('0', 'd')
                    .setIcon(android.R.drawable.ic_menu_delete);

        } else {
            menu.add(0, DISCARD_ID, 0, R.string.menu_discard)
                    .setShortcut('0', 'd')
                    .setIcon(android.R.drawable.ic_menu_delete);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                deleteContact();
                finish();
                break;
            case DISCARD_ID:
                cancelContact();
                break;
            case REVERT_ID:
                backupContact();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //删除联系人信息
    private void deleteContact() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
            getContentResolver().delete(mUri, null, null);
            nameText.setText("");
        }

    }
    //丢弃信息
    private void cancelContact() {
        if (mCursor != null) {
            deleteContact();
        }
        setResult(RESULT_CANCELED);
        finish();

    }
    //更新 变更的信息
    private void updateContact() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
            ContentValues values = new ContentValues();
            values.put(ContactColumn.NAME, nameText.getText().toString());
            values.put(ContactColumn.MOBILE, mPhoneText.getText().toString());
            values.put(ContactColumn.EMAIL, emailText.getText().toString());
            Log.e(TAG+":onPause",mUri.toString());
            Log.e(TAG+":onPause",values.toString());
            getContentResolver().update(mUri, values, null, null);
        }
        setResult(RESULT_CANCELED);
        finish();

    }
    //取消用，回退到最初的信息
    private void backupContact() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
            ContentValues values = new ContentValues();
            values.put(ContactColumn.NAME, this.originalNameText);
            values.put(ContactColumn.MOBILE,this.originalMPhoneText);
            values.put(ContactColumn.EMAIL, this.originalEmailText);
            Log.e(TAG+":onPause",mUri.toString());
            Log.e(TAG+":onPause",values.toString());
            getContentResolver().update(mUri, values, null, null);
        }
        setResult(RESULT_CANCELED);
        finish();

    }

}