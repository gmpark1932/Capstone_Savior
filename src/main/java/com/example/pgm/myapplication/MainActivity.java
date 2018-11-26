package com.example.pgm.myapplication;

/**
 * Created by DowonYoon on 2017-06-21.
 */

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Main";

    Button btn_Insert;
    Button btn_send;
    Button btn_gps;
    EditText edit_Name;
    EditText edit_Phone;
    TextView text_Name;
    TextView text_Phone;
    TextView current_GPS;

    long nowIndex;
    String name;
    long phone;
    String sort = "phone";

    ArrayAdapter<String> arrayAdapter;

    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();
    private DbOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_Insert = (Button) findViewById(R.id.insert_button);
        btn_Insert.setOnClickListener(this);
        edit_Name = (EditText) findViewById(R.id.edit_name);
        edit_Phone = (EditText) findViewById(R.id.edit_phone);
        btn_send = (Button) findViewById(R.id.send_button);
        btn_send.setOnClickListener(this);
        current_GPS = (TextView)findViewById(R.id.current_gps);
        btn_gps = (Button) findViewById(R.id.gps_button);
        btn_gps.setOnClickListener(this);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.db_list);
        listView.setAdapter(arrayAdapter);

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        showDatabase(sort);

        btn_Insert.setEnabled(true);
        btn_send.setEnabled(true);
        btn_gps.setEnabled(true);
    }

    public void setInsertMode(){
        edit_Name.setText("");
        edit_Phone.setText("");
        btn_Insert.setEnabled(true);
        btn_send.setEnabled(true);
        btn_gps.setEnabled(true);
    }

    public void showDatabase(String sort){
        Cursor iCursor = mDbOpenHelper.sortColumn(sort);
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        arrayData.clear();
        arrayIndex.clear();
        while(iCursor.moveToNext()){
            String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            String tempName = iCursor.getString(iCursor.getColumnIndex("name"));
            tempName = setTextLength(tempName,10);
            String tempPhone = iCursor.getString(iCursor.getColumnIndex("phone"));
            tempPhone = setTextLength(tempPhone,10);

            String Result = tempName + tempPhone;
            arrayData.add(Result);
            arrayIndex.add(tempIndex);
        }
        arrayAdapter.clear();
        arrayAdapter.addAll(arrayData);
        arrayAdapter.notifyDataSetChanged();
    }

    public String setTextLength(String text, int length){
        if(text.length()<length){
            int gap = length - text.length();
            for (int i=0; i<gap; i++){
                text = text + " ";
            }
        }
        return text;
    }

    private void sendSMS(String phoneNumber, String message)
    {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.insert_button:
                name = edit_Name.getText().toString();
                phone = Long.parseLong(edit_Phone.getText().toString());
                mDbOpenHelper.open();
                mDbOpenHelper.insertColumn(name, phone);
                showDatabase(sort);
                setInsertMode();
                break;
            case R.id.send_button:
                Cursor iCursor = mDbOpenHelper.sortColumn(sort);
                Log.d("showDatabase", "DB Size: " + iCursor.getCount());
                arrayData.clear();
                arrayIndex.clear();
                while(iCursor.moveToNext()){
                    String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
                    String tempName = iCursor.getString(iCursor.getColumnIndex("name"));
                    tempName = setTextLength(tempName,10);
                    String tempPhone = iCursor.getString(iCursor.getColumnIndex("phone"));
                    tempPhone = setTextLength(tempPhone,10);

                    //문자 보내기 필요하다.
                    String emergency_message = "위급상황입니다.";
                    sendSMS("01050411932",emergency_message);
                }
                arrayAdapter.clear();
                arrayAdapter.addAll(arrayData);
                arrayAdapter.notifyDataSetChanged();
                break;
            case R.id.gps_button:
                current_GPS.setText("1111");
                break;
        }
    }



}
