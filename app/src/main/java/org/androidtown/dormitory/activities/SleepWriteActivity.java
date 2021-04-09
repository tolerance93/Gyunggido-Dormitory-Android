package org.androidtown.dormitory.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidtown.dormitory.R;
import org.androidtown.dormitory.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SleepWriteActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText buildingEditText;
    private EditText buildingNumberEditText;
    private EditText fromEditText;
    private EditText toEditText;
    private EditText destinationEditText;
    private EditText reasonEditText;
    private Button doneButton;

    private ProgressDialog mSleepProgress;
    private ProgressDialog mSleepUploadProgress;

    private static final int DIALOG_ID_FIRST = 0;
    private static final int DIALOG_ID_SECOND = 1;

    Calendar initialCalendar = Calendar.getInstance( );
    private int year_x = initialCalendar.get(Calendar.YEAR);
    private int month_x = initialCalendar.get(Calendar.MONTH) + 1;
    private int day_x = initialCalendar.get(Calendar.DAY_OF_MONTH);
    private int year_y = initialCalendar.get(Calendar.YEAR);
    private int month_y = initialCalendar.get(Calendar.MONTH) + 1;
    private int day_y = initialCalendar.get(Calendar.DAY_OF_MONTH) +1;

    private int mHour = initialCalendar.get(Calendar.HOUR_OF_DAY);

    private long start_time = System.currentTimeMillis();
    private DatePickerDialog datePickerDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mSleepDatabase;

    private String uid;

    private User user;

    private static final String TAG = "qkrrhksdyd";

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_write);

        nameEditText = findViewById(R.id.sleep_write_name);
        buildingEditText = findViewById(R.id.sleep_write_building);
        buildingNumberEditText = findViewById(R.id.sleep_write_building_number);
        fromEditText = findViewById(R.id.sleep_write_from);
        toEditText = findViewById(R.id.sleep_write_to);
        destinationEditText = findViewById(R.id.sleep_write_destination);
        reasonEditText = findViewById(R.id.sleep_write_reason);
        doneButton = findViewById(R.id.sleep_write_done_button);

        mSleepProgress = new ProgressDialog(this);
        mSleepProgress.setTitle("로딩중");
        mSleepProgress.setMessage("잠시만 기다려주세요.");
        mSleepProgress.show();

        mToolbar = findViewById(R.id.sleep_write_toolbar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.google_cancel_white);
        mTitle.setText("외박 신청");
        mTitle.setTextColor(Color.WHITE);


        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        uid = current_user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                nameEditText.setText(user.getName());
                mSleepProgress.dismiss();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SleepWriteActivity.this, databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
                mSleepProgress.dismiss();

            }
        });

        showDialogOnDateFromSelectClicked();
        showDialogOnDateToSelectClicked();
        doneButtonClicked();

    }

    private void doneButtonClicked() {
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is black?
                if (buildingEditText.getText().toString().equals("")) {
                    Toast.makeText(SleepWriteActivity.this, "건물을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if (buildingNumberEditText.getText().toString().equals("")) {
                    Toast.makeText(SleepWriteActivity.this, "호수 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if (fromEditText.getText().toString().equals("")) {
                    Toast.makeText(SleepWriteActivity.this, "시작일을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if (toEditText.getText().toString().equals("")) {
                    Toast.makeText(SleepWriteActivity.this, "도착일을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if (destinationEditText.getText().toString().equals("")) {
                    Toast.makeText(SleepWriteActivity.this, "행선지를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if (reasonEditText.getText().toString().equals("")) {
                    Toast.makeText(SleepWriteActivity.this, "사유를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                mSleepUploadProgress = new ProgressDialog(SleepWriteActivity.this);
                mSleepUploadProgress.setTitle("신청중");
                mSleepUploadProgress.setMessage("잠시만 기다려주세요.");
                mSleepUploadProgress.show();

                mSleepDatabase = FirebaseDatabase.getInstance().getReference().child("Sleep").push();
                ValueEventListener sleepListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final HashMap<String, Object> sleepMap = new HashMap<String, Object>();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd HH:mm");
                        final String currentDateandTime = sdf.format(new Date());


                        sleepMap.put("building", buildingEditText.getText().toString());
                        sleepMap.put("buildingNumber",buildingNumberEditText.getText().toString());
                        sleepMap.put("destination",destinationEditText.getText().toString());
                        sleepMap.put("from",fromEditText.getText().toString());
                        sleepMap.put("to",toEditText.getText().toString());
                        sleepMap.put("name",nameEditText.getText().toString());
                        sleepMap.put("orderTime", Double.valueOf((System.currentTimeMillis())));
                        sleepMap.put("reason",reasonEditText.getText().toString());
                        sleepMap.put("status","미확인");
                        sleepMap.put("time",currentDateandTime.toString());
                        sleepMap.put("uid",uid);

                        mSleepDatabase.setValue(sleepMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mSleepUploadProgress.dismiss();
//                                    InputMethodManager inputManager = (InputMethodManager)
//                                            getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                    inputManager.hideSoftInputFromWindow(mNoticeWriteContentEdit.getApplicationWindowToken(), 0);
                                    finish();
                                }else{
                                    mSleepUploadProgress.dismiss();
                                    Toast.makeText(SleepWriteActivity.this,"Error, try again",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        mSleepUploadProgress.dismiss();
                        Toast.makeText(SleepWriteActivity.this, databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
                    }
                };
                mSleepDatabase.addListenerForSingleValueEvent(sleepListener);
            }
        });

    }

    public void showDialogOnDateFromSelectClicked(){
        fromEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyBoard();
                datePickerDialog = new DatePickerDialog(SleepWriteActivity.this, dPickerListener, year_x,month_x-1,day_x);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });
    }
    public void showDialogOnDateToSelectClicked(){
        toEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyBoard();
                datePickerDialog = new DatePickerDialog(SleepWriteActivity.this, dPickerListenerTWO, year_y,month_y-1,day_y);
                datePickerDialog.getDatePicker().setMinDate(start_time);
                datePickerDialog.show();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener dPickerListener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            year_x = year;
            month_x = month+1;
            day_x = dayOfMonth;
            String str_day_x = "";
            if ( day_x / 10 == 0){
                str_day_x = "0" + String.valueOf(day_x);
            } else {
                str_day_x =  String.valueOf(day_x);
            }


            Calendar calendar = Calendar.getInstance();
            calendar.set(year_x,month_x-1,dayOfMonth);
            start_time = calendar.getTimeInMillis();

            fromEditText.setText(year_x+ "-"+ month_x+ "-"+ str_day_x);
        }
    };

    private DatePickerDialog.OnDateSetListener dPickerListenerTWO = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            year_y = year;
            month_y = month+1;
            day_y = dayOfMonth;

            String str_day_y = "";
            if ( day_x / 10 == 0){
                str_day_y = "0" + String.valueOf(day_y);
            } else {
                str_day_y =  String.valueOf(day_y);
            }

            toEditText.setText(year_y+ "-"+ month_y+ "-"+ str_day_y);

        }
    };


    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
