package org.androidtown.dormitory.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import org.androidtown.dormitory.display.SleepMainDisplay;
import org.androidtown.dormitory.model.User;

public class SleepReadActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private DatabaseReference mSleepDatabase;
    private DatabaseReference mSleepWriterDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference mUserDatabase;
    private SleepMainDisplay text;

    private TextView mSleepReadName;
    private TextView mSleepReadBuilding;
    private TextView mSleepReadBuildingNumber;
    private TextView mSleepReadFrom;
    private TextView mSleepReadTo;
    private TextView mSleepReadDestination;
    private TextView mSleepReadReason;
    private Spinner mSleepReadSpinner;

    private User writeUser;

    private ProgressDialog mSleepDeleteProgress;
    private ProgressDialog mSleepLoadDialog;

    private String nickname;
    private String mClass;
    private String text_id;
    private String current_user_id;
    private String user_id;

    private User user,writer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_read);

        mSleepLoadDialog = new ProgressDialog(this);
        mSleepLoadDialog.setTitle("로딩중");
        mSleepLoadDialog.setMessage("잠시만 기다려주세요.");
        mSleepLoadDialog.show();

        mToolbar = findViewById(R.id.sleep_read_toolbar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.google_back_white);
        mTitle.setText("외박 신청");
        mTitle.setTextColor(Color.WHITE);

        mSleepReadSpinner = findViewById(R.id.sleep_read_spinner);
        mSleepReadName = findViewById(R.id.sleep_read_name);
        mSleepReadBuilding = findViewById(R.id.sleep_read_building);
        mSleepReadBuildingNumber = findViewById(R.id.sleep_read_building_number);
        mSleepReadFrom = findViewById(R.id.sleep_read_from);
        mSleepReadTo = findViewById(R.id.sleep_read_to);
        mSleepReadDestination = findViewById(R.id.sleep_read_destination);
        mSleepReadReason = findViewById(R.id.sleep_read_reason);

        user_id = getIntent().getStringExtra("user_id");
        text_id = getIntent().getStringExtra("text_id");

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        current_user_id = current_user.getUid();

        mSleepDatabase = FirebaseDatabase.getInstance().getReference().child("Sleep").child(text_id);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

        //관리자인지 확인
        ValueEventListener currentUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(User.class);
                nickname = user.getNickname();
                mClass = user.getmClass();

                if((mClass.equals("관리자") )){
                    mSleepReadSpinner.setEnabled(true);
                }else{
                    mSleepReadSpinner.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                mSleepLoadDialog.dismiss();
                Toast.makeText(getApplicationContext(), databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addValueEventListener(currentUserListener);

        //write text
        //텍스트 입력하기
        mSleepWriterDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {

                    //닉네임 입력하기 시작
                    ValueEventListener writeUserListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get Post object and use the values to update the UI
                            writer = dataSnapshot.getValue(User.class);
                            String writerName = writer.getName();
                            mSleepReadName.setText(writerName);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Toast.makeText(getApplicationContext(), databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    };
                    mSleepWriterDatabase.addValueEventListener(writeUserListener);
                    //닉네임 입력하기 끝


                    text = dataSnapshot.getValue(SleepMainDisplay.class);
                    mSleepReadBuilding.setText(text.getBuilding());
                    mSleepReadBuildingNumber.setText(text.getBuildingNumber());
                    mSleepReadFrom.setText(text.getFrom());
                    mSleepReadTo.setText(text.getTo());
                    mSleepReadDestination.setText(text.getDestination());
                    mSleepReadReason.setText(text.getReason());

                    //image 불러오기

                    final String mStatus = text.getStatus().toString();
                    switch (mStatus) {
                        case "미확인":
                            mSleepReadSpinner.setBackgroundColor(Color.parseColor("#fafafa"));
                            break;
                        case "확인":
                            mSleepReadSpinner.setBackgroundColor(Color.parseColor("#b4eeb4"));
                            break;
                        case "거절":
                            mSleepReadSpinner.setBackgroundColor(Color.parseColor("#e96e67"));
                            break;
                    }

                    mSleepLoadDialog.dismiss();
                    ArrayAdapter<CharSequence> sleepAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.sleep_array, R.layout.sleep_spinner_item);
                    sleepAdapter.setDropDownViewResource(R.layout.sleep_spinner_item);
                    mSleepReadSpinner.setAdapter(sleepAdapter);
                    if (!mStatus.equals(null)) {
                        int spinnerPosition = sleepAdapter.getPosition(mStatus);
                        mSleepReadSpinner.setSelection(spinnerPosition);
                    }

                    // ...
                    mSleepReadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String mStatus_update = mStatus;
                            switch (position) {
                                case 0:
                                    mStatus_update = "미확인";
                                    break;
                                case 1:
                                    mStatus_update = "확인";

                                    break;
                                case 2:
                                    mStatus_update = "거절";
                                    break;
                            }
                            updateStatus(mStatus_update);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                mSleepLoadDialog.dismiss();
                Toast.makeText(SleepReadActivity.this, databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                // ...
            }
        };
        mSleepDatabase.addValueEventListener(userListener);

    }

    private void updateStatus(final String status) {
        mSleepDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                }else{
                    Toast.makeText(SleepReadActivity.this,"상태가 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sleep_read_bar, menu);
        if (!current_user_id.equals(user_id)){
            menu.findItem(R.id.action_sleep_read_delete).setVisible(false);
        }

        return true;
    }

    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
            case R.id.action_sleep_read_delete:
                deleteSleep();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteSleep() {
        final AlertDialog alertDialog = new AlertDialog.Builder(SleepReadActivity.this)
                .setTitle("삭제")
                .setMessage("작성한 글을 삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSleepDeleteProgress = new ProgressDialog(SleepReadActivity.this);
                        mSleepDeleteProgress.setTitle("삭제중");
                        mSleepDeleteProgress.setMessage("잠시만 기다려 주세요.");
                        mSleepDeleteProgress.setCanceledOnTouchOutside(false);
                        mSleepDeleteProgress.show();
                        mSleepDatabase.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mSleepDeleteProgress.dismiss();
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                }else{
                                    mSleepDeleteProgress.dismiss();
                                    Toast.makeText(SleepReadActivity.this, "삭제되지 않았습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
            }
        });
        alertDialog.show();
    }


}
