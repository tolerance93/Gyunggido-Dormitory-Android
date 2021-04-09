package org.androidtown.dormitory.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.androidtown.dormitory.R;
import org.androidtown.dormitory.model.RepairComment;
import org.androidtown.dormitory.display.RepairMainDisplay;
import org.androidtown.dormitory.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class RepairReadActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private DatabaseReference mRepairDatabase;
    private DatabaseReference mRepairWriterDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference mRepairCommentDatabase;
    private DatabaseReference mUserDatabase;
    private RepairMainDisplay text;

    private TextView mRepairReadTitle;
    private TextView mRepairReadOpen;
    private TextView mRepairReadNickname;
    private TextView mRepairReadDate;
    private TextView mRepairReadContents;
    private EditText mRepairReadComments;
    private Button mRepairReadSubmitBtn;
    private Spinner mRepairReadSpinner;

    private User writeUser;

    private ImageView mRepairReadImage;

    private RecyclerView mRepairCommentList;

    private LinearLayout mReadTitleLayout;

    private ProgressDialog mRepairCommentProgress;
    private ProgressDialog mRepairDeleteProgress;
    private ProgressDialog mRepairLoadDialog;

    private String imageUri = "";


    private String nickname;
    private String mClass;
    private String text_id;
    private String current_user_id;
    private String user_id;

    private User user,writer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_read);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.BLACK);
        }

        mRepairLoadDialog = new ProgressDialog(this);
        mRepairLoadDialog.setTitle("로딩중");
        mRepairLoadDialog.setMessage("잠시만 기다려주세요.");
        mRepairLoadDialog.show();


        mToolbar = findViewById(R.id.repair_read_appBar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.google_back);
        mTitle.setText("수리 요청");
        mTitle.setTextColor(Color.BLACK);

        mRepairCommentList = (RecyclerView) findViewById(R.id.repair_write_comments_list);
        mRepairCommentList.setNestedScrollingEnabled(false);
        //mRepairCommentList.setHasFixedSize(true);
        mRepairCommentList.setLayoutManager(new LinearLayoutManager(this));

        mRepairReadComments = (EditText) findViewById(R.id.repair_read_comments);
        mRepairReadSubmitBtn = (Button) findViewById(R.id.repair_read_submit_btn);

        mReadTitleLayout = (LinearLayout) findViewById(R.id.repair_read_title_layout);

        mRepairReadImage = (ImageView) findViewById(R.id.repair_read_image_view);

        mRepairReadTitle = (TextView) findViewById(R.id.repair_read_title);
        mRepairReadContents = (TextView) findViewById(R.id.repair_read_contents);
        mRepairReadOpen = findViewById(R.id.repair_read_open);
        mRepairReadNickname = findViewById(R.id.repair_read_nickname);
        mRepairReadDate = findViewById(R.id.repair_read_date);
        mRepairReadSpinner = (Spinner) findViewById(R.id.repair_read_spinner);

        //작성자 uid, 글id
        user_id = getIntent().getStringExtra("user_id");
        text_id = getIntent().getStringExtra("text_id");

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        current_user_id = current_user.getUid();

        mRepairDatabase = FirebaseDatabase.getInstance().getReference().child("Repair").child(text_id);
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
                    mRepairReadSpinner.setEnabled(true);
                }else{
                    mRepairReadSpinner.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(getApplicationContext(), databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addValueEventListener(currentUserListener);

        //댓글 입력하기
        mRepairReadSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comments = mRepairReadComments.getText().toString();
                commentsUpdate(comments, current_user_id, text_id);
                InputMethodManager inputManager = (InputMethodManager)
                        getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(mRepairReadComments.getApplicationWindowToken(), 0);
                onStart();
            }
        });


        //텍스트 입력하기
        mRepairWriterDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
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
                            String writerNickname = writer.getNickname();
                            mRepairReadNickname.setText(writerNickname);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Toast.makeText(getApplicationContext(), databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    };
                    mRepairWriterDatabase.addValueEventListener(writeUserListener);
                    //닉네임 입력하기 끝


                    text = dataSnapshot.getValue(RepairMainDisplay.class);
                    mRepairReadTitle.setText(text.getTitle());
                    mRepairReadOpen.setText(text.getOpen());
                    if (text.getOpen().equals("공개")){
                        mRepairReadOpen.setBackgroundColor(Color.parseColor("#1276d6"));
                    } else if (text.getOpen().equals("비공개")){
                        mRepairReadOpen.setBackgroundColor(Color.parseColor("#ff5733"));
                    }
                    mRepairReadDate.setText(text.getTime());
                    mRepairReadContents.setText(text.getContents());
                    imageUri = text.getImageUrl();

                    //image 불러오기
                    if(!imageUri.equals("")){
                        Picasso.get().load(imageUri).into(mRepairReadImage, new Callback(){
                            @Override
                            public void onSuccess() {
                                mRepairLoadDialog.dismiss();
                            }

                            @Override
                            public void onError(Exception e) {
                                mRepairLoadDialog.dismiss();
                                Toast.makeText(RepairReadActivity.this,"사진을 불러올 수 없습니다.",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        mRepairLoadDialog.dismiss();
                    }

                    final String mStatus = text.getStatus().toString();
                    switch (mStatus) {
                        case "미확인":
                            mRepairReadSpinner.setBackgroundColor(Color.parseColor("#fafafa"));
                            break;
                        case "수리중":
                            mRepairReadSpinner.setBackgroundColor(Color.parseColor("#ffec8b"));
                            break;
                        case "완료":
                            mRepairReadSpinner.setBackgroundColor(Color.parseColor("#b4eeb4"));
                            break;
                    }


                    ArrayAdapter<CharSequence> repairAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.repair_array, R.layout.repair_spinner_item);
                    repairAdapter.setDropDownViewResource(R.layout.repair_spinner_item);
                    mRepairReadSpinner.setAdapter(repairAdapter);
                    if (!mStatus.equals(null)) {
                        int spinnerPosition = repairAdapter.getPosition(mStatus);
                        mRepairReadSpinner.setSelection(spinnerPosition);
                    }

                    // ...
                    mRepairReadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String mStatus_update = mStatus;
                            switch (position) {
                                case 0:
                                    mStatus_update = "미확인";

                                    break;
                                case 1:
                                    mStatus_update = "수리중";

                                    break;
                                case 2:
                                    mStatus_update = "완료";
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
                Toast.makeText(RepairReadActivity.this, databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                // ...
            }
        };
        mRepairDatabase.addValueEventListener(userListener);

        //이미지 확대
        mRepairReadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imageUri.equals("")){
                    Intent intent = new Intent(RepairReadActivity.this, ImageZoomActivity.class);
                    intent.putExtra("image_uri",imageUri);
                    startActivity(intent);
                    overridePendingTransition(R.anim.below_in, R.anim.below_out);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query repairDatabaseQuery;
        repairDatabaseQuery = FirebaseDatabase.getInstance().getReference().child("RepairComments").child(text_id).orderByChild("orderTime");

        FirebaseRecyclerAdapter<RepairComment, RepairCommentViewHolder> firebaseCommentRecyclerAdapter = new FirebaseRecyclerAdapter<RepairComment, RepairCommentViewHolder>(
                RepairComment.class,
                R.layout.repair_comments_layout,
                RepairCommentViewHolder.class,
                repairDatabaseQuery
        ) {
            @Override
            protected void populateViewHolder(final RepairCommentViewHolder viewHolder, RepairComment model, int position) {

                System.out.println("wow   " + model.getUid());
                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(model.getUid());
                ValueEventListener userListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        writeUser = dataSnapshot.getValue(User.class);
                        viewHolder.setNickname(writeUser.getNickname());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Toast.makeText(getApplicationContext(), databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
                    }
                };
                mUserDatabase.addValueEventListener(userListener);
                viewHolder.setDate(model.getTime());
                viewHolder.setContent(model.getText());

            }
        };
        mRepairCommentList.setAdapter(firebaseCommentRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repair_read_bar, menu);
        if (!current_user_id.equals(user_id)){
            menu.findItem(R.id.action_repair_read_delete).setVisible(false);
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
            case R.id.action_repair_read_delete:
                deleteRepair();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteRepair() {
        final AlertDialog alertDialog = new AlertDialog.Builder(RepairReadActivity.this)
                .setTitle("삭제")
                .setMessage("작성한 글을 삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRepairDeleteProgress = new ProgressDialog(RepairReadActivity.this);
                        mRepairDeleteProgress.setTitle("삭제중");
                        mRepairDeleteProgress.setMessage("잠시만 기다려 주세요.");
                        mRepairDeleteProgress.setCanceledOnTouchOutside(false);
                        mRepairDeleteProgress.show();
                        mRepairDatabase.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mRepairDeleteProgress.dismiss();
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                }else{
                                    mRepairDeleteProgress.dismiss();
                                    Toast.makeText(RepairReadActivity.this, "삭제되지 않았습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();

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


    private void commentsUpdate(String comments, String myUid, String textId) {
        mRepairCommentProgress = new ProgressDialog(this);
        mRepairCommentProgress.setTitle("로딩중");
        mRepairCommentProgress.setMessage("잠시만 기다려주세요.");
        mRepairCommentProgress.show();

        final HashMap<String, Object> commentMap = new HashMap<String, Object>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd HH:mm");
        String currentDateandTime = sdf.format(new Date());

        commentMap.put("text",comments);
        commentMap.put("uid",myUid);
        commentMap.put("time",currentDateandTime.toString());
        commentMap.put("orderTime", Double.valueOf((System.currentTimeMillis())));
        mRepairCommentDatabase = FirebaseDatabase.getInstance().getReference().child("RepairComments").child(textId).push();
        final String commentTextKey = mRepairCommentDatabase.getKey();

        mRepairCommentDatabase.setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mRepairReadComments.setText("");
                    mRepairCommentProgress.dismiss();
                }else{
                    mRepairCommentProgress.dismiss();
                    Toast.makeText(RepairReadActivity.this,"댓글이 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void updateStatus(final String status) {
        mRepairDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                }else{
                    Toast.makeText(RepairReadActivity.this,"상태가 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class RepairCommentViewHolder extends RecyclerView.ViewHolder{
        View mView;
        private TextView repairCommentContent;
        private TextView repairCommentDate;
        private TextView repairCommentNickname;

        public RepairCommentViewHolder(View itemView){
            super(itemView);
            mView = itemView;

        }
        public void setContent(String content){
            repairCommentContent = (TextView) mView.findViewById(R.id.repair_comments_content);
            repairCommentContent.setText(content);
        }
        public void setNickname(String nickname){
            repairCommentNickname = (TextView) mView.findViewById(R.id.repair_comments_nickname);
            repairCommentNickname.setText(nickname);
        }
        public void setDate(String date){
            repairCommentDate = (TextView) mView.findViewById(R.id.repair_comments_date);
            repairCommentDate.setText(date);
        }

    }
}
